package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.api.project.dto.InputProjectPartnerContact
import io.cloudflight.jems.api.project.dto.InputProjectPartnerContribution
import io.cloudflight.jems.api.project.dto.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.InputProjectPartnerOrganizationDetails
import io.cloudflight.jems.api.project.dto.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.ProjectPartnerRole
import io.cloudflight.jems.server.exception.I18nValidationException
import io.cloudflight.jems.server.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectPartnerOrganizationRepository
import io.cloudflight.jems.server.project.repository.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectPartnerServiceImpl(
    private val projectPartnerRepo: ProjectPartnerRepository,
    private val projectRepo: ProjectRepository,
    private val projectPartnerOrganizationRepo: ProjectPartnerOrganizationRepository
) : ProjectPartnerService {

    @Transactional(readOnly = true)
    override fun getById(projectId: Long, id: Long): OutputProjectPartnerDetail {
        return projectPartnerRepo.findFirstByProjectIdAndId(projectId, id).map { it.toOutputProjectPartnerDetail() }
            .orElseThrow { ResourceNotFoundException("projectPartner") }
    }

    @Transactional(readOnly = true)
    override fun findAllByProjectId(projectId: Long, page: Pageable): Page<OutputProjectPartner> {
        return projectPartnerRepo.findAllByProjectId(projectId, page).map { it.toOutputProjectPartner() }
    }

    @Transactional
    override fun create(projectId: Long, projectPartner: InputProjectPartnerCreate): OutputProjectPartnerDetail {
        val project = projectRepo.findById(projectId).orElseThrow { ResourceNotFoundException("project") }

        // prevent multiple role LEAD_PARTNER entries
        if (projectPartner.role!!.isLead)
            validateLeadPartnerChange(projectId, projectPartner.oldLeadPartnerId)

        val organization = if (projectPartner.organization == null){
            null
        } else {
            this.projectPartnerOrganizationRepo.save(projectPartner.organization!!.toEntity())
        }

        val partnerCreated = projectPartnerRepo.save(projectPartner.toEntity(project = project).copy(organization = organization));
        updateSortByRole(projectId)
        // entity is attached, number will have been updated
        return partnerCreated.toOutputProjectPartnerDetail()
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
        val oldProjectPartner = projectPartnerRepo.findFirstByProjectIdAndId(projectId, projectPartner.id)
            .orElseThrow { ResourceNotFoundException("projectPartner") }

        if (!oldProjectPartner.role.isLead && projectPartner.role!!.isLead)
            validateLeadPartnerChange(projectId, projectPartner.oldLeadPartnerId)

        val organization = if (projectPartner.organization == null){
            null
        } else {
            this.projectPartnerOrganizationRepo.save(projectPartner.organization!!.toEntity())
        }

        val partnerUpdated = projectPartnerRepo.save(
            oldProjectPartner.copy(
                name = projectPartner.name!!,
                role = projectPartner.role!!,
                organization = organization
            )
        )
        // update sorting if leadPartner changed
        if (projectPartner.oldLeadPartnerId != null)
            updateSortByRole(projectId)

        return partnerUpdated.toOutputProjectPartnerDetail()
    }

    /**
     * sets or updates the sort number for all partners for the specified project.
     */
    protected fun updateSortByRole(projectId: Long) {
        val sort = Sort.by(listOf(
            Sort.Order(Sort.Direction.ASC, "role"),
            Sort.Order(Sort.Direction.ASC, "id")
        ))

        val projectPartners = projectPartnerRepo.findAllByProjectId(projectId, sort)
            .mapIndexed { index, old -> old.copy(sortNumber = index.plus(1)) }
        projectPartnerRepo.saveAll(projectPartners)
    }

    @Transactional
    override fun updatePartnerContact(projectId: Long, partnerId: Long, projectPartnerContact: Set<InputProjectPartnerContact>): OutputProjectPartnerDetail {
        val projectPartner = projectPartnerRepo.findFirstByProjectIdAndId(projectId, partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }
        return projectPartnerRepo.save(
            projectPartner.copy(
                partnerContactPersons = projectPartnerContact.map{ it.toEntity(projectPartner) }.toHashSet()
            )
        ).toOutputProjectPartnerDetail()
    }

    @Transactional
    override fun updatePartnerContribution(projectId: Long, partnerId: Long, partnerContribution: InputProjectPartnerContribution): OutputProjectPartnerDetail {
        val projectPartner = projectPartnerRepo.findFirstByProjectIdAndId(projectId, partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }
        return projectPartnerRepo.save(
            projectPartner.copy(
                partnerContribution = partnerContribution.toEntity(projectPartner)
            )
        ).toOutputProjectPartnerDetail()
    }

    @Transactional
    override fun updatePartnerOrganizationDetails(projectId: Long, partnerId: Long, partnerOrganizationDetails: Set<InputProjectPartnerOrganizationDetails>): OutputProjectPartnerDetail {
        val projectPartner = projectPartnerRepo.findFirstByProjectIdAndId(projectId, partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }
        val partnerOrganization =
            projectPartnerOrganizationRepo.findById(projectPartner.organization?.id!!)
                .orElseThrow { ResourceNotFoundException("projectPartnerOrganization") }
        val updatePartnerOrganization = projectPartnerOrganizationRepo.save(
            partnerOrganization.copy(
                organizationsDetails = partnerOrganizationDetails.map{ it.toEntity(partnerOrganization) }.toHashSet()
            )
        )

        return projectPartnerRepo.save(
                    projectPartner.copy(
                        organization = updatePartnerOrganization
                    )
                ).toOutputProjectPartnerDetail()
    }

    @Transactional
    override fun deletePartner(projectId: Long, partnerId: Long) {
        this.projectPartnerRepo.deleteById(partnerId)
        this.updateSortByRole(projectId)
    }

}
