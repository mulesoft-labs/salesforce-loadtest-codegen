String sfdcdate = message.getOutboundProperty("sfdcmodifieddate");
sfdcdate = sfdcdate.replace('T', ' ');
sfdcdate = sfdcdate.substring(0, sfdcdate.indexOf('+'));
message.setOutboundProperty("sfdcmodifieddate", sfdcdate);

return payload;
