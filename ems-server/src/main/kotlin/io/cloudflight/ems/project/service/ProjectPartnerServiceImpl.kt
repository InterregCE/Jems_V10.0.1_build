package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.InputProjectPartnerContact
import io.cloudflight.ems.api.project.dto.InputProjectPartnerCreate
import io.cloudflight.ems.api.project.dto.InputProjectPartnerUpdate
import io.cloudflight.ems.api.project.dto.OutputProjectPartner
import io.cloudflight.ems.api.project.dto.OutputProjectPartnerDetail
import io.cloudflight.ems.api.project.dto.ProjectPartnerRole
import io.cloudflight.ems.exception.I18nValidationException
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.project.repository.ProjectPartnerRepository
import io.cloudflight.ems.project.repository.ProjectRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectPartnerServiceImpl(
    private val projectPartnerRepo: ProjectPartnerRepository,
    private val projectRepo: ProjectRepository
) : ProjectPartnerService {

    @Transactional(readOnly = true)
    override fun getById(id: Long): OutputProjectPartnerDetail {
        return projectPartnerRepo.findById(id).map { it.toOutputProjectPartnerDetail() }
            .orElseThrow { ResourceNotFoundException("projectPartner") }
    }

    @Transactional(readOnly = true)
    override fun findAllByProjectId(projectId: Long, page: Pageable): Page<OutputProjectPartner> {
        return projectPartnerRepo.findAllByProjectId(projectId, page).map { it.toOutputProjectPartner() }
    }

    @Transactional
    override fun create(projectId: Long, projectPartner: InputProjectPartnerCreate): OutputProjectPartnerDetail {
        val project = projectRepo.findByIdOrNull(projectId) ?: throw ResourceNotFoundException("project")

        // prevent multiple role LEAD_PARTNER entries
        if (projectPartner.role!!.isLead)
            validateLeadPartnerChange(projectId, projectPartner.oldLeadPartnerId)

        return projectPartnerRepo.save(projectPartner.toEntity(project = project)).toOutputProjectPartnerDetail()
    }

    private fun validateLeadPartnerChange(projectId: Long, oldLeadPartnerId: Long?) {
        if (oldLeadPartnerId == null)
            validateOnlyOneLeadPartner(projectId)
        else
            updateOldLeadPartner(projectId, oldLeadPartnerId)
    }

    /**
     * validate project partner to be saved: only one role LEAD should exist.
     */
    private fun validateOnlyOneLeadPartner(projectId: Long) {
        val projectPartner = projectPartnerRepo.findFirstByProjectIdAndRole(projectId, ProjectPartnerRole.LEAD_PARTNER)
        if (projectPartner.isPresent) {
            val currentLead = projectPartner.get()
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "project.partner.role.lead.already.existing",
                i18nArguments = listOf(currentLead.id.toString(), currentLead.name)
            )
        }
    }

    private fun updateOldLeadPartner(projectId: Long, oldLeadPartnerId: Long) {
        val oldLeadPartner = projectPartnerRepo.findFirstByProjectIdAndRole(projectId, ProjectPartnerRole.LEAD_PARTNER)
            .orElseThrow { ResourceNotFoundException("projectPartner") }

        if (oldLeadPartner.id == oldLeadPartnerId)
            projectPartnerRepo.save(oldLeadPartner.copy(role = ProjectPartnerRole.PARTNER))
        else
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "project.partner.oldLeadPartnerId.is.not.lead"
            )
    }

    @Transactional
    override fun update(projectId: Long, projectPartner: InputProjectPartnerUpdate): OutputProjectPartnerDetail {
        val oldProjectPartner = projectPartnerRepo.findById(projectPartner.id)
            .orElseThrow { ResourceNotFoundException("projectPartner") }

        if (!oldProjectPartner.role.isLead && projectPartner.role!!.isLead)
            validateLeadPartnerChange(projectId, projectPartner.oldLeadPartnerId)

        return projectPartnerRepo.save(
            oldProjectPartner.copy(
                name = projectPartner.name!!,
                role = projectPartner.role!!
            )
        ).toOutputProjectPartnerDetail()
    }

    /**
     * sets or updates the sort number for all partners for the specified project.
     */
    @Transactional
    override fun updateSortByRole(projectId: Long) {
        val sort = Sort.by(listOf(
            Sort.Order(Sort.Direction.ASC, "role"),
            Sort.Order(Sort.Direction.ASC, "id")
        ))

        val projectPartners = projectPartnerRepo.findAllByProjectId(projectId, sort)
            .mapIndexed { index, old -> old.copy(sortNumber = index.plus(1)) }
        projectPartnerRepo.saveAll(projectPartners)
    }

    @Transactional
    override fun updatePartnerContact(partnerId: Long, projectPartnerContact: Set<InputProjectPartnerContact>): OutputProjectPartnerDetail {
        val projectPartner = projectPartnerRepo.findById(partnerId).orElseThrow { ResourceNotFoundException("projectPartner") }
        return projectPartnerRepo.save(
            projectPartner.copy(
                partnerContactPersons = projectPartnerContact.map{ it.toEntity(projectPartner) }.toHashSet()
            )
        ).toOutputProjectPartnerDetail()
    }

}
