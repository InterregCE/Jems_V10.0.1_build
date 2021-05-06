package io.cloudflight.jems.server.plugin

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.service.AuditBuilder


fun pluginCalled(
    context: Any, name: String, key: String,methodName: String
): AuditCandidateEvent =
    AuditCandidateEvent(context,
        AuditBuilder(AuditAction.PLUGIN_CALLED).description("plugin with name:'$name' and key: '$key' was called. (method name: $methodName)")
            .build()
    )

fun pluginExecuted(
    context: Any, name: String, key: String, methodName: String
): AuditCandidateEvent =
    AuditCandidateEvent(context,
        AuditBuilder(AuditAction.PLUGIN_EXECUTED).description("plugin with name:'$name' and key: '$key' was executed. (method name: $methodName)")
            .build()
    )
