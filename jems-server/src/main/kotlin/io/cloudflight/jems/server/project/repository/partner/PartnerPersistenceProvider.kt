package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerTranslEntity
import io.cloudflight.jems.server.project.entity.partner.state_aid.ProjectPartnerStateAidEntity
import io.cloudflight.jems.server.project.repository.ApplicationVersionNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerAddress
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerContact
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerMotivation
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.util.stream.Collectors
import java.util.stream.StreamSupport

@Repository
class PartnerPersistenceProvider(
    private val projectVersionUtils: ProjectVersionUtils,
    private val projectPartnerRepository: ProjectPartnerRepository,
    private val legalStatusRepo: ProgrammeLegalStatusRepository,
    private val projectRepo: ProjectRepository,
    private val projectPartnerStateAidRepository: ProjectPartnerStateAidRepository,
    private val projectAssociatedOrganizationService: ProjectAssociatedOrganizationService,
) : PartnerPersistence {

    @Transactional(readOnly = true)
    override fun throwIfNotExistsInProject(projectId: Long, partnerId: Long) {
        if (!projectPartnerRepository.existsByProjectIdAndId(projectId, partnerId))
            throw PartnerNotFoundInProjectException(projectId, partnerId)
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long, version: String?): ProjectPartnerDetail {
        return projectVersionUtils.fetch(version,
            projectId = projectVersionUtils.fetchProjectId(version, id,
                currentVersionOnlyFetcher = { projectPartnerRepository.getProjectIdForPartner(id) },
                historicVersionFetcher = { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(id) }
            ),
            currentVersionFetcher = {
                getPartnerOrThrow(id).toProjectPartnerDetail()
            },
            previousVersionFetcher = { timestamp ->
                getPartnerHistoricalDetail(id, timestamp)
            }
        ) ?: throw ApplicationVersionNotFoundException()
    }

    @Transactional(readOnly = true)
    override fun findAllByProjectId(projectId: Long, page: Pageable, version: String?): Page<ProjectPartnerSummary> {
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                projectPartnerRepository.findAllByProjectId(projectId, page).map { it.toModel() }
            },
            previousVersionFetcher = { timestamp ->
                projectPartnerRepository.findAllByProjectIdAsOfTimestamp(projectId, page, timestamp)
                    .map { it.toProjectPartnerDTOHistoricalData() }
            }
        ) ?: Page.empty()
    }

    @Transactional(readOnly = true)
    override fun findAllByProjectIdForDropdown(
        projectId: Long,
        sort: Sort,
        version: String?
    ): List<ProjectPartnerSummary> {
        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                StreamSupport.stream(
                    projectPartnerRepository.findTop30ByProjectId(projectId, sort).spliterator(),
                    false
                ).map { it.toModel() }.collect(Collectors.toList())
            },
            previousVersionFetcher = { timestamp ->
                projectPartnerRepository.findTop30ByProjectIdSortBySortNumberAsOfTimestamp(projectId, timestamp)
                    .map { it.toProjectPartnerDTOHistoricalData() }
            }
        ) ?: emptyList()
    }

    // used for authorization
    @Transactional(readOnly = true)
    override fun getProjectIdForPartnerId(id: Long, version: String?): Long {
        if (version != null) {
            return getProjectIdIfExistedOrThrow(id)
        }
        return getPartnerOrThrow(id).project.id
    }

    @Transactional(readOnly = true)
    override fun findAllByProjectId(projectId: Long): Iterable<ProjectPartnerDetail> {
        return projectPartnerRepository.findAllByProjectId(projectId).map { it.toProjectPartnerDetail() }.toSet()
    }

    @Transactional(readOnly = true)
    override fun countByProjectId(projectId: Long): Long =
        projectPartnerRepository.countByProjectId(projectId)

    @Transactional
    override fun changeRoleOfLeadPartnerToPartnerIfItExists(projectId: Long) {
        projectPartnerRepository.findFirstByProjectIdAndRole(projectId, ProjectPartnerRole.LEAD_PARTNER).ifPresent {
            it.role = ProjectPartnerRole.PARTNER
        }.also {
            updateSortByRole(projectId)
        }
    }

    @Transactional(readOnly = true)
    override fun throwIfPartnerAbbreviationAlreadyExists(projectId: Long, abbreviation: String) {
        if (projectPartnerRepository.existsByProjectIdAndAbbreviation(projectId, abbreviation))
            throw PartnerAbbreviationNotUnique(abbreviation)
    }

    @Transactional
    override fun create(projectId: Long, projectPartner: ProjectPartner): ProjectPartnerDetail =
        projectPartnerRepository.save(
            projectPartner.toEntity(
                project = projectRepo.getReferenceIfExistsOrThrow(projectId),
                legalStatus = legalStatusRepo.getReferenceIfExistsOrThrow(projectPartner.legalStatusId)
            )
        ).also { updateSortByRole(projectId) }.toProjectPartnerDetail()


    @Transactional
    override fun update(projectPartner: ProjectPartner): ProjectPartnerDetail =
        getPartnerOrThrow(projectPartner.id!!).also { oldPartner ->

            oldPartner.abbreviation = projectPartner.abbreviation!!
            oldPartner.role = projectPartner.role!!
            oldPartner.nameInOriginalLanguage = projectPartner.nameInOriginalLanguage
            oldPartner.nameInEnglish = projectPartner.nameInEnglish
            oldPartner.translatedValues = mutableSetOf<ProjectPartnerTranslEntity>().also {
                it.addPartnerTranslations(
                    oldPartner,
                    projectPartner.department,
                    projectPartner.otherIdentifierDescription
                )
            }
            oldPartner.partnerType = projectPartner.partnerType
            oldPartner.legalStatus = legalStatusRepo.getReferenceIfExistsOrThrow(projectPartner.legalStatusId!!)
            oldPartner.vat = projectPartner.vat
            oldPartner.vatRecovery = projectPartner.vatRecovery

        }.toProjectPartnerDetail()


    @Transactional
    override fun updatePartnerAddresses(
        partnerId: Long,
        addresses: Set<ProjectPartnerAddress>
    ): ProjectPartnerDetail {
        val projectPartner = getPartnerOrThrow(partnerId)
        return projectPartnerRepository.save(
            projectPartner.copy(
                addresses = addresses.mapTo(HashSet()) { it.toEntity(projectPartner) }
            )
        ).toProjectPartnerDetail()
    }

    @Transactional
    override fun updatePartnerContacts(
        partnerId: Long,
        contacts: Set<ProjectPartnerContact>
    ): ProjectPartnerDetail {
        val projectPartner = getPartnerOrThrow(partnerId)
        return projectPartnerRepository.save(
            projectPartner.copy(
                contacts = contacts.mapTo(HashSet()) { it.toEntity(projectPartner) }
            )
        ).toProjectPartnerDetail()
    }

    @Transactional
    override fun updatePartnerMotivation(
        partnerId: Long,
        motivation: ProjectPartnerMotivation
    ): ProjectPartnerDetail {
        val projectPartner = getPartnerOrThrow(partnerId)
        return projectPartnerRepository.save(
            projectPartner.copy(
                motivation = motivation.toEntity(projectPartner.id)
            )
        ).toProjectPartnerDetail()
    }

    @Transactional(readOnly = true)
    override fun getPartnerStateAid(partnerId: Long, version: String?): ProjectPartnerStateAid {
        return projectVersionUtils.fetch(version,
            projectId = getProjectIdForPartnerId(partnerId, version),
            currentVersionFetcher = {
                projectPartnerStateAidRepository.findById(partnerId)
                    .orElse(ProjectPartnerStateAidEntity(partnerId)).toModel()
            },
            previousVersionFetcher = { timestamp ->
                getPartnerStateAidHistorical(partnerId, timestamp)
            }
        ) ?: throw ApplicationVersionNotFoundException()
    }

    @Transactional
    override fun updatePartnerStateAid(partnerId: Long, stateAid: ProjectPartnerStateAid): ProjectPartnerStateAid =
        projectPartnerStateAidRepository.save(stateAid.toEntity(partnerId)).toModel()

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
        val sort = Sort.by(
            listOf(
                Sort.Order(Sort.Direction.ASC, "role"),
                Sort.Order(Sort.Direction.ASC, "id")
            )
        )

        val projectPartners = projectPartnerRepository.findTop30ByProjectId(projectId, sort)
            .mapIndexed { index, old -> old.copy(sortNumber = index.plus(1)) }
        projectPartnerRepository.saveAll(projectPartners)
    }

    private fun getPartnerHistoricalDetail(
        partnerId: Long,
        timestamp: Timestamp,
    ): ProjectPartnerDetail {
        val addresses = projectPartnerRepository.findPartnerAddressesByIdAsOfTimestamp(partnerId, timestamp)
            .toProjectPartnerAddressHistoricalData()
        val contacts = projectPartnerRepository.findPartnerContactsByIdAsOfTimestamp(partnerId, timestamp)
            .toProjectPartnerContactHistoricalData()
        val motivation = projectPartnerRepository.findPartnerMotivationByIdAsOfTimestamp(partnerId, timestamp)
            .toProjectPartnerMotivationHistoricalData()
        return projectPartnerRepository.findPartnerIdentityByIdAsOfTimestamp(partnerId, timestamp)
            .toProjectPartnerDetailHistoricalData(addresses, contacts, motivation)
    }

    private fun getPartnerStateAidHistorical(
        partnerId: Long,
        timestamp: Timestamp,
    ): ProjectPartnerStateAid =
        projectPartnerStateAidRepository.findPartnerStateAidByIdAsOfTimestamp(partnerId, timestamp).toModel()
            ?: ProjectPartnerStateAid(answer1 = null, answer2 = null, answer3 = null, answer4 = null)

    private fun getPartnerOrThrow(partnerId: Long): ProjectPartnerEntity {
        return projectPartnerRepository.findById(partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }
    }

    private fun getProjectIdIfExistedOrThrow(partnerId: Long): Long {
        return projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(partnerId)
            ?: throw ResourceNotFoundException("projectPartner")
    }

}
