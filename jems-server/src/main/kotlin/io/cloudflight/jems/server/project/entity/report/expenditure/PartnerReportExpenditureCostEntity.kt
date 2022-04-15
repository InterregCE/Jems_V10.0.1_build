package io.cloudflight.jems.server.project.entity.report.expenditure

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_expenditure")
class PartnerReportExpenditureCostEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_report_id")
    @field:NotNull
    val partnerReport: ProjectPartnerReportEntity,

    @Enumerated(EnumType.STRING)
    @field:NotNull
    var costCategory: BudgetCategory,

    var investmentId: Long?,
    var procurementId: Long?,
    var internalReferenceNumber: String?,
    var invoiceNumber: String?,
    var invoiceDate: LocalDate?,
    var dateOfPayment: LocalDate?,
    var totalValueInvoice: BigDecimal?,
    var vat: BigDecimal?,
    @field:NotNull
    var declaredAmount: BigDecimal,

    @field:NotNull
    var currencyCode: String,
    var currencyConversionRate: BigDecimal?,
    var declaredAmountAfterSubmission: BigDecimal?,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    val translatedValues: MutableSet<PartnerReportExpenditureCostTranslEntity> = mutableSetOf(),

    @ManyToOne
    @JoinColumn(name = "file_id")
    var attachment: ReportProjectFileEntity?,
)
