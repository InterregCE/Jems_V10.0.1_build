package io.cloudflight.jems.server.project.service.report.project.identification

import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentification
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportIdentificationUpdate
import io.cloudflight.jems.server.project.service.report.model.project.identification.ProjectReportSpendingProfileReportedValues
import java.math.BigDecimal

interface ProjectReportIdentificationPersistence {

    fun getReportIdentification(projectId: Long, reportId: Long): ProjectReportIdentification

    fun getSpendingProfileReportedValues(reportId: Long): List<ProjectReportSpendingProfileReportedValues>

    fun getSpendingProfileCumulative(reportIds: Set<Long>): Map<Long, BigDecimal>

    fun getSpendingProfileCurrentValues(reportId: Long): Map<Long, BigDecimal>

    fun updateReportIdentification(
        projectId: Long,
        reportId: Long,
        identification: ProjectReportIdentificationUpdate
    ): ProjectReportIdentification

    fun updateSpendingProfile(reportId: Long, currentValuesByPartnerId: Map<Long, BigDecimal>)

    fun deleteSpendingProfileReportedValues(reportId: Long, partnerId: Long)
}
