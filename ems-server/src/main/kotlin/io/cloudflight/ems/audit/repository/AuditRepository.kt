package io.cloudflight.ems.audit.repository

import io.cloudflight.ems.config.AUDIT_ENABLED
import io.cloudflight.ems.config.AUDIT_PROPERTY_PREFIX
import io.cloudflight.ems.audit.entity.Audit
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnProperty(prefix = AUDIT_PROPERTY_PREFIX, name = [AUDIT_ENABLED], havingValue = "true")
interface AuditRepository : ElasticsearchRepository<Audit, Long>
