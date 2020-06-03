package io.cloudflight.ems.service

import io.cloudflight.ems.entity.Audit

interface AuditService {
    fun logEvent(event: Audit)
}
