package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.api.project.dto.InputProjectContact
import io.cloudflight.jems.api.project.dto.ProjectPartnerMotivationDTO
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerCreate
import io.cloudflight.jems.api.project.dto.partner.InputProjectPartnerUpdate
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartnerDetail
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import java.sql.Timestamp
import java.util.stream.Collectors
import java.util.stream.StreamSupport
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class PartnerPersistenceProvider(
    private val projectVersionUtils: ProjectVersionUtils,
    private val projectPartnerRepository: ProjectPartnerRepository,
    private val projectPersistence: ProjectPersistence,
    private val legalStatusRepo: ProgrammeLegalStatusRepository,
    private val projectRepo: ProjectRepository,
    private val projectAssociatedOrganizationService: ProjectAssociatedOrganizationService,
) : PartnerPersistence {

    companion object {
        // when changing also change repository findTop*() methods
        const val MAX_PROJECT_PARTNERS = 30
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long, version: String?): OutputProjectPartnerDetail {
        return projectVersionUtils.fetch(version, projectPersistence.getProjectIdForPartner(id),
            currentVersionFetcher = {
                getPartnerOrThrow(id).toOutputProjectPartnerDetail()
            },
            previousVersionFetcher = { timestamp ->
                getPartnerHistoricalDetail(id, timestamp)
            }
        )
    }

    @Transactional(readOnly = true)
    override fun findAllByProjectId(projectId: Long, page: Pageable, version: String?): Page<OutputProjectPartner> {
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                projectPartnerRepository.findAllByProjectId(projectId, page).map { it.toOutputProjectPartner() }
            },
            previousVersionFetcher = { timestamp ->
                projectPartnerRepository.findAllByProjectId(projectId, page, timestamp).map { it.toOutputProjectPartnerHistoricalData() }
            }
        )
    }

    @Transactional(readOnly = true)
    override fun findAllByProjectIdForDropdown(projectId: Long, sort: Sort, version: String?): List<OutputProjectPartner> {
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                StreamSupport.stream(
                    projectPartnerRepository.findTop30ByProjectId(projectId, sort).spliterator(),
                    false
                ).map { it.toOutputProjectPartner() }.collect(Collectors.toList())
            },
            previousVersionFetcher = { timestamp ->
                StreamSupport.stream(
                    projectPartnerRepository.findTop30ByProjectIdSortBySortNumber(projectId, timestamp).spliterator(),
                    false
                ).map { it.toOutputProjectPartnerHistoricalData() }.collect(Collectors.toList())
            }
        )
    }

    // used for authorization
    @Transactional(readOnly = true)
    override fun getProjectIdForPartnerId(id: Long): Long {
        return getPartnerOrThrow(id).project.id
    }

    @Transactional(readOnly = true)
    override fun findAllByProjectId(projectId: Long): Iterable<OutputProjectPartnerDetail> {
        return projectPartnerRepository.findAllByProjectId(projectId).map { it.toOutputProjectPartnerDetail() }.toSet()
    }

    @Transactional
    override fun create(projectId: Long, projectPartner: InputProjectPartnerCreate): OutputProjectPartnerDetail {
        val project = projectRepo.findById(projectId).orElseThrow { ResourceNotFoundException("project") }
        val legalStatus = legalStatusRepo.findById(projectPartner.legalStatusId!!).orElseThrow { ResourceNotFoundException("legalstatus") }

        // to be possible to list all partners for dropdowns we decided to limit total amount of them
        if (projectPartnerRepository.countByProjectId(projectId) >= MAX_PROJECT_PARTNERS)
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "project.partner.max.allowed.count.reached"
            )

        // prevent multiple role LEAD_PARTNER entries
        if (projectPartner.role!!.isLead)
            validateLeadPartnerChange(projectId, projectPartner.oldLeadPartnerId)

        // prevent multiple partners with same abbreviation
        validatePartnerAbbreviationUnique(projectId, abbreviation = projectPartner.abbreviation!!)

        val partnerCreated = projectPartnerRepository.save(projectPartner.toEntity(project = project, legalStatus = legalStatus))
        // save translations for which the just created Id is needed
        projectPartnerRepository.save(
            partnerCreated.copy(
                translatedValues = projectPartner.combineTranslatedValues(partnerCreated.id))
        )
        updateSortByRole(projectId)
        // entity is attached, number will have been updated
        return partnerCreated.toOutputProjectPartnerDetail()
    }

    @Transactional
    override fun update(projectPartner: InputProjectPartnerUpdate): OutputProjectPartnerDetail {
        val oldProjectPartner = getPartnerOrThrow(projectPartner.id)
        val projectId = oldProjectPartner.project.id
        val legalStatus = legalStatusRepo.findById(projectPartner.legalStatusId!!).orElseThrow { ResourceNotFoundException("legalstatus") }

        val makingThisLead = !oldProjectPartner.role.isLead && projectPartner.role!!.isLead
        if (makingThisLead)
            validateLeadPartnerChange(projectId, projectPartner.oldLeadPartnerId)

        if (oldProjectPartner.abbreviation != projectPartner.abbreviation) {
            validatePartnerAbbreviationUnique(projectId, abbreviation = projectPartner.abbreviation!!)
        }

        val partnerUpdated = projectPartnerRepository.save(
            oldProjectPartner.copy(
                abbreviation = projectPartner.abbreviation!!,
                role = projectPartner.role!!,
                nameInOriginalLanguage = projectPartner.nameInOriginalLanguage,
                nameInEnglish = projectPartner.nameInEnglish,
                translatedValues = projectPartner.combineTranslatedValues(oldProjectPartner.id),
                partnerType = projectPartner.partnerType,
                legalStatus = legalStatus,
                vat = projectPartner.vat,
                vatRecovery = projectPartner.vatRecovery
            )
        )
        // update sorting if leadPartner changed
        if (projectPartner.oldLeadPartnerId != null || makingThisLead)
            updateSortByRole(projectId)

        return partnerUpdated.toOutputProjectPartnerDetail()
    }

    @Transactional
    override fun updatePartnerAddresses(partnerId: Long, addresses: Set<ProjectPartnerAddressDTO>): OutputProjectPartnerDetail {
        val projectPartner = getPartnerOrThrow(partnerId)
        return projectPartnerRepository.save(
            projectPartner.copy(
                addresses = addresses.mapTo(HashSet()) { it.toEntity(projectPartner) }
            )
        ).toOutputProjectPartnerDetail()
    }

    @Transactional
    override fun updatePartnerContacts(partnerId: Long, contacts: Set<InputProjectContact>): OutputProjectPartnerDetail {
        val projectPartner = getPartnerOrThrow(partnerId)
        return projectPartnerRepository.save(
            projectPartner.copy(
                contacts = contacts.mapTo(HashSet()) { it.toEntity(projectPartner) }
            )
        ).toOutputProjectPartnerDetail()
    }

    @Transactional
    override fun updatePartnerMotivation(partnerId: Long, motivation: ProjectPartnerMotivationDTO): OutputProjectPartnerDetail {
        val projectPartner = getPartnerOrThrow(partnerId)
        return projectPartnerRepository.save(
            projectPartner.copy(
                motivation = motivation.toEntity(projectPartner.id)
            )
        ).toOutputProjectPartnerDetail()
    }

    @Transactional
    override fun deletePartner(partnerId: Long) {
        val projectId = getPartnerOrThrow(partnerId).project.id
        projectPartnerRepository.deleteById(partnerId)
        updateSortByRole(projectId)
        projectAssociatedOrganizationService.refreshSortNumbers(projectId)
    }

    /**
     * sets or updates the sort number for all partners for the specified project.
     */
    private fun updateSortByRole(projectId: Long) {
        val sort = Sort.by(listOf(
            Sort.Order(Sort.Direction.ASC, "role"),
            Sort.Order(Sort.Direction.ASC, "id")
        ))

        val projectPartners = projectPartnerRepository.findTop30ByProjectId(projectId, sort)
            .mapIndexed { index, old -> old.copy(sortNumber = index.plus(1)) }
        projectPartnerRepository.saveAll(projectPartners)
    }

    private fun validateLeadPartnerChange(projectId: Long, oldLeadPartnerId: Long?) {
        if (oldLeadPartnerId == null)
            validateOnlyOneLeadPartner(projectId)
        else
            updateOldLeadPartner(projectId, oldLeadPartnerId)
    }

    private fun validatePartnerAbbreviationUnique(projectId: Long, abbreviation: String) {
        val partnerWithSameName = projectPartnerRepository.findFirstByProjectIdAndAbbreviation(projectId, abbreviation)
        if (partnerWithSameName.isPresent) {
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "project.partner.abbreviation.already.existing",
                i18nArguments = listOf(abbreviation)
            )
        }
    }

    /**
     * validate project partner to be saved: only one role LEAD should exist.
     */
    private fun validateOnlyOneLeadPartner(projectId: Long) {
        val projectPartner = projectPartnerRepository.findFirstByProjectIdAndRole(projectId, ProjectPartnerRole.LEAD_PARTNER)
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
        val oldLeadPartner = projectPartnerRepository.findFirstByProjectIdAndRole(projectId, ProjectPartnerRole.LEAD_PARTNER)
            .orElseThrow { ResourceNotFoundException("projectPartner") }

        if (oldLeadPartner.id == oldLeadPartnerId)
            projectPartnerRepository.save(oldLeadPartner.copy(role = ProjectPartnerRole.PARTNER))
        else
            throw I18nValidationException(
                httpStatus = HttpStatus.UNPROCESSABLE_ENTITY,
                i18nKey = "project.partner.oldLeadPartnerId.is.not.lead"
            )
    }

    private fun getPartnerOrThrow(partnerId: Long): ProjectPartnerEntity {
        return projectPartnerRepository.findById(partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }
    }

    private fun getPartnerHistoricalDetail(
        partnerId: Long,
        timestamp: Timestamp,
    ): OutputProjectPartnerDetail {
        val addresses = projectPartnerRepository.findPartnerAddressesByIdAsOfTimestamp(partnerId, timestamp)
            .toProjectPartnerAddressHistoricalData()
        val contacts = projectPartnerRepository.findPartnerContactsByIdAsOfTimestamp(partnerId, timestamp)
            .toProjectPartnerContactHistoricalData()
        val motivation = projectPartnerRepository.findPartnerMotivationByIdAsOfTimestamp(partnerId, timestamp)
            .toProjectPartnerMotivationHistoricalData()
        return projectPartnerRepository.findPartnerIdentityByIdAsOfTimestamp(partnerId, timestamp)
            .toProjectPartnerDetailHistoricalData(addresses, contacts, motivation)
    }

}