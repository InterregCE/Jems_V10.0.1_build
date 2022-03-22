package io.cloudflight.jems.server.project.entity.report.expenditure

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.CascadeType
import javax.persistence.Entity
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
    var id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_report_id")
    @field:NotNull
    val partnerReport: ProjectPartnerReportEntity,

    @field:NotNull
    var costCategory: String,

    var investmentId: Long?,
    var procurementId: Long?,
    var internalReferenceNumber: String?,
    var invoiceNumber: String?,
    var invoiceDate: LocalDate?,
    var dateOfPayment: LocalDate?,
    var totalValueInvoice: BigDecimal?,
    var vat: BigDecimal?,
    var declaredAmount: BigDecimal?,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.sourceEntity")
    var translatedValues: MutableSet<PartnerReportExpenditureCostTranslEntity> = mutableSetOf(),
)
