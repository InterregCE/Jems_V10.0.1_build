package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.InputProjectPartner
import io.cloudflight.ems.api.project.dto.InputProjectPartnerUpdate
import io.cloudflight.ems.api.project.dto.OutputProjectPartner
import io.cloudflight.ems.api.project.dto.ProjectPartnerRole
import io.cloudflight.ems.exception.I18nValidationException
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.project.repository.ProjectPartnerRepository
import io.cloudflight.ems.project.repository.ProjectRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectPartnerServiceImpl(
    private val projectPartnerRepo: ProjectPartnerRepository,
    private val projectRepo: ProjectRepository
) : ProjectPartnerService {

    @Transactional(readOnly = true)
    override fun getById(id: Long): OutputProjectPartner {
        return projectPartnerRepo.findOneById(id)?.toOutputProjectPartner()
            ?: throw ResourceNotFoundException("projectPartner")
    }

    @Transactional(readOnly = true)
    override fun findAllByProjectId(projectId: Long, page: Pageable): Page<OutputProjectPartner> {
        return projectPartnerRepo.findAllByProjectId(projectId, page).map { it.toOutputProjectPartner() }
    }

    @Transactional
    override fun createProjectPartner(projectId: Long, projectPartner: InputProjectPartner): OutputProjectPartner {
        val project = projectRepo.findOneById(projectId) ?: throw ResourceNotFoundException("project")

        // prevent multiple role LEAD_PARTNER entries
        validateProjectPartner(projectId, projectPartner.role!!)

        return projectPartnerRepo.save(projectPartner.toEntity(project = project)).toOutputProjectPartner()
    }

    /**
     * validate project partner to be saved: only one role LEAD should exist.
     */
    private fun validateProjectPartner(projectId: Long, projectPartnerRole: ProjectPartnerRole) {
        if (!ProjectPartnerRole.isLeadPartner(projectPartnerRole)) {
            return
        }

        val projectPartners =
            projectPartnerRepo.findAllByProjectIdAndRole(projectId, ProjectPartnerRole.LEAD_PARTNER, Pageable.unpaged())
        if (!projectPartners.isEmpty) {
            val leadPartnerNameAlreadyExisting = projectPartners.first().name
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "project.partner.role.lead.already.existing",
                additionalInfo = leadPartnerNameAlreadyExisting
            )
        }
    }

    @Transactional
    override fun update(projectId: Long, projectPartner: InputProjectPartnerUpdate): OutputProjectPartner {
        val oldProjectPartner = projectPartnerRepo.findById(projectPartner.id).orElseThrow { ResourceNotFoundException("projectPartner") }
        val project = projectRepo.findOneById(projectId) ?: throw ResourceNotFoundException("project")

        return projectPartnerRepo.save(
            oldProjectPartner.copy(
                name = projectPartner.name!!,
                role = projectPartner.role!!,
                project = project
            )
        ).toOutputProjectPartner()
    }

    /**
     * sets or updates the sort number for all partners for the specified project.
     */
    @Transactional
    override fun updateSortByRole(projectId: Long) {
        val pageable =  PageRequest.of(0, Integer.MAX_VALUE, Sort.by(listOf(
            Sort.Order(Sort.Direction.ASC, "role"),
            Sort.Order(Sort.Direction.DESC, "id")
        )))

        val projectPartners = projectPartnerRepo.findAllByProjectId(projectId, pageable)
        // renumber partners by role
        projectPartners.forEachIndexed {
            index, oldPartner ->
            // index starts at 0
            val updatedPartner = oldPartner.copy(sortNumber = index.plus(1).toLong())
            projectPartnerRepo.save(updatedPartner)
        }
    }

}
