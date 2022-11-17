package io.cloudflight.jems.server.project.repository.contracting.contractInfo

import io.cloudflight.jems.server.project.entity.contracting.ProjectContractInfoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectContractInfoRepository : JpaRepository<ProjectContractInfoEntity, Long>
