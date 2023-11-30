package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.payments.entity.PartnerWithContributionsRow
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.programme.repository.stateaid.ProgrammeStateAidRepository
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.entity.partner.PartnerIdentityRow
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partner.state_aid.ProjectPartnerStateAidEntity
import io.cloudflight.jems.server.project.entity.workpackage.activity.WorkPackageActivityRow
import io.cloudflight.jems.server.project.repository.ApplicationVersionNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectNotFoundException
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionPersistenceProvider
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.repository.workpackage.activity.WorkPackageActivityRepository
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionSpf
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerPaymentSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.utils.partner.CREATED_AT_TIMESTAMP
import io.cloudflight.jems.server.utils.partner.PARTNER_ID
import io.cloudflight.jems.server.utils.partner.PROJECT_ID
import io.cloudflight.jems.server.utils.partner.ProjectPartnerTestUtil.Companion.project
import io.cloudflight.jems.server.utils.partner.activityEntity
import io.cloudflight.jems.server.utils.partner.activitySummary
import io.cloudflight.jems.server.utils.partner.legalStatusEntity
import io.cloudflight.jems.server.utils.partner.partnerDetailRows
import io.cloudflight.jems.server.utils.partner.programmeStateAidEntity
import io.cloudflight.jems.server.utils.partner.projectPartner
import io.cloudflight.jems.server.utils.partner.projectPartnerDetail
import io.cloudflight.jems.server.utils.partner.projectPartnerEntity
import io.cloudflight.jems.server.utils.partner.projectPartnerSummary
import io.cloudflight.jems.server.utils.partner.projectPartnerWithOrganizationEntity
import io.cloudflight.jems.server.utils.partner.stateAid
import io.cloudflight.jems.server.utils.partner.stateAidActivity
import io.cloudflight.jems.server.utils.partner.stateAidEmpty
import io.cloudflight.jems.server.utils.partner.stateAidEntity
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.Optional

class PartnerPersistenceProviderTest {

    private lateinit var projectVersionUtils: ProjectVersionUtils

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var legalStatusRepo: ProgrammeLegalStatusRepository

    @MockK
    lateinit var projectRepository: ProjectRepository

    @MockK
    lateinit var projectPartnerStateAidRepository: ProjectPartnerStateAidRepository

    @MockK
    lateinit var projectAssociatedOrganizationService: ProjectAssociatedOrganizationService

    @MockK
    lateinit var workPackageActivityRepository: WorkPackageActivityRepository

    @MockK
    lateinit var programmeStateAidRepository: ProgrammeStateAidRepository

    @RelaxedMockK
    lateinit var projectVersionPersistenceProvider: ProjectVersionPersistenceProvider

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    @MockK
    lateinit var partner: ProjectPartnerEntity

    lateinit var persistence: PartnerPersistenceProvider

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = PartnerPersistenceProvider(
            projectVersionUtils,
            projectPartnerRepository,
            legalStatusRepo,
            projectRepository,
            projectPartnerStateAidRepository,
            projectAssociatedOrganizationService,
            workPackageActivityRepository,
            programmeStateAidRepository,
            projectVersionPersistenceProvider,
            projectVersionRepo
        )
        //for all delete tests
        every { projectAssociatedOrganizationService.refreshSortNumbers(any()) } answers {}
    }


    @Test
    fun `should throw ResourceNotFoundException when partner does not exist`() {
        every { projectPartnerRepository.findById(-1) } returns Optional.empty()
        every { projectPartnerRepository.findById(1) } returns Optional.of(projectPartnerEntity())
        every { projectPartnerRepository.getProjectIdForPartner(-1) } throws ResourceNotFoundException("partner")
        every { projectPartnerRepository.getProjectIdForPartner(1) } returns PARTNER_ID

        assertThrows<ResourceNotFoundException> { persistence.getById(-1, null) }
        assertThat(persistence.getById(1, null)).isEqualTo(projectPartnerDetail(PARTNER_ID))
    }

    @Test
    fun `should change role of lead partner to partner in the project if it exists`() {
        val projectPartnerEntity = projectPartnerEntity()
        every {
            projectPartnerRepository.findFirstByProjectIdAndRole(PROJECT_ID, ProjectPartnerRole.LEAD_PARTNER)
        } returns Optional.of(projectPartnerEntity)
        assertDoesNotThrow { persistence.changeRoleOfLeadPartnerToPartnerIfItExists(PROJECT_ID) }
    }

    @Test
    fun `should throw PartnerAbbreviationNotUnique when partner abbreviation already exists`() {
        val abbreviation = "abbreviation"
        every { projectPartnerRepository.existsByProjectIdAndAbbreviation(PROJECT_ID, abbreviation) } returns true
        assertThrows<PartnerAbbreviationNotUnique> {
            persistence.throwIfPartnerAbbreviationAlreadyExists(PROJECT_ID, abbreviation)
        }
    }

    @Test
    fun getByIdAndVersion() {
        val timestamp = Timestamp.valueOf(LocalDateTime.now())
        val version = "1.0"
        val mockPartnerIdentityRow: PartnerIdentityRow = mockk()
        every { mockPartnerIdentityRow.id } returns PARTNER_ID
        every { mockPartnerIdentityRow.active } returns true
        every { mockPartnerIdentityRow.projectId } returns PROJECT_ID
        every { mockPartnerIdentityRow.createdAt } returns CREATED_AT_TIMESTAMP
        every { mockPartnerIdentityRow.abbreviation } returns "partner"
        every { mockPartnerIdentityRow.role } returns ProjectPartnerRole.LEAD_PARTNER
        every { mockPartnerIdentityRow.sortNumber } returns 0
        every { mockPartnerIdentityRow.nameInOriginalLanguage } returns "test"
        every { mockPartnerIdentityRow.nameInEnglish } returns "test"
        every { mockPartnerIdentityRow.partnerType } returns ProjectTargetGroup.BusinessSupportOrganisation
        every { mockPartnerIdentityRow.partnerSubType } returns PartnerSubType.LARGE_ENTERPRISE
        every { mockPartnerIdentityRow.nace } returns NaceGroupLevel.A
        every { mockPartnerIdentityRow.otherIdentifierNumber } returns "12"
        every { mockPartnerIdentityRow.otherIdentifierDescription } returns null
        every { mockPartnerIdentityRow.pic } returns "009"
        every { mockPartnerIdentityRow.vat } returns "test vat"
        every { mockPartnerIdentityRow.language } returns null
        every { mockPartnerIdentityRow.department } returns null
        every { mockPartnerIdentityRow.vatRecovery } returns ProjectPartnerVatRecovery.Yes
        every { mockPartnerIdentityRow.legalStatusId } returns 1

        every { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(1) } returns 2
        every { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(-1) } returns null
        every { projectVersionRepo.findTimestampByVersion(2, "404") } returns null
        every { projectVersionRepo.findTimestampByVersion(2, version) } returns timestamp
        every { projectPartnerRepository.findPartnerAddressesByIdAsOfTimestamp(1, timestamp) } returns emptyList()
        every { projectPartnerRepository.findPartnerContactsByIdAsOfTimestamp(1, timestamp) } returns emptyList()
        every { projectPartnerRepository.findPartnerMotivationByIdAsOfTimestamp(1, timestamp) } returns emptyList()
        every { projectPartnerRepository.findPartnerIdentityByIdAsOfTimestamp(1, timestamp) } returns listOf(
            mockPartnerIdentityRow
        )

        // partner does not exist in any version
        assertThrows<ResourceNotFoundException> { persistence.getById(-1, version) }
        // no timestamp can be found for the specified partner->project
        assertThrows<ApplicationVersionNotFoundException> { persistence.getById(1, "404") }
        // historic version of partner returned (version found)
        assertThat(persistence.getById(1, version)).isEqualTo(projectPartnerDetail(address = mutableListOf()))
    }

    @Test
    fun findAllByProjectId() {
        every { projectPartnerRepository.findAllByProjectId(0, Pageable.unpaged()) } returns PageImpl(emptyList())
        every { projectPartnerRepository.findAllByProjectId(1, Pageable.unpaged()) } returns PageImpl(
            listOf(
                projectPartnerEntity()
            )
        )

        assertThat(persistence.findAllByProjectId(0, Pageable.unpaged(), null)).isEmpty()
        assertThat(persistence.findAllByProjectId(1, Pageable.unpaged(), null)).containsExactly(
            projectPartnerSummary()
        )
    }

    @Test
    fun findAllByProjectIdUnpaged() {
        every { projectPartnerRepository.findTop50ByProjectId(0) } returns PageImpl(emptyList())
        every { projectPartnerRepository.findTop50ByProjectId(1) } returns PageImpl(listOf(projectPartnerEntity()))

        assertThat(persistence.findTop50ByProjectId(0)).isEmpty()
        assertThat(persistence.findTop50ByProjectId(1)).containsExactly(projectPartnerDetail(id = PARTNER_ID))
    }

    @Test
    fun `should return partners detail for a particular version of the project when version is specified`() {
        val timestamp = Timestamp.valueOf(LocalDateTime.of(2020, 8, 15, 6, 0))
        val version = "1.0"

        every { projectVersionRepo.findTimestampByVersion(PROJECT_ID, version) } returns timestamp
        every { projectPartnerRepository.findByProjectIdAsOfTimestamp(PROJECT_ID, timestamp) } returns partnerDetailRows()

        assertThat(persistence.findTop50ByProjectId(PROJECT_ID, version)).containsExactly(projectPartnerDetail(id = PARTNER_ID))
    }

    @Test
    fun createProjectPartner() {
        val projectPartnerEntity = projectPartnerEntity()
        val projectPartnerRequest =
            ProjectPartner(0, "partner", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
        val projectPartnerWithProject = ProjectPartnerEntity(
            2,
            active = true,
            project,
            projectPartnerRequest.abbreviation!!,
            projectPartnerRequest.role!!,
            legalStatus = legalStatusEntity
        )
        every { projectRepository.getById(PROJECT_ID) } returns project
        every { legalStatusRepo.getById(legalStatusEntity.id) } returns legalStatusEntity

        every { projectPartnerRepository.save(any()) } returns projectPartnerEntity
        // also handle sorting
        val projectPartners = listOf(projectPartnerEntity, projectPartnerWithProject)
        every { projectPartnerRepository.findTop50ByProjectId(1, any()) } returns projectPartners
        every { projectPartnerRepository.saveAll(any<Iterable<ProjectPartnerEntity>>()) } returnsArgument 0

        assertThat(persistence.create(PROJECT_ID, projectPartnerRequest, true)).isEqualTo(projectPartnerDetail(sortNumber = 1))
    }

    @Test
    fun `should not resort partners on creating new partner when resortByRole is false`() {
        val projectPartnerEntity = projectPartnerEntity()
        val projectPartnerRequest =
            ProjectPartner(0, "partner", ProjectPartnerRole.LEAD_PARTNER, legalStatusId = 1)
        every { projectRepository.getById(PROJECT_ID) } returns project
        every { legalStatusRepo.getById(legalStatusEntity.id) } returns legalStatusEntity
        every { projectPartnerRepository.save(any()) } returns projectPartnerEntity
        every { projectPartnerRepository.countByProjectId(any()) } returns 2

        assertThat(persistence.create(PROJECT_ID, projectPartnerRequest, false)).isEqualTo(projectPartnerDetail(sortNumber = 2))
        verify (atLeast = 0, atMost = 0) {projectPartnerRepository.findTop50ByProjectId(PROJECT_ID)}
    }

    @Test
    fun `createProjectPartner not existing`() {

        every { projectRepository.getById(PROJECT_ID) } throws ProjectNotFoundException()
        every { legalStatusRepo.getById(1) } returns legalStatusEntity
        every { projectPartnerRepository.save(any()) } returns projectPartnerEntity()
        assertThrows<ProjectNotFoundException> { persistence.create(PROJECT_ID, projectPartner(), true) }

    }

    @Test
    fun createProjectPartnerWithOrganization() {
        val projectPartnerRequest = projectPartner(
            department = setOf(InputTranslation(EN, "test"))
        )
        val updatedEntity = projectPartnerWithOrganizationEntity()
        every { projectRepository.getById(0) } throws ProjectNotFoundException()
        every { projectRepository.getById(PROJECT_ID) } returns project
        every { legalStatusRepo.getById(legalStatusEntity.id) } returns legalStatusEntity
        every { projectPartnerRepository.save(any()) } returns updatedEntity
        // also handle sorting
        val projectPartners = listOf(projectPartnerEntity(), updatedEntity)
        every { projectPartnerRepository.findTop50ByProjectId(PROJECT_ID, any()) } returns projectPartners

        assertThrows<ProjectNotFoundException> { persistence.create(0, projectPartnerRequest, true) }
        assertThat(
            persistence.create(PROJECT_ID, projectPartnerRequest, true)
        ).isEqualTo(projectPartnerDetail(department = setOf(InputTranslation(EN, "test")), sortNumber = 2))
    }

    @Test
    fun deleteProjectPartnerWithOrganization() {
        val projectPartnerWithOrganization =projectPartnerWithOrganizationEntity()
        every { projectPartnerRepository.findById(projectPartnerWithOrganization.id) } returns Optional.of(
            projectPartnerWithOrganization
        )
        every { projectPartnerRepository.deleteById(projectPartnerWithOrganization.id) } returns Unit
        every {
            projectPartnerRepository.findTop50ByProjectId(project.id, any())
        } returns emptySet()
        every { projectPartnerRepository.saveAll(emptyList()) } returns emptyList()

        assertDoesNotThrow { persistence.deletePartner(projectPartnerWithOrganization.id) }
        verify { projectAssociatedOrganizationService.refreshSortNumbers(project.id) }
    }

    @Test
    fun deleteProjectPartnerWithoutOrganization() {
        every { projectPartnerRepository.findById(PARTNER_ID) } returns Optional.of(projectPartnerEntity())
        every { projectPartnerRepository.deleteById(PARTNER_ID) } returns Unit
        every {
            projectPartnerRepository.findTop50ByProjectId(project.id, any())
        } returns emptySet()
        every { projectPartnerRepository.saveAll(emptyList()) } returns emptyList()

        assertDoesNotThrow { persistence.deletePartner(PARTNER_ID) }
        verify { projectAssociatedOrganizationService.refreshSortNumbers(project.id) }
    }

    @Test
    fun deleteProjectPartner_notExisting() {
        every { projectPartnerRepository.findById(-1) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { persistence.deletePartner(-1) }
    }

    @Test
    fun `get ProjectId for Partner`() {
        val entity: ProjectPartnerEntity = mockk()
        every { entity.project.id } returns 2
        every { projectPartnerRepository.findById(1) } returns Optional.of(entity)
        assertThat(persistence.getProjectIdForPartnerId(1)).isEqualTo(2)
    }

    @Test
    fun `get ProjectId for Partner - not existing`() {
        every { projectPartnerRepository.findById(1) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { persistence.getProjectIdForPartnerId(1) }
        assertThat(ex.entity).isEqualTo("projectPartner")
    }

    @Test
    fun `get ProjectId for historic Partner`() {
        every { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(1) } returns 2
        assertThat(persistence.getProjectIdForPartnerId(1, "1.0")).isEqualTo(2)
    }

    @Test
    fun `get ProjectId for historic Partner - not existing`() {
        every { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(PARTNER_ID) } returns null
        assertThrows<ResourceNotFoundException> { persistence.getProjectIdForPartnerId(PARTNER_ID, "404") }
    }

    @Test
    fun `get state aid`() {
        every { partner.project.id } returns 25L
        every { projectPartnerRepository.findById(PARTNER_ID) } returns Optional.of(partner)
        every { projectPartnerStateAidRepository.findById(PARTNER_ID) } returns Optional.of(stateAidEntity)

        assertThat(persistence.getPartnerStateAid(PARTNER_ID, null))
            .isEqualTo(stateAid)
    }

    @Test
    fun `get state aid - not existing`() {
        every { partner.project.id } returns 95L
        every { projectPartnerRepository.findById(PARTNER_ID) } returns Optional.of(partner)
        every { projectPartnerStateAidRepository.findById(PARTNER_ID) } returns Optional.empty()

        assertThat(persistence.getPartnerStateAid(PARTNER_ID, null))
            .isEqualTo(stateAidEmpty)
    }

    @Test
    fun `get state aid - historical`() {
        val version = "some historical version"
        val timestamp = Timestamp.valueOf(ZonedDateTime.now().toLocalDateTime())

        every { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(PARTNER_ID) } returns 909L
        every { projectVersionRepo.findTimestampByVersion(909L, version) } returns timestamp
        every {
            projectPartnerStateAidRepository.findPartnerStateAidByIdAsOfTimestamp(PARTNER_ID, timestamp)
        } returns listOf(
            PartnerStateAidRowTest(EN, PARTNER_ID, answer1 = true, answer2 = false, justification1 = "justification1", stateAidId = 2L),
            PartnerStateAidRowTest(EN, PARTNER_ID, answer1 = true, answer2 = false, justification2 = "justification2"),
        )
        every {
            projectPartnerStateAidRepository.findPartnerStateAidActivitiesByPartnerIdAsOfTimestamp(PARTNER_ID, timestamp)
        } returns listOf(3)
        every {
            workPackageActivityRepository.findAllByActivityIdInAsOfTimestamp(listOf(3), timestamp)
        } returns listOf(
            WorkPackageActivityRowImpl(3L, EN, 1L, 10, 3, null, null, null, null, false, null))
        every { programmeStateAidRepository.findById(2L) } returns Optional.of(programmeStateAidEntity)

        assertThat(persistence.getPartnerStateAid(PARTNER_ID, version))
            .isEqualTo(stateAidActivity)
    }

    data class WorkPackageActivityRowImpl(
        override val id: Long,
        override val language: SystemLanguage?,
        override val workPackageId: Long,
        override val workPackageNumber: Int?,
        override val activityNumber: Int,
        override val startPeriod: Int?,
        override val endPeriod: Int?,
        override val title: String?,
        override val description: String?,
        override val deactivated: Boolean,
        override val partnerId: Long?
    ) : WorkPackageActivityRow

    @Test
    fun `get state aid - historical but not existing`() {
        val version = "some historical version"
        val timestamp = Timestamp.valueOf(ZonedDateTime.now().toLocalDateTime())

        every { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(PARTNER_ID) } returns 1029L
        every { projectVersionRepo.findTimestampByVersion(1029L, version) } returns timestamp
        every {
            projectPartnerStateAidRepository.findPartnerStateAidByIdAsOfTimestamp(PARTNER_ID, timestamp)
        } returns emptyList()
        every {
            projectPartnerStateAidRepository.findPartnerStateAidActivitiesByPartnerIdAsOfTimestamp(PARTNER_ID, timestamp)
        } returns emptyList()
        every {
            workPackageActivityRepository.findAllByActivityIdInAsOfTimestamp(emptyList(), timestamp)
        } returns emptyList()

        assertThat(persistence.getPartnerStateAid(PARTNER_ID, version))
            .isEqualTo(stateAidEmpty)
    }

    @Test
    fun `update state aid`() {
        every { workPackageActivityRepository.getById(activitySummary.activityId) } returns activityEntity
        every { programmeStateAidRepository.getById(stateAidActivity.stateAidScheme?.id!!) } returns programmeStateAidEntity
        val stateAidEntitySlot = slot<ProjectPartnerStateAidEntity>()
        every { projectPartnerStateAidRepository.save(capture(stateAidEntitySlot)) } returnsArgument 0

        assertThat(persistence.updatePartnerStateAid(PARTNER_ID, stateAidActivity))
            .isEqualTo(stateAidActivity)

        assertThat(stateAidEntitySlot.captured.partnerId).isEqualTo(PARTNER_ID)
        assertThat(stateAidEntitySlot.captured.answer1).isTrue
        assertThat(stateAidEntitySlot.captured.answer2).isFalse
        assertThat(stateAidEntitySlot.captured.answer3).isNull()
        assertThat(stateAidEntitySlot.captured.answer4).isNull()
        assertThat(stateAidEntitySlot.captured.translatedValues).hasSize(1)
        assertThat(stateAidEntitySlot.captured.activities?.map { it.id.activity }).containsExactly(activityEntity)
    }

    @Test
    fun `update state aid - historical but not existing`() {
        val stateAidEntitySlot = slot<ProjectPartnerStateAidEntity>()
        every { projectPartnerStateAidRepository.save(capture(stateAidEntitySlot)) } returnsArgument 0

        assertThat(persistence.updatePartnerStateAid(PARTNER_ID, stateAid)).isEqualTo(stateAid)

        assertThat(stateAidEntitySlot.captured.partnerId).isEqualTo(PARTNER_ID)
        assertThat(stateAidEntitySlot.captured.answer1).isTrue
        assertThat(stateAidEntitySlot.captured.answer2).isFalse
        assertThat(stateAidEntitySlot.captured.answer3).isNull()
        assertThat(stateAidEntitySlot.captured.answer4).isNull()
        assertThat(stateAidEntitySlot.captured.translatedValues).hasSize(2)
    }

    @Test
    fun `should throw PartnerNotFoundInProjectException when partner does not exist in the project`() {
        every { projectPartnerRepository.existsByProjectIdAndId(PROJECT_ID, PARTNER_ID) } returns false
        assertThrows<PartnerNotFoundInProjectException> {
            (persistence.throwIfNotExistsInProject(PROJECT_ID, PARTNER_ID))
        }
    }

    @Test
    fun `should return Unit when partner exists in the project`() {
        every { projectPartnerRepository.existsByProjectIdAndId(PROJECT_ID, PARTNER_ID) } returns true
        assertThat(persistence.throwIfNotExistsInProject(PROJECT_ID, PARTNER_ID)).isEqualTo(Unit)
    }

    @Test
    fun `should deactivate partner when there is no problem`() {
        val partnerEntity: ProjectPartnerEntity = mockk()
        every { partnerEntity.active = false } returns Unit
        every { projectPartnerRepository.getById(PARTNER_ID) } returns partnerEntity
        assertDoesNotThrow {(persistence.deactivatePartner(PARTNER_ID))}
        verify(exactly = 1) { partnerEntity.active = false  }
    }


    @Test
    fun findAllByProjectIdWithContributionsForDropdown() {
        val partnerWithContributionsRow1: PartnerWithContributionsRow = mockk()
        val timestamp = Timestamp.valueOf(LocalDateTime.now())
        val version = "1.0"
        every { partnerWithContributionsRow1.partnerId } returns 1L
        every { partnerWithContributionsRow1.partnerAbbreviation } returns "A"
        every { partnerWithContributionsRow1.partnerRole } returns ProjectPartnerRole.PARTNER
        every { partnerWithContributionsRow1.partnerActive } returns true
        every { partnerWithContributionsRow1.partnerSortNumber } returns 1
        every { partnerWithContributionsRow1.fundId } returns 2L
        every { partnerWithContributionsRow1.fundAbbreviation } returns "ERDF"
        every { partnerWithContributionsRow1.language } returns EN
        every { partnerWithContributionsRow1.partnerContributionId } returns 2L
        every { partnerWithContributionsRow1.partnerContributionName } returns "contribution source"
        every { partnerWithContributionsRow1.partnerContributionStatus } returns ProjectPartnerContributionStatus.Public
        every { partnerWithContributionsRow1.partnerContributionAmount } returns BigDecimal(100)
        every { partnerWithContributionsRow1.partnerContributionSpfId } returns 1L
        every { partnerWithContributionsRow1.partnerContributionSpfName } returns "spf contribution"
        every { partnerWithContributionsRow1.partnerContributionSpfStatus } returns ProjectPartnerContributionStatus.Public
        every { partnerWithContributionsRow1.partnerContributionSpfAmount } returns BigDecimal(150)

        val partnerWithContributionsRow2: PartnerWithContributionsRow = mockk()
        every { partnerWithContributionsRow2.partnerId } returns 1L
        every { partnerWithContributionsRow2.partnerAbbreviation } returns "A"
        every { partnerWithContributionsRow2.partnerRole } returns ProjectPartnerRole.PARTNER
        every { partnerWithContributionsRow2.partnerActive } returns true
        every { partnerWithContributionsRow2.partnerSortNumber } returns 1
        every { partnerWithContributionsRow2.fundId } returns 2L
        every { partnerWithContributionsRow2.fundAbbreviation } returns "ERDF DE"
        every { partnerWithContributionsRow2.language } returns SystemLanguage.DE
        every { partnerWithContributionsRow2.partnerContributionId } returns 2L
        every { partnerWithContributionsRow2.partnerContributionName } returns "contribution source"
        every { partnerWithContributionsRow2.partnerContributionStatus } returns ProjectPartnerContributionStatus.Public
        every { partnerWithContributionsRow2.partnerContributionAmount } returns BigDecimal(100)
        every { partnerWithContributionsRow2.partnerContributionSpfId } returns 1L
        every { partnerWithContributionsRow2.partnerContributionSpfName } returns "spf contribution"
        every { partnerWithContributionsRow2.partnerContributionSpfStatus } returns ProjectPartnerContributionStatus.Public
        every { partnerWithContributionsRow2.partnerContributionSpfAmount } returns BigDecimal(150)

        every { projectVersionPersistenceProvider.getLatestApprovedOrCurrent(PROJECT_ID)} returns version
        every { projectVersionRepo.findTimestampByVersion(PROJECT_ID, version) } returns timestamp

        every { projectPartnerRepository.findAllByProjectIdWithContributionsForDropdownAsOfTimestamp(1L, any()) } returns listOf(
            partnerWithContributionsRow1,
            partnerWithContributionsRow2
        )

        assertThat(persistence.findAllByProjectIdWithContributionsForDropdown(1L, null)).containsExactly(
            ProjectPartnerPaymentSummary(
                partnerSummary = ProjectPartnerSummary(
                    id = 1L,
                    abbreviation = "A",
                    role = ProjectPartnerRole.PARTNER,
                    active = true,
                    sortNumber = 1
                ),
                partnerCoFinancing = listOf(
                    ProgrammeFund(
                        id = 2L,
                        selected = true,
                        abbreviation = setOf(
                            InputTranslation(language = EN, translation = "ERDF"),
                            InputTranslation(language = SystemLanguage.DE, translation = "ERDF DE")
                        )
                    )
                ),
                partnerContributions = listOf(
                    ProjectPartnerContribution(
                        id = 2L,
                        name = "contribution source",
                        status = ProjectPartnerContributionStatus.Public,
                        isPartner = true,
                        amount = BigDecimal(100)
                    )
                ),
                partnerContributionsSpf = listOf(ProjectPartnerContributionSpf(
                    id = 1L,
                    name = "spf contribution",
                    status = ProjectPartnerContributionStatus.Public,
                    amount = BigDecimal(150)
                ))
            )
        )
    }
}
