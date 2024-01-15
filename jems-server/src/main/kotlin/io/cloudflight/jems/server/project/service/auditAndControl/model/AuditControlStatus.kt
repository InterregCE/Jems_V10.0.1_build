package io.cloudflight.jems.server.project.service.auditAndControl.model

enum class AuditControlStatus {
    Ongoing,
    Closed;

    fun isClosed() = this == Closed

}
