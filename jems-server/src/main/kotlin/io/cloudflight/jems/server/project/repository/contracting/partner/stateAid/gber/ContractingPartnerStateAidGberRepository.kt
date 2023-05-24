package io.cloudflight.jems.server.project.repository.contracting.partner.stateAid.gber

import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerStateAidGberEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContractingPartnerStateAidGberRepository: JpaRepository<ProjectContractingPartnerStateAidGberEntity, Long>
