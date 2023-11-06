package io.cloudflight.jems.server.project.service.auditAndControl.getPartnerAndPartnerReportData

import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.CorrectionAvailablePartner


interface GetPartnerAndPartnerReportDataInteractor {

    fun getPartnerAndPartnerReportData(
        projectId: Long,
    ): List<CorrectionAvailablePartner>

}
