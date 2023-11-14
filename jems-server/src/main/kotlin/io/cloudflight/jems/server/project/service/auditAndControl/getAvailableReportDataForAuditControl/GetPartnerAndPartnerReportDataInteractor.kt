package io.cloudflight.jems.server.project.service.auditAndControl.getAvailableReportDataForAuditControl

import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartner


interface GetPartnerAndPartnerReportDataInteractor {

    fun getPartnerAndPartnerReportData(
        projectId: Long,
    ): List<CorrectionAvailablePartner>

}
