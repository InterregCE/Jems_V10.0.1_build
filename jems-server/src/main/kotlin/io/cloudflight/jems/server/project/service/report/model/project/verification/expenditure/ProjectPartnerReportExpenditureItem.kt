package io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import java.math.BigDecimal
import java.time.LocalDate

data class ProjectPartnerReportExpenditureItem(

    val id: Long,
    val number: Int,

    val partnerId: Long,
    val partnerRole: ProjectPartnerRole,
    val partnerNumber: Int,

    val partnerReportId: Long,
    val partnerReportNumber: Int,

    var lumpSum: ProjectPartnerReportLumpSum?,
    val unitCost: ProjectPartnerReportUnitCost?,
    var gdpr: Boolean,
    val costCategory: ReportBudgetCategory,
    val investment: ExpenditureInvestmentBreakdownLine?,
    val contract: ProjectPartnerReportProcurement?,
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
    var declaredAmountAfterSubmission: BigDecimal?,
    val attachment: JemsFileMetadata?,

    var partOfSample: Boolean,
    var partOfSampleLocked: Boolean,
    var certifiedAmount: BigDecimal,
    var deductedAmount: BigDecimal,
    var typologyOfErrorId: Long?,
    var parked: Boolean,
    var verificationComment: String?,

    val parkingMetadata: ExpenditureParkingMetadata?,
)
