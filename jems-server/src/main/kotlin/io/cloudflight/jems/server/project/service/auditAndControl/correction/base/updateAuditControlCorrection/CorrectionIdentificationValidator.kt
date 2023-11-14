package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.updateAuditControlCorrection

import io.cloudflight.jems.server.project.repository.report.partner.expenditure.ProjectPartnerReportExpenditurePersistenceProvider
import io.cloudflight.jems.server.project.repository.report.partner.procurement.ProjectPartnerReportProcurementPersistenceProvider
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.getAvailableReportDataForAuditControl.GetPartnerAndPartnerReportDataService
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControl
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.CorrectionAvailablePartner
import org.springframework.stereotype.Service

@Service
class CorrectionIdentificationValidator(
    private val auditControlPersistence: AuditControlPersistence,
    private val auditControlCorrectionPersistence: AuditControlCorrectionPersistence,
    private val allowedDataService: GetPartnerAndPartnerReportDataService,
    private val partnerReportExpenditurePersistenceProvider: ProjectPartnerReportExpenditurePersistenceProvider,
    private val partnerReportProcurementPersistence: ProjectPartnerReportProcurementPersistenceProvider,
) {

    fun validate(
        correctionId: Long,
        correctionUpdate: AuditControlCorrectionUpdate,
    ) {

        val oldCorrection = auditControlCorrectionPersistence.getByCorrectionId(correctionId)
        val auditControl = auditControlPersistence.getById(oldCorrection.auditControlId)

        validateAuditControlNotClosed(auditControl)
        validateAuditControlCorrectionNotClosed(oldCorrection)

        validateCorrectionScope(correctionUpdate, oldCorrection, auditControl)
    }

    fun validateCorrectionScope(
        correctionUpdate: AuditControlCorrectionUpdate,
        correction: AuditControlCorrectionDetail,
        auditControl: AuditControl
    ) {
        val allowedReportData = allowedDataService.getPartnerAndPartnerReportData(projectId = auditControl.projectId)
        validateReportAndFundSelectedAreValid(correctionUpdate, availableData = allowedReportData)

        validateOptionalScope(
            updatedData = correctionUpdate,
            correction = correction,
            allowedReportData = allowedReportData
        )

    }

    private fun validateLinkedCorrectionScope(correctionUpdate: AuditControlCorrectionUpdate, correction: AuditControlCorrectionDetail) {
        if (correctionUpdate.expenditureId != null) {
            checkExpenditureIsValidForSelectedPartnerReport(correctionUpdate, correction)
        }
        checkCategoryAndProcurementIsNull(correctionUpdate)
    }

    private fun checkExpenditureIsValidForSelectedPartnerReport(correctionUpdate: AuditControlCorrectionUpdate, correction: AuditControlCorrectionDetail) {
        val exists = partnerReportExpenditurePersistenceProvider.existsByExpenditureId(
            partnerId = correction.partnerId!!,
            reportId = correctionUpdate.partnerReportId,
            expenditureId = correctionUpdate.expenditureId!!
        )
        if (exists.not()) {
             throw ExpenditureNotValidException()
        }
    }

    private fun checkCategoryAndProcurementIsNull(correctionIdentification: AuditControlCorrectionUpdate) {
        if (correctionIdentification.costCategory != null || correctionIdentification.procurementId != null) {
             throw InvalidCorrectionScopeException()
        }
    }


    private fun validateNotLinkedCorrectionScope(
        correctionIdentificationUpdate: AuditControlCorrectionUpdate,
        allowedReportData: List<CorrectionAvailablePartner>
    ) {
        checkExpenditureIsNull(correctionIdentificationUpdate)

        if (correctionIdentificationUpdate.procurementId != null) {
            val partnerDataForSelectedReport =
                allowedReportData.firstOrNull { partnerData ->
                    partnerData.availableReports.firstOrNull { it.id == correctionIdentificationUpdate.partnerReportId } != null
                }
            val partnerAvailableReportIds = partnerDataForSelectedReport?.availableReports?.map { it.id }?.toSet() ?: emptySet()

            checkProcurementIsValidForSelectedReport(
                correctionIdentificationUpdate.procurementId,
                partnerAvailableReportIds
            )
        }

    }

    private fun checkProcurementIsValidForSelectedReport(procurementId: Long, reportIds: Set<Long>) {
        val exists = partnerReportProcurementPersistence.existsByProcurementIdAndPartnerReportIdIn(
            procurementId = procurementId,
            partnerReportIds = reportIds
        )
        if (exists.not()) {
             throw ProcurementNotValidException()
        }
    }

    private fun checkExpenditureIsNull(correctionIdentification: AuditControlCorrectionUpdate) {
        if (correctionIdentification.expenditureId != null ) {
            throw InvalidCorrectionScopeException()
        }
    }


    private fun validateAuditControlNotClosed(auditControl: AuditControl) {
        if (auditControl.status.isClosed())
            throw AuditControlClosedException()
    }

    private fun validateAuditControlCorrectionNotClosed(correction: AuditControlCorrectionDetail) {
        if (correction.status.isClosed())
            throw AuditControlCorrectionClosedException()
    }


    private fun validateReportAndFundSelectedAreValid(input: AuditControlCorrectionUpdate, availableData: List<CorrectionAvailablePartner>) {
        availableData.flatMap { it.availableReports }
            .firstOrNull { it.id == input.partnerReportId }?.availableReportFunds?.firstOrNull { it.id == input.programmeFundId }
            ?: throw CombinationOfReportAndFundIsInvalidException()
    }

    private fun validateOptionalScope(
        updatedData: AuditControlCorrectionUpdate,
        correction: AuditControlCorrectionDetail,
        allowedReportData: List<CorrectionAvailablePartner>
    ) {
        if (correction.type == AuditControlCorrectionType.LinkedToInvoice) {
            validateLinkedCorrectionScope(updatedData, correction)
        } else {
            validateNotLinkedCorrectionScope(updatedData, allowedReportData)
        }
    }

}