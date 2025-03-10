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
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import org.springframework.stereotype.Service

@Service
class CorrectionIdentificationValidator(
    private val auditControlPersistence: AuditControlPersistence,
    private val auditControlCorrectionPersistence: AuditControlCorrectionPersistence,
    private val allowedDataService: GetPartnerAndPartnerReportDataService,
    private val partnerReportExpenditurePersistenceProvider: ProjectPartnerReportExpenditurePersistenceProvider,
    private val partnerReportProcurementPersistence: ProjectPartnerReportProcurementPersistenceProvider,
    private val reportPersistence: ProjectPartnerReportPersistence,
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
        validatePartnerReportAndLumpSum(correctionUpdate, availableData = allowedReportData)
        validateSelectedFundCombinationValid(correctionUpdate, availableData = allowedReportData)

        validateOptionalScope(
            updatedData = correctionUpdate,
            correction = correction,
        )

    }

    private fun validatePartnerReportAndLumpSum(correctionUpdate: AuditControlCorrectionUpdate, availableData: List<CorrectionAvailablePartner>) {
        if (correctionUpdate.lumpSumOrderNr != null) {
            val validPartnerIds = availableData.map { it.partnerId }
            if (correctionUpdate.partnerId !in validPartnerIds)
                throw LumpSumAndPartnerNotValidException()
        } else {
            val validPartnerReportIds = availableData.flatMap { it.availableReports.map { report -> report.id } }
            if (correctionUpdate.partnerReportId !in validPartnerReportIds)
                throw PartnerReportNotValidException()
        }
    }

    private fun validateLinkedToInvoiceCorrectionScope(correctionUpdate: AuditControlCorrectionUpdate, correction: AuditControlCorrectionDetail) {
        if (correctionUpdate.expenditureId != null) {
            checkExpenditureIsValidForSelectedPartnerReport(correctionUpdate, correction)
        }
        checkCategoryAndProcurementIsNull(correctionUpdate)
    }

    private fun checkExpenditureIsValidForSelectedPartnerReport(correctionUpdate: AuditControlCorrectionUpdate, correction: AuditControlCorrectionDetail) {
        val exists = partnerReportExpenditurePersistenceProvider.existsByExpenditureId(
            partnerId = correction.partnerId!!,
            reportId = correctionUpdate.partnerReportId!!,
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


    private fun validateLinkedToCostOptionCorrectionScope(
        correctionUpdateData: AuditControlCorrectionUpdate,
        correction: AuditControlCorrectionDetail
    ) {
        checkExpenditureIsNull(correctionUpdateData)

        if (correctionUpdateData.procurementId != null) {
            checkProcurementIsValidForSelectedReport(
                correctionUpdateData.procurementId,
                correctionUpdateData.partnerReportId!!,
                correction.partnerId!!
            )
        }

    }

    private fun checkProcurementIsValidForSelectedReport(procurementId: Long, partnerReportId: Long, partnerId: Long) {
        val exists = partnerReportProcurementPersistence.existsByProcurementIdAndPartnerReportIdIn(
            procurementId = procurementId,
            partnerReportIds = reportPersistence.getReportIdsBefore(
                partnerId = partnerId,
                beforeReportId = partnerReportId
            ).plus(partnerReportId)
        )
        if (exists.not()) {
            throw ProcurementNotValidException()
        }
    }

    private fun checkExpenditureIsNull(correctionIdentification: AuditControlCorrectionUpdate) {
        if (correctionIdentification.expenditureId != null) {
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


    private fun validateSelectedFundCombinationValid(input: AuditControlCorrectionUpdate, availableData: List<CorrectionAvailablePartner>) {
        val availablePartner = availableData.firstOrNull { it.partnerId == input.partnerId }
        val availableReports = availablePartner?.availableReports?.firstOrNull { it.id == input.partnerReportId }
        val availableFtls = availablePartner?.availableFtls?.firstOrNull { it.orderNr == input.lumpSumOrderNr }
        val availableFunds = availableReports?.availableFunds ?: availableFtls?.availableFunds

        availableFunds?.filter { !it.disabled }?.firstOrNull { it.fund.id == input.programmeFundId }
            ?: throw CombinationOfSelectedFundIsInvalidException()
    }

    private fun validateOptionalScope(
        updatedData: AuditControlCorrectionUpdate,
        correction: AuditControlCorrectionDetail,
    ) {
        if (correction.type == AuditControlCorrectionType.LinkedToInvoice) {
            validateLinkedToInvoiceCorrectionScope(updatedData, correction)
        } else {
            validateLinkedToCostOptionCorrectionScope(updatedData, correction)
        }
    }

}
