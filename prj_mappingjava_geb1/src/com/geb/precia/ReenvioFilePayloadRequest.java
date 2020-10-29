package com.geb.precia;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.ecopetrol.transfiriendo.util.AuditLogHelper;
import com.ecopetrol.transfiriendo.util.GlobalVariable;
import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;
import com.sap.aii.mapping.lookup.Channel;
import com.sap.aii.mapping.lookup.LookupService;
import com.sap.aii.mapping.lookup.Payload;
import com.sap.aii.mapping.lookup.SystemAccessor;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class ReenvioFilePayloadRequest extends AbstractTransformation {

//	private static final DynamicConfigurationKey KEY_FILENAME = DynamicConfigurationKey
//			.create("http://sap.com/xi/XI/System/File", "FileName");

	private AuditLogHelper logger = null;

	@Override
	public void transform(TransformationInput in, TransformationOutput out) throws StreamTransformationException {

		try {
			logger = AuditLogHelper.getInstance(in.getInputHeader().getMessageId());
		} catch (Exception e) {

			e.printStackTrace();
		}
		String RESULT = new String();
		if (logger != null) {
			logger.log("Inicio del Java Mapping - ReenvioFilePayloadRequest", AuditLogStatus.SUCCESS);
		}
		try {

 

			InputStream inputStream = in.getInputPayload().getInputStream();
			InputStream inputStream2 = in.getInputPayload().getInputStream();
			OutputStream outputStream = out.getOutputPayload().getOutputStream();

 
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();           
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            ByteArrayOutputStream resultXml = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int lengthBytes;
            while ((lengthBytes = inputStream2.read(buffer)) != -1) {
            	resultXml.write(buffer, 0, lengthBytes);
            }
 

			String bs = in.getInputParameters().getString("BS_PARAM");
			String cn = in.getInputParameters().getString("CN_PARAM");

			if (bs == null)
				bs = "FILESERVER";
			if (cn == null)
				cn = "SFTPFILEReceiver";

			String msgID = (String) in.getInputHeader().getMessageId();
			
			GlobalVariable.BlockId = msgID;

 

			RESULT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns2:MT_RESTIN_V01 xmlns:ns2=\"urn:std:restlookup\">"
					+ "<MessageId>" + msgID + "</MessageId>";
			RESULT +="<Archivo><![CDATA[";

			RESULT += resultXml.toString("UTF-8");

			RESULT +=" ]]></Archivo>";
			RESULT += "</ns2:MT_RESTIN_V01>";

			if (logger != null) {
				
				logger.log(RESULT, AuditLogStatus.SUCCESS);
			}

			Channel channel = LookupService.getChannel(bs, cn);

			SystemAccessor accessor = null;
			accessor = LookupService.getSystemAccessor(channel);

			// InputStream inputStreamFinal = new ByteArrayInputStream(b);
			InputStream inputStreamFinal = new ByteArrayInputStream(RESULT.getBytes());
			Payload requestPayload = LookupService.getXmlPayload(inputStreamFinal);

			Payload responsePayload = accessor.call(requestPayload);
			InputStream responseInputStream = responsePayload.getContent();
			if (logger!=null) {
			   logger.log("Escribiendo el archivo  "+msgID, AuditLogStatus.SUCCESS);
			}

			byte[] b = new byte[inputStream.available()];
 			
// 			logger.log(String.valueOf(inputStream.available()), AuditLogStatus.SUCCESS);
 			inputStream.read(b);
 			outputStream.write(b);
 			
		} catch (Exception exception) {

			if (logger != null) {
				 
				logger.log(exception.getMessage(), AuditLogStatus.ERROR);
			}

		}
		if (logger != null) {
			 
			logger.log("Fin del Java Mapping - ReenvioFilePayloadRequest", AuditLogStatus.SUCCESS);
		}
	}

	

}
