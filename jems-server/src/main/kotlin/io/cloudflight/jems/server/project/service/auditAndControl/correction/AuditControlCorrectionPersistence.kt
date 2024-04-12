package io.cloudflight.jems.server.project.service.auditAndControl.correction

import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionLinking
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionSearchRequest
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionLinking
import io.cloudflight.jems.server.payments.model.ec.PaymentToEcCorrectionSearchRequest
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.tmpModel.AuditControlCorrectionLineTmp
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionDetail
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.CorrectionCostItem
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.availableData.AvailableCorrectionsForPayment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable


interface AuditControlCorrectionPersistence {

    fun getProjectIdForCorrection(correctionId: Long): Long

    fun getAllCorrectionsByAuditControlId(auditControlId: Long, pageable: Pageable): Page<AuditControlCorrectionLineTmp>

    fun getPreviousClosedCorrections(correctionId: Long): List<AuditControlCorrection>

    fun getByCorrectionId(correctionId: Long): AuditControlCorrectionDetail

    fun getLastUsedOrderNr(auditControlId: Long): Int?

    fun deleteCorrectionById(id: Long)

    fun closeCorrection(correctionId: Long): AuditControlCorrection

    fun getOngoingCorrectionsByAuditControlId(auditControlId: Long): List<AuditControlCorrection>

    fun updateCorrection(correctionId: Long, data: AuditControlCorrectionUpdate): AuditControlCorrectionDetail

    fun getCorrectionAvailableCostItems(partnerReportId: Long, pageable: Pageable): Page<CorrectionCostItem>

    fun updateModificationByCorrectionIds(projectId: Long, correctionIds: Set<Long>, statuses: List<ApplicationStatus>)

    fun getAvailableCorrectionsForPayments(projectId: Long): List<AvailableCorrectionsForPayment>

    fun getAvailableCorrectionsForModification(projectId: Long): List<AuditControlCorrection>

    fun getCorrectionsForModificationDecisions(projectId: Long): Map<Long, List<AuditControlCorrection>>

    fun getCorrectionsLinkedToEcPayment(pageable: Pageable, filter: PaymentToEcCorrectionSearchRequest): Page<PaymentToEcCorrectionLinking>

    fun getCorrectionsLinkedToPaymentAccount(pageable: Pageable, filter: PaymentAccountCorrectionSearchRequest): Page<PaymentAccountCorrectionLinking>

    fun existsByProcurementId(procurementId: Long): Boolean
}
