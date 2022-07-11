package io.cloudflight.jems.server.project.repository.contracting.monitoring

import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingMonitoringEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProjectContractingMonitoringRepository: JpaRepository<ProjectContractingMonitoringEntity, Long> {

    fun findByProjectId(projectId: Long): Optional<ProjectContractingMonitoringEntity>

}
