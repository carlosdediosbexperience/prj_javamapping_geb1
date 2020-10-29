package com.geb.precia;

import com.sap.engine.interfaces.messaging.api.MessageDirection;
import com.sap.engine.interfaces.messaging.api.MessageKey;
import com.sap.engine.interfaces.messaging.api.PublicAPIAccessFactory;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditAccess;
import com.sap.engine.interfaces.messaging.api.auditlog.AuditLogStatus;

public class AuditLogHelper {
private static final String DASH = "-";
	
	private static boolean isInitialize = false;
	
	private static AuditAccess auditAccess;

	private MessageKey messageKey;
	
	private static void initialize() throws Exception {
		auditAccess = PublicAPIAccessFactory.getPublicAPIAccess().getAuditAccess();
	}
	
	private AuditLogHelper(MessageKey messageKey) throws Exception {
		this.messageKey = messageKey;
	}

	public static AuditLogHelper getInstance(String messageId) throws Exception {
		
		if(!isInitialize)
			initialize();
		
		String msgUUID = messageId.substring(0, 8) + DASH + messageId.substring(8, 12) + DASH
				+ messageId.substring(12, 16) + DASH + messageId.substring(16, 18) + messageId.substring(18, 20) + DASH
				+ messageId.substring(20, 32);
		
		MessageKey messageKey = new MessageKey(msgUUID, MessageDirection.OUTBOUND);
		
		return new AuditLogHelper(messageKey);
	}

	public void log(String message, AuditLogStatus status) {
		auditAccess.addAuditLogEntry(messageKey, status, message);
	}

	public void setMessageKey(MessageKey messageKey) {
		this.messageKey = messageKey;
	}
}

