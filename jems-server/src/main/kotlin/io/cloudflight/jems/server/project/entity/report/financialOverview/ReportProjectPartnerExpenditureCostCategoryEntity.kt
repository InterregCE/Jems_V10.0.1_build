package io.cloudflight.jems.server.project.entity.report.financialOverview

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.MapsId
import javax.persistence.OneToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_partner_expenditure_cost_category")
class ReportProjectPartnerExpenditureCostCategoryEntity(

    @Id
    val reportId: Long = 0,

    @OneToOne
    @JoinColumn(name = "report_id")
    @MapsId
    @field:NotNull
    val reportEntity: ProjectPartnerReportEntity,

    val officeAndAdministrationOnStaffCostsFlatRate: Int?,
    val officeAndAdministrationOnDirectCostsFlatRate: Int?,
    val travelAndAccommodationOnStaffCostsFlatRate: Int?,
    val staffCostsFlatRate: Int?,
    val otherCostsOnStaffCostsFlatRate: Int?,

    @field:NotNull
    val staffTotal: BigDecimal,
    @field:NotNull
    val officeTotal: BigDecimal,
    @field:NotNull
    val travelTotal: BigDecimal,
    @field:NotNull
    val externalTotal: BigDecimal,
    @field:NotNull
    val equipmentTotal: BigDecimal,
    @field:NotNull
    val infrastructureTotal: BigDecimal,
    @field:NotNull
    val otherTotal: BigDecimal,
    @field:NotNull
    val lumpSumTotal: BigDecimal,
    @field:NotNull
    val unitCostTotal: BigDecimal,
    @field:NotNull
    val sumTotal: BigDecimal,

    @field:NotNull
    var staffCurrent: BigDecimal,
    @field:NotNull
    var officeCurrent: BigDecimal,
    @field:NotNull
    var travelCurrent: BigDecimal,
    @field:NotNull
    var externalCurrent: BigDecimal,
    @field:NotNull
    var equipmentCurrent: BigDecimal,
    @field:NotNull
    var infrastructureCurrent: BigDecimal,
    @field:NotNull
    var otherCurrent: BigDecimal,
    @field:NotNull
    var lumpSumCurrent: BigDecimal,
    @field:NotNull
    var unitCostCurrent: BigDecimal,
    @field:NotNull
    var sumCurrent: BigDecimal,

    @field:NotNull
    val staffPreviouslyReported: BigDecimal,
    @field:NotNull
    val officePreviouslyReported: BigDecimal,
    @field:NotNull
    val travelPreviouslyReported: BigDecimal,
    @field:NotNull
    val externalPreviouslyReported: BigDecimal,
    @field:NotNull
    val equipmentPreviouslyReported: BigDecimal,
    @field:NotNull
    val infrastructurePreviouslyReported: BigDecimal,
    @field:NotNull
    val otherPreviouslyReported: BigDecimal,
    @field:NotNull
    val lumpSumPreviouslyReported: BigDecimal,
    @field:NotNull
    val unitCostPreviouslyReported: BigDecimal,
    @field:NotNull
    val sumPreviouslyReported: BigDecimal,

) : Serializable
