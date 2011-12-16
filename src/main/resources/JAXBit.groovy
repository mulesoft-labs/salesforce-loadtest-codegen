import javax.xml.bind.*;
import javax.xml.namespace.QName;

JAXBContext context = JAXBContext.newInstance(payload.getClass());
StringWriter sw = new StringWriter();
context.createMarshaller().marshal(new JAXBElement(new QName("response"),payload.class,payload),sw);
return sw.toString();