package io.cloudflight.jems.server.project.service.auditAndControl.correction.model

enum class CorrectionStatus(val key: String) {
    Ongoing("Ongoing"),
    Closed("Closed");

    fun isOngoing() = this == Ongoing

    fun isClosed() = this == Closed
}
