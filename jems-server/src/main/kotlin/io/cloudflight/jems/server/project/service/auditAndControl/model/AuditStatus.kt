package io.cloudflight.jems.server.project.service.auditAndControl.model

enum class AuditStatus(val key: String) {
    Ongoing("Ongoing"),
    Closed("Closed");

    fun isClosed() = this == Closed
}
