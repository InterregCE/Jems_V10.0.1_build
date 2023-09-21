package io.cloudflight.jems.server.project.entity.report.project.financialOverview

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import java.io.Serializable
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.MapsId
import javax.persistence.OneToOne
import javax.validation.constraints.NotNull

@Entity(name = "report_project_certificate_cost_category")
class ReportProjectCertificateCostCategoryEntity(

    @Id
    val reportId: Long = 0,

    @OneToOne
    @JoinColumn(name = "report_id")
    @MapsId
    @field:NotNull
    val reportEntity: ProjectReportEntity,

    @field:NotNull val staffTotal: BigDecimal,
    @field:NotNull val officeTotal: BigDecimal,
    @field:NotNull val travelTotal: BigDecimal,
    @field:NotNull val externalTotal: BigDecimal,
    @field:NotNull val equipmentTotal: BigDecimal,
    @field:NotNull val infrastructureTotal: BigDecimal,
    @field:NotNull val otherTotal: BigDecimal,
    @field:NotNull val lumpSumTotal: BigDecimal,
    @field:NotNull val unitCostTotal: BigDecimal,
    @field:NotNull val sumTotal: BigDecimal,

    @field:NotNull var staffCurrent: BigDecimal,
    @field:NotNull var officeCurrent: BigDecimal,
    @field:NotNull var travelCurrent: BigDecimal,
    @field:NotNull var externalCurrent: BigDecimal,
    @field:NotNull var equipmentCurrent: BigDecimal,
    @field:NotNull var infrastructureCurrent: BigDecimal,
    @field:NotNull var otherCurrent: BigDecimal,
    @field:NotNull var lumpSumCurrent: BigDecimal,
    @field:NotNull var unitCostCurrent: BigDecimal,
    @field:NotNull var sumCurrent: BigDecimal,

    @field:NotNull val staffPreviouslyReported: BigDecimal,
    @field:NotNull val officePreviouslyReported: BigDecimal,
    @field:NotNull val travelPreviouslyReported: BigDecimal,
    @field:NotNull val externalPreviouslyReported: BigDecimal,
    @field:NotNull val equipmentPreviouslyReported: BigDecimal,
    @field:NotNull val infrastructurePreviouslyReported: BigDecimal,
    @field:NotNull val otherPreviouslyReported: BigDecimal,
    @field:NotNull val lumpSumPreviouslyReported: BigDecimal,
    @field:NotNull val unitCostPreviouslyReported: BigDecimal,
    @field:NotNull val sumPreviouslyReported: BigDecimal,

    @field:NotNull val staffPreviouslyVerified: BigDecimal,
    @field:NotNull val officePreviouslyVerified: BigDecimal,
    @field:NotNull val travelPreviouslyVerified: BigDecimal,
    @field:NotNull val externalPreviouslyVerified: BigDecimal,
    @field:NotNull val equipmentPreviouslyVerified: BigDecimal,
    @field:NotNull val infrastructurePreviouslyVerified: BigDecimal,
    @field:NotNull val otherPreviouslyVerified: BigDecimal,
    @field:NotNull val lumpSumPreviouslyVerified: BigDecimal,
    @field:NotNull val unitCostPreviouslyVerified: BigDecimal,
    @field:NotNull val sumPreviouslyVerified: BigDecimal,

    @field:NotNull var staffCurrentVerified: BigDecimal,
    @field:NotNull var officeCurrentVerified: BigDecimal,
    @field:NotNull var travelCurrentVerified: BigDecimal,
    @field:NotNull var externalCurrentVerified: BigDecimal,
    @field:NotNull var equipmentCurrentVerified: BigDecimal,
    @field:NotNull var infrastructureCurrentVerified: BigDecimal,
    @field:NotNull var otherCurrentVerified: BigDecimal,
    @field:NotNull var lumpSumCurrentVerified: BigDecimal,
    @field:NotNull var unitCostCurrentVerified: BigDecimal,
    @field:NotNull var sumCurrentVerified: BigDecimal,

    ) : Serializable
