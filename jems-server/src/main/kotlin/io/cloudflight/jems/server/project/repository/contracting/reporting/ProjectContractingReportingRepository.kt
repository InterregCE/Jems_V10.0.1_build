package io.cloudflight.jems.server.project.repository.contracting.reporting

import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProjectContractingReportingRepository: JpaRepository<ProjectContractingReportingEntity, Long> {

    fun findTop50ByProjectIdOrderByDeadline(projectId: Long): MutableList<ProjectContractingReportingEntity>

    fun findAllByProjectIdAndPeriodNumberGreaterThan(projectId: Long, maxAvailablePeriod: Int):
        MutableList<ProjectContractingReportingEntity>

    @Modifying
    @Query("UPDATE #{#entityName} SET period_number = NULL, deadline = NULL WHERE id IN :ids", nativeQuery = true)
    fun updatePeriodAndDatesAsNullFor(ids: List<Long>)

}
