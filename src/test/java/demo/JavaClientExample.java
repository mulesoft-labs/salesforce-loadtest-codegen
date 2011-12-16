package demo;

import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.bayeux.client.ClientSessionChannel.MessageListener;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.ClientTransport;
import org.cometd.client.transport.LongPollingTransport;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JavaClientExample {
    // Skips certificate validation in the case of logging proxies for debugging
    private static final boolean NO_VALIDATION = false;

    // The long poll duration
    private static final int TIMEOUT = 120 * 1000;

    // Get credentials etc from environment
    private static final String LOGIN_SERVER = "https://login.salesforce.com";
    private static final String USERNAME = "testuser2@mulesource.com";
    private static final String PASSWORD = "mule2demo";

    private static final String CLIENT_ID = "3MVG9QDx8IX8nP5Qo89z0CzekjL2VgKaHoVuqg_sk0_61YA8MgsFSO8rqp.QEoKq9wCLAK2QBKTDc_WtY3eSD";
    private static final String CLIENT_SECRET = "5407559371249260101";

    // The path for the Streaming API endpoint
    private static final String DEFAULT_PUSH_ENDPOINT = "/cometd/23.0";

    public static void main(String[] args) throws Exception {
        BayeuxClient client = null;

        if (args.length < 1) {
        	args = new String[1];
        	args[0] ="AllAccounts";
        }

        String topic = args[0];

        System.out.println("Running example....");
        if (NO_VALIDATION) {
            setNoValidation();
        }

        client = getClient();
        client.handshake();

        System.out.println("Waiting for handshake");
        waitForHandshake(client, 60 * 1000, 1000);

        System.out.println("Subscribing to topic: " + topic);
        client.getChannel("/topic/" + topic).subscribe(new MessageListener() {
            @Override
            public void onMessage(ClientSessionChannel channel, Message message) {
                try {
                    System.out.println("Received Message: "
                            + (new JSONObject(
                                    new JSONTokener(message.getJSON())))
                                    .toString(2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("Waiting for streamed data from Force.com...");
        while (true) {
            // This infinite loop is for demo only, to receive streamed events
            // on the specified topic from Salesforce.com
            Thread.sleep(TIMEOUT);
        }
    }

    private static BayeuxClient getClient() throws Exception {
        // Authenticate via OAuth
        JSONObject response = oauthLogin();
        System.out.println("Login response: " + response.toString(2));
        if (!response.has("access_token")) {
            throw new Exception("OAuth failed: " + response.toString());
        }

        // Get what we need from the OAuth response
        final String sid = response.getString("access_token");
        String instance_url = response.getString("instance_url");

        // Set up a Jetty HTTP client to use with CometD
        HttpClient httpClient = new HttpClient();
        httpClient.setConnectTimeout(TIMEOUT);
        httpClient.setTimeout(TIMEOUT);
        httpClient.start();
        
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(ClientTransport.TIMEOUT_OPTION, TIMEOUT);
        
        // Adds the OAuth header in LongPollingTransport
        LongPollingTransport transport = new LongPollingTransport(
                options, httpClient) {
            @Override 
            protected void customize(ContentExchange exchange) {
                super.customize(exchange);
                exchange.addRequestHeader("Authorization", "OAuth " + sid);
            }
        };

        // Now set up the Bayeux client itself
        BayeuxClient client = new BayeuxClient(instance_url
                + DEFAULT_PUSH_ENDPOINT, transport);

        return client;
    }

    private static void waitForHandshake(BayeuxClient client,
            long timeoutInMilliseconds, long intervalInMilliseconds) {
        long start = System.currentTimeMillis();
        long end = start + timeoutInMilliseconds;
        while (System.currentTimeMillis() < end) {
            if (client.isHandshook())
                return;
            try {
                Thread.sleep(intervalInMilliseconds);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException("Client did not handshake with server");
    }

    public static void setNoValidation() throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs,
                    String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs,
                    String authType) {
            }
        } };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    private static JSONObject oauthLogin() throws Exception {
        HttpClient httpClient = new HttpClient();
        httpClient.start();

        String url = LOGIN_SERVER + "/services/oauth2/token";

        ContentExchange exchange = new ContentExchange();
        exchange.setMethod("POST");
        exchange.setURL(url);

        String message = "grant_type=password&client_id=" + CLIENT_ID
                + "&client_secret=" + CLIENT_SECRET + "&username=" + USERNAME
                + "&password=" + PASSWORD;

        exchange.setRequestHeader("Content-Type",
                "application/x-www-form-urlencoded");
        exchange.setRequestContentSource(new ByteArrayInputStream(message
                .getBytes("UTF-8")));

        httpClient.send(exchange);
        exchange.waitForDone();

        return new JSONObject(new JSONTokener(exchange.getResponseContent()));

    }
}
