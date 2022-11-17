package io.cloudflight.jems.server.controllerInstitution.repository

import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ControllerInstitutionRepository: JpaRepository<ControllerInstitutionEntity, Long> {
    fun findAllByIdIn(id: List<Long>, page: Pageable): Page<ControllerInstitutionEntity>
}
