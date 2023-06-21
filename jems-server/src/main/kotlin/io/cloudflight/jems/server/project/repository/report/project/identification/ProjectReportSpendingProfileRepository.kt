package io.cloudflight.jems.server.project.repository.report.project.identification

import io.cloudflight.jems.server.project.entity.report.project.identification.ProjectReportSpendingProfileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface ProjectReportSpendingProfileRepository :
    JpaRepository<ProjectReportSpendingProfileEntity, Long> {

    fun findAllByIdProjectReportIdOrderByPartnerNumber(projectReportId: Long): List<ProjectReportSpendingProfileEntity>

    fun deleteByIdProjectReportIdAndIdPartnerId(projectReportId: Long, partnerId: Long)

    @Query(
        """
            SELECT new kotlin.Pair(sp.id.partnerId, COALESCE(SUM(sp.currentlyReported), 0))
            FROM #{#entityName} sp
            WHERE sp.id.projectReport.id IN :reportIds
            GROUP BY sp.id.partnerId
        """
    )
    fun findCumulativeForReportIds(reportIds: Set<Long>): List<Pair<Long, BigDecimal>>
}
