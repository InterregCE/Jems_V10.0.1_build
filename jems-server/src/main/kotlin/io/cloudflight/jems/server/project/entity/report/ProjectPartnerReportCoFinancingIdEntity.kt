package io.cloudflight.jems.server.project.entity.report

import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class ProjectPartnerReportCoFinancingIdEntity(

    @ManyToOne
    @JoinColumn(name = "report_id")
    @field:NotNull
    val report: ProjectPartnerReportEntity,

    @field:NotNull
    val fundSortNumber: Int,

): Serializable {

    companion object {
        const val serialVersionUID = 1L
    }

    override fun equals(other: Any?): Boolean = this === other ||
        other is ProjectPartnerReportCoFinancingIdEntity
        && report == other.report
        && fundSortNumber == other.fundSortNumber

    override fun hashCode(): Int = Objects.hash(report, fundSortNumber)

}
