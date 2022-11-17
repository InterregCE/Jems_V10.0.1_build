package io.cloudflight.jems.server.project.repository.contracting.management

import io.cloudflight.jems.server.project.entity.contracting.ContractingManagementId
import io.cloudflight.jems.server.project.entity.contracting.ProjectContractingManagementEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectContractingManagementRepository: JpaRepository<ProjectContractingManagementEntity, ContractingManagementId> {

    fun findByManagementIdProjectId(projectId: Long): List<ProjectContractingManagementEntity>
}
