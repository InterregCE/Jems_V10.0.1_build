package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.server.call.entity.ApplicationFormConfigurationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ApplicationFormConfigurationRepository : JpaRepository<ApplicationFormConfigurationEntity, Long>
