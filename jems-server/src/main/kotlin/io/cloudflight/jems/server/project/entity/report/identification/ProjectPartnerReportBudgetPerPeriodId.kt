package io.cloudflight.jems.server.project.entity.report.identification

import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import java.io.Serializable
import java.util.Objects
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class ProjectPartnerReportBudgetPerPeriodId (

    @field:NotNull
    @ManyToOne
    var report: ProjectPartnerReportEntity,

    @Column
    @field:NotNull
    val periodNumber: Int,

) : Serializable {

    override fun equals(other: Any?): Boolean = this === other ||
        other is ProjectPartnerReportBudgetPerPeriodId && report == other.report && periodNumber == other.periodNumber

    override fun hashCode(): Int = Objects.hash(report)

}
