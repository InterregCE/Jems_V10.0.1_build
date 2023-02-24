package io.cloudflight.jems.server.project.entity.report.project

import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class ProjectReportCoFinancingIdEntity(

    @ManyToOne
    @JoinColumn(name = "report_id")
    @field:NotNull
    val report: ProjectReportEntity,

    @field:NotNull
    val fundSortNumber: Int,

): Serializable {

    companion object {
        const val serialVersionUID = 1L
    }

    override fun equals(other: Any?): Boolean = this === other ||
        other is ProjectReportCoFinancingIdEntity
        && report == other.report
        && fundSortNumber == other.fundSortNumber

    override fun hashCode(): Int = Objects.hash(report, fundSortNumber)

}
