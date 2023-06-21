package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionNutsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ControllerInstitutionNutsRepository : JpaRepository<ControllerInstitutionNutsEntity, Long> {

    fun findAllByIdInstitutionIdIn(institutionIds: List<Long>): List<ControllerInstitutionNutsEntity>
}
