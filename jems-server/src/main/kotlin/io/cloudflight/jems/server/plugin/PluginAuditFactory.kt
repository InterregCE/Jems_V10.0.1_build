package io.cloudflight.jems.server.plugin

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.common.event.JemsAuditEvent


fun pluginCalled(name: String, key: String, methodName: String) =
    JemsAuditEvent(
        auditCandidate = AuditBuilder(AuditAction.PLUGIN_CALLED)
            .description("plugin with name:'$name' and key: '$key' was called. (method name: $methodName)")
            .build()
    )


fun pluginExecuted(name: String, key: String, methodName: String) =
    JemsAuditEvent(
        auditCandidate = AuditBuilder(AuditAction.PLUGIN_EXECUTED)
            .description("plugin with name:'$name' and key: '$key' was executed. (method name: $methodName)")
            .build()
    )
