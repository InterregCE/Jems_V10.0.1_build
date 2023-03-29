package io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import java.math.BigDecimal
import java.time.LocalDate

data class ProjectPartnerReportExpenditureVerification(
    override val id: Long,
    override var lumpSumId: Long?,
    val number: Int,
    val unitCostId: Long?,
    var gdpr: Boolean,
    override val costCategory: ReportBudgetCategory,
    val investmentId: Long?,
    val contractId: Long?,
    val internalReferenceNumber: String?,
    val invoiceNumber: String?,
    val invoiceDate: LocalDate?,
    val dateOfPayment: LocalDate?,
    var description: Set<InputTranslation> = emptySet(),
    var comment: Set<InputTranslation> = emptySet(),
    val totalValueInvoice: BigDecimal? = null,
    val vat: BigDecimal? = null,
    val numberOfUnits: BigDecimal = BigDecimal.ONE,
    val pricePerUnit: BigDecimal = BigDecimal.ZERO,
    val declaredAmount: BigDecimal = BigDecimal.ZERO,
    val currencyCode: String,
    val currencyConversionRate: BigDecimal?,
    override var declaredAmountAfterSubmission: BigDecimal?,
    val attachment: JemsFileMetadata?,

    var partOfSample: Boolean,
    var partOfSampleLocked: Boolean,
    var certifiedAmount: BigDecimal,
    var deductedAmount: BigDecimal,
    var typologyOfErrorId: Long?,
    var parked: Boolean,
    var verificationComment: String?,

    override val parkingMetadata: ExpenditureParkingMetadata?,
): ExpenditureCost
