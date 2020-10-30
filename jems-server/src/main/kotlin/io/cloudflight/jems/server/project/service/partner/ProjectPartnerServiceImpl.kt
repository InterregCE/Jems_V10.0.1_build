package io.cloudflight.jems.server.project.service.partner

import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.InputProjectPartnerContribution
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerAddress
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.server.exception.I18nValidationException
import io.cloudflight.jems.server.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.partner.ProjectPartner
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors
import java.util.stream.StreamSupport

@Service
class ProjectPartnerServiceImpl(
    private val projectPartnerRepo: ProjectPartnerRepository,
    private val projectAssociatedOrganizationService: ProjectAssociatedOrganizationService,
    private val projectRepo: ProjectRepository
) : ProjectPartnerService {

    companion object {
        // when changing also change repository findTop*() methods
        const val MAX_PROJECT_PARTNERS = 30
    }

    @Transactional(readOnly = true)
    override fun getById(projectId: Long, id: Long): OutputProjectPartnerDetail {
        return getPartnerOrThrow(projectId, id).toOutputProjectPartnerDetail()
    }

    @Transactional(readOnly = true)
    override fun findAllByProjectId(projectId: Long, page: Pageable): Page<OutputProjectPartner> {
        return projectPartnerRepo.findAllByProjectId(projectId, page).map { it.toOutputProjectPartner() }
    }

    @Transactional(readOnly = true)
    override fun findAllByProjectIdForDropdown(projectId: Long, sort: Sort): List<OutputProjectPartner> {
        return StreamSupport.stream(
            projectPartnerRepo.findTop30ByProjectId(projectId, sort).spliterator(),
            false
        ).map { it.toOutputProjectPartner() }.collect(Collectors.toList())
    }

    @Transactional
    override fun create(projectId: Long, projectPartner: InputProjectPartnerCreate): OutputProjectPartnerDetail {
        val project = projectRepo.findById(projectId).orElseThrow { ResourceNotFoundException("project") }

        // to be possible to list all partners for dropdowns we decided to limit total amount of them
        if (projectPartnerRepo.countByProjectId(projectId) >= MAX_PROJECT_PARTNERS)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "project.partner.max.allowed.count.reached"
            )

        // prevent multiple role LEAD_PARTNER entries
        if (projectPartner.role!!.isLead)
            validateLeadPartnerChange(projectId, projectPartner.oldLeadPartnerId)

        val partnerCreated = projectPartnerRepo.save(projectPartner.toEntity(project = project))
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
                i18nArguments = listOf(currentLead.id.toString(), currentLead.abbreviation)
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
        val oldProjectPartner = getPartnerOrThrow(projectId, projectPartner.id)

        val makingThisLead = !oldProjectPartner.role.isLead && projectPartner.role!!.isLead
        if (makingThisLead)
            validateLeadPartnerChange(projectId, projectPartner.oldLeadPartnerId)

        val partnerUpdated = projectPartnerRepo.save(
            oldProjectPartner.copy(
                abbreviation = projectPartner.abbreviation!!,
                role = projectPartner.role!!,
                nameInOriginalLanguage = projectPartner.nameInOriginalLanguage,
                nameInEnglish = projectPartner.nameInEnglish,
                department = projectPartner.department
            )
        )
        // update sorting if leadPartner changed
        if (projectPartner.oldLeadPartnerId != null || makingThisLead)
            updateSortByRole(projectId)

        return partnerUpdated.toOutputProjectPartnerDetail()
    }

    /**
     * sets or updates the sort number for all partners for the specified project.
     */
    private fun updateSortByRole(projectId: Long) {
        val sort = Sort.by(listOf(
            Sort.Order(Sort.Direction.ASC, "role"),
            Sort.Order(Sort.Direction.ASC, "id")
        ))

        val projectPartners = projectPartnerRepo.findTop30ByProjectId(projectId, sort)
            .mapIndexed { index, old -> old.copy(sortNumber = index.plus(1)) }
        projectPartnerRepo.saveAll(projectPartners)
    }

    @Transactional
    override fun updatePartnerAddresses(projectId: Long, partnerId: Long, addresses: Set<InputProjectPartnerAddress>): OutputProjectPartnerDetail {
        val projectPartner = getPartnerOrThrow(projectId, partnerId)
        return projectPartnerRepo.save(
            projectPartner.copy(
                addresses = addresses.mapTo(HashSet()) { it.toEntity(projectPartner) }
            )
        ).toOutputProjectPartnerDetail()
    }

    @Transactional
    override fun updatePartnerContacts(projectId: Long, partnerId: Long, contacts: Set<InputProjectContact>): OutputProjectPartnerDetail {
        val projectPartner = getPartnerOrThrow(projectId, partnerId)
        return projectPartnerRepo.save(
            projectPartner.copy(
                contacts = contacts.mapTo(HashSet()) { it.toEntity(projectPartner) }
            )
        ).toOutputProjectPartnerDetail()
    }

    @Transactional
    override fun updatePartnerContribution(projectId: Long, partnerId: Long, partnerContribution: InputProjectPartnerContribution): OutputProjectPartnerDetail {
        val projectPartner = getPartnerOrThrow(projectId, partnerId)
        return projectPartnerRepo.save(
            projectPartner.copy(
                partnerContribution = partnerContribution.toEntity(projectPartner.id!!)
            )
        ).toOutputProjectPartnerDetail()
    }

    @Transactional
    override fun deletePartner(projectId: Long, partnerId: Long) {
        projectPartnerRepo.deleteById(partnerId)
        updateSortByRole(projectId)
        projectAssociatedOrganizationService.refreshSortNumbers(projectId)
    }

    private fun getPartnerOrThrow(projectId: Long, partnerId: Long): ProjectPartner {
        return projectPartnerRepo.findFirstByProjectIdAndId(projectId, partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }
    }

}
