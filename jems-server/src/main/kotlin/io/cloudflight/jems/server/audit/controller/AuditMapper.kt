package io.cloudflight.jems.server.audit.controller

import io.cloudflight.jems.api.audit.dto.AuditDTO
import io.cloudflight.jems.api.audit.dto.AuditProjectDTO
import io.cloudflight.jems.api.audit.dto.AuditSearchRequestDTO
import io.cloudflight.jems.api.audit.dto.AuditUserDTO
import io.cloudflight.jems.server.audit.model.Audit
import io.cloudflight.jems.server.audit.model.AuditFilter
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.model.AuditSearchRequest
import io.cloudflight.jems.server.audit.model.AuditUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

fun Page<Audit>.toDto() = map {
    AuditDTO(
        timestamp = it.timestamp,
        action = it.action,
        project = it.project?.tDto(),
        user = it.user.tDto(),
        description = it.description,
    )
}

fun AuditUser?.tDto() = let { user ->
    if (user == null) null else AuditUserDTO(
        id = user.id,
        email = user.email,
    )
}

fun AuditProject.tDto() = AuditProjectDTO(
    id = id,
    customIdentifier = customIdentifier,
    name = name
)

fun AuditSearchRequestDTO?.toModel(pageable: Pageable) =
    if (this == null)
        AuditSearchRequest(
            pageable = pageable
        )
    else
        AuditSearchRequest(
            userId = AuditFilter(values = userIds.filterNotNullTo(HashSet())),
            userEmail = AuditFilter(values = userEmails.filterNotNullTo(HashSet())),
            action = AuditFilter(values = actions.map { it?.name }.filterNotNullTo(HashSet())),
            projectId = AuditFilter(values = projectIds.filterNotNullTo(HashSet())),
            timeFrom = timeFrom,
            timeTo = timeTo,
            pageable = pageable,
        )
