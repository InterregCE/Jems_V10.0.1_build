package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.programme.repository.stateaid.ProgrammeStateAidRepository
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.state_aid.ProjectPartnerStateAidEntity
import io.cloudflight.jems.server.project.repository.ApplicationVersionNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.workpackage.activity.WorkPackageActivityRepository
import io.cloudflight.jems.server.project.repository.workpackage.activity.toActivityHistoricalData
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
    private val workPackageActivityRepository: WorkPackageActivityRepository,
    private val programmeStateAidRepository: ProgrammeStateAidRepository
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
        getPartnerOrThrow(projectPartner.id!!).let { entity ->
            projectPartnerRepository.save(
                entity.copy(
                    projectPartner = projectPartner,
                    legalStatusRef = legalStatusRepo.getReferenceIfExistsOrThrow(projectPartner.legalStatusId)
                )
            ).also { updateSortByRole(entity.project.id) }
        }.toProjectPartnerDetail()


    @Transactional
    override fun updatePartnerAddresses(
        partnerId: Long, addresses: Set<ProjectPartnerAddress>
    ): ProjectPartnerDetail =
        projectPartnerRepository.save(
            getPartnerOrThrow(partnerId).copy(newAddresses = addresses)
        ).toProjectPartnerDetail()


    @Transactional
    override fun updatePartnerContacts(
        partnerId: Long, contacts: Set<ProjectPartnerContact>
    ): ProjectPartnerDetail =
        projectPartnerRepository.save(
            getPartnerOrThrow(partnerId).copy(newContacts = contacts)
        ).toProjectPartnerDetail()

    @Transactional
    override fun updatePartnerMotivation(
        partnerId: Long, motivation: ProjectPartnerMotivation
    ): ProjectPartnerDetail =
        projectPartnerRepository.save(
            getPartnerOrThrow(partnerId).copy(newMotivation = motivation)
        ).toProjectPartnerDetail()


    @Transactional(readOnly = true)
    override fun getPartnerStateAid(partnerId: Long, version: String?): ProjectPartnerStateAid {
        val projectId = getProjectIdForPartnerId(partnerId, version)
        return projectVersionUtils.fetch(version,
            projectId = projectId,
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
    override fun updatePartnerStateAid(partnerId: Long, stateAid: ProjectPartnerStateAid): ProjectPartnerStateAid {
        val workPackageActivities =
            stateAid.activities?.map { workPackageActivityRepository.getReferenceIfExistsOrThrow(it.activityId) }
                .orEmpty()
                .filterNotNull()
        return projectPartnerStateAidRepository.save(stateAid.toEntity(
            partnerId = partnerId,
            workPackageActivities = workPackageActivities,
            programmeStateAid = programmeStateAidRepository.getReferenceIfExistsOrThrow(stateAid.stateAidScheme?.id)
        )).toModel()
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
        val sort = Sort.by(
            listOf(
                Sort.Order(Sort.Direction.ASC, "role"),
                Sort.Order(Sort.Direction.ASC, "id")
            )
        )

        projectPartnerRepository.findTop30ByProjectId(projectId, sort)
            .forEachIndexed { index, old -> old.sortNumber = index.plus(1) }
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
    ): ProjectPartnerStateAid {
        val activityIds =
            projectPartnerStateAidRepository.findPartnerStateAidActivitiesByPartnerIdAsOfTimestamp(partnerId, timestamp)
        val workPackageActivities =
            workPackageActivityRepository.findAllByActivityIdInAsOfTimestamp(activityIds, timestamp)
        val partnerStateAidRows =
            projectPartnerStateAidRepository.findPartnerStateAidByIdAsOfTimestamp(partnerId, timestamp)
        val programmeStateAidId = partnerStateAidRows.firstOrNull { it.stateAidId != null }?.stateAidId
        val programmeStateAid =
            if (programmeStateAidId != null) { programmeStateAidRepository.findById(programmeStateAidId) } else { null }

        return partnerStateAidRows.toModel(workPackageActivities.toActivityHistoricalData(), programmeStateAid?.get())
            ?: ProjectPartnerStateAid(answer1 = null, answer2 = null, answer3 = null, answer4 = null)
    }

    private fun getPartnerOrThrow(partnerId: Long): ProjectPartnerEntity {
        return projectPartnerRepository.findById(partnerId)
            .orElseThrow { ResourceNotFoundException("projectPartner") }
    }

    private fun getProjectIdIfExistedOrThrow(partnerId: Long): Long {
        return projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(partnerId)
            ?: throw ResourceNotFoundException("projectPartner")
    }

}
