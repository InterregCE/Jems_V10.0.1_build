package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.scope.getCorrectionAvailableProcurements

import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewAuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetCorrectionAvailableProcurements(
    private val auditControlCorrectionPersistence: AuditControlCorrectionPersistence,
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportProcurementPersistence: ProjectPartnerReportProcurementPersistence,
) : GetCorrectionAvailableProcurementsInteractor {

    @CanViewAuditControlCorrection
    @Transactional
    @ExceptionWrapper(GetCorrectionAvailableProcurementsException::class)
    override fun getAvailableProcurements(correctionId: Long): List<IdNamePair> {
        val correction = auditControlCorrectionPersistence.getByCorrectionId(correctionId)

        if (!correction.isPartnerRepostSet()) {
            throw MandatoryScopeNotSetException()
        }

        val previousReportIds = with(correction) {
            reportPersistence.getReportIdsBefore(partnerId = partnerId!!, beforeReportId = partnerReportId!!)
        }


        return reportProcurementPersistence.getProcurementsForReportIds(
            reportIds = previousReportIds.plus(correction.partnerReportId!!),
            pageable = Pageable.unpaged()
        ).content.map { IdNamePair(it.id, it.contractName) }

    }

}