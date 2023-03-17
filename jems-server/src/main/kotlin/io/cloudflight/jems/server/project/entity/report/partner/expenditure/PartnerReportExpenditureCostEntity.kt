package io.cloudflight.jems.server.project.entity.report.partner.expenditure

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.project.entity.report.partner.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_expenditure")
class PartnerReportExpenditureCostEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @field:NotNull
    var number: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_report_id")
    @field:NotNull
    val partnerReport: ProjectPartnerReportEntity,

    @ManyToOne
    var reportLumpSum: PartnerReportLumpSumEntity?,

    @ManyToOne
    var reportUnitCost: PartnerReportUnitCostEntity?,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var costCategory: ReportBudgetCategory,

    @ManyToOne
    var reportInvestment: PartnerReportInvestmentEntity?,

    var procurementId: Long?,
    var internalReferenceNumber: String?,
    var invoiceNumber: String?,
    var invoiceDate: LocalDate?,
    var dateOfPayment: LocalDate?,
    var totalValueInvoice: BigDecimal?,
    var vat: BigDecimal?,
    @field:NotNull
    var numberOfUnits: BigDecimal,
    @field:NotNull
    var pricePerUnit: BigDecimal,
    @field:NotNull
    var declaredAmount: BigDecimal,

    @field:NotNull
    var currencyCode: String,
    var currencyConversionRate: BigDecimal?,
    var declaredAmountAfterSubmission: BigDecimal?,

    @field:NotNull
    var partOfSample: Boolean,
    @field:NotNull
    var partOfSampleLocked: Boolean,
    @field:NotNull
    var certifiedAmount: BigDecimal,
    @field:NotNull
    var deductedAmount: BigDecimal,

    var typologyOfErrorId: Long?,

    @field:NotNull
    var parked: Boolean,

    var verificationComment: String?,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<PartnerReportExpenditureCostTranslEntity> = mutableSetOf(),

    @ManyToOne
    @JoinColumn(name = "file_id")
    var attachment: JemsFileMetadataEntity?,


    @OneToOne
    @JoinColumn(name = "un_parked_from_expenditure_id")
    val unParkedFrom: PartnerReportExpenditureCostEntity?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_of_origin_id")
    val reportOfOrigin: ProjectPartnerReportEntity?,

    val originalNumber: Int?,

)
