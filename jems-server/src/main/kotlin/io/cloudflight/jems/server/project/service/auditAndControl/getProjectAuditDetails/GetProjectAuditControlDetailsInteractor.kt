package io.cloudflight.jems.server.project.service.auditAndControl.getProjectAuditDetails

import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectAuditControl

interface GetProjectAuditControlDetailsInteractor {

    fun getDetails(projectId: Long, auditId: Long): ProjectAuditControl
}