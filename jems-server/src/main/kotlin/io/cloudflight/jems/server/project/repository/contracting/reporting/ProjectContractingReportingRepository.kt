package io.cloudflight.jems.server.project.repository.contracting.reporting

import io.cloudflight.jems.server.project.entity.contracting.reporting.ProjectContractingReportingEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectContractingReportingRepository: JpaRepository<ProjectContractingReportingEntity, Long> {

    fun findTop50ByProjectIdOrderByDeadline(projectId: Long): MutableList<ProjectContractingReportingEntity>

}
