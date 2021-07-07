package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerStateAidEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProjectPartnerStateAidRepository : JpaRepository<ProjectPartnerStateAidEntity, Long>
