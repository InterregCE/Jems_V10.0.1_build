package io.cloudflight.jems.server.project.entity.report.project.identification

import io.cloudflight.jems.server.project.entity.report.project.ProjectReportEntity
import java.io.Serializable
import java.util.Objects
import javax.persistence.Embeddable
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class ProjectReportSpendingProfileId(

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name="project_report_id", referencedColumnName="id")
    @field:NotNull
    val projectReport: ProjectReportEntity,

    @field:NotNull
    val partnerId: Long
): Serializable {
    override fun equals(other: Any?): Boolean = this === other ||
        other is ProjectReportSpendingProfileId && projectReport == other.projectReport && partnerId == other.partnerId

    override fun hashCode() = Objects.hash(projectReport)
}
