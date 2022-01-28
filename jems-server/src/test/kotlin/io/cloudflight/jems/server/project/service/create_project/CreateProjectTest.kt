package io.cloudflight.jems.server.project.service.create_project

import io.cloudflight.jems.api.audit.dto.AuditAction.APPLICATION_STATUS_CHANGED
import io.cloudflight.jems.api.audit.dto.AuditAction.APPLICATION_VERSION_RECORDED
import io.cloudflight.jems.api.audit.dto.AuditAction.CALL_ALREADY_ENDED
import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.api.programme.dto.ProgrammeDataDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.ProgrammeDataService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.DRAFT
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_DRAFT
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectDetail
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.project.service.model.ProjectVersion
import io.cloudflight.jems.server.project.service.save_project_version.CreateNewProjectVersionInteractor
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.project.entity.projectuser.CollaboratorLevel
import io.cloudflight.jems.server.project.entity.projectuser.CollaboratorLevel.MANAGE
import io.cloudflight.jems.server.project.service.model.ProjectVersionSummary
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import io.cloudflight.jems.server.project.service.projectuser.UserProjectCollaboratorPersistence
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.time.ZonedDateTime

internal class CreateProjectTest : UnitTest() {

    companion object {
        private const val CALL_ID = 54L
        private const val USER_ID = 18L
        private const val PROJECT_ID = 29L

        private val startDate = ZonedDateTime.now().minusDays(3)
        private val endDateStep1 = ZonedDateTime.now().plusDays(2)
        private val endDate = ZonedDateTime.now().plusDays(4)

        private val call = CallDetail(
            id = CALL_ID,
            name = "call name",
            status = CallStatus.PUBLISHED,
            type = CallType.STANDARD,
            startDate = startDate,
            endDateStep1 = endDateStep1,
            endDate = endDate,
            isAdditionalFundAllowed = true,
            lengthOfPeriod = 12,
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null
        )

        val callSettings = ProjectCallSettings(
            callId = CALL_ID,
            callName = "call name",
            startDate = call.startDate,
            endDateStep1 = call.endDateStep1,
            endDate = call.endDate,
            lengthOfPeriod = call.lengthOfPeriod!!,
            isAdditionalFundAllowed = call.isAdditionalFundAllowed,
            flatRates = emptySet(),
            lumpSums = emptyList(),
            unitCosts = emptyList(),
            stateAids = emptyList(),
            applicationFormFieldConfigurations = mutableSetOf(),
            preSubmissionCheckPluginKey = null
        )

        private val userEntity = UserEntity(
            id = USER_ID,
            email = "some@applicant",
            name ="",
            surname = "",
            userRole = UserRoleEntity(0, "role"),
            password = "",
            userStatus = UserStatus.ACTIVE
        )

        private fun projectVersion(status: ApplicationStatus): ProjectVersion {
            return ProjectVersion(
                version = "1.0",
                projectId = PROJECT_ID,
                createdAt = ZonedDateTime.now(),
                user = userEntity,
                status = status,
                current = true
            )
        }

        private fun projectVersionSummary(): ProjectVersionSummary {
            return ProjectVersionSummary(
                version = "1.0",
                projectId = PROJECT_ID,
                createdAt = ZonedDateTime.now(),
                user = userEntity,
            )
        }

        private fun dummyProjectWithStatus(acronym: String, status: ApplicationStatus): ProjectDetail {
            return ProjectDetail(
                id = PROJECT_ID,
                customIdentifier = "01",
                callSettings = callSettings,
                acronym = acronym,
                applicant = userEntity.toUserSummary(),
                projectStatus = ProjectStatus(id = 4587L, status = status, user = userEntity.toUserSummary(), updated = ZonedDateTime.now()),
                title = setOf(InputTranslation(SystemLanguage.EN, "title")),
                specificObjective = null,
                programmePriority = null
            )
        }

        private val expectedCallNotOpenAudit = AuditCandidate(
            action = CALL_ALREADY_ENDED,
            entityRelatedId = CALL_ID,
            description = "Attempted unsuccessfully to submit or to apply for call 'call name' (id=54) that is not open.",
        )

        private fun getProgrammeData(projectIdProgrammeAbbreviation: String?, projectIdUseCallId: Boolean) =
            ProgrammeDataDTO(
                "cci",
                "title",
                "version",
                2020,
                2024,
                null,
                null,
                null,
                null,
                null,
                null,
                projectIdProgrammeAbbreviation = projectIdProgrammeAbbreviation,
                projectIdUseCallId = projectIdUseCallId,
                emptyList(),
            )
    }

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var callPersistence: CallPersistence

    @MockK
    lateinit var collaboratorPersistence: UserProjectCollaboratorPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var programmeService: ProgrammeDataService

    @MockK
    lateinit var createNewProjectVersion: CreateNewProjectVersionInteractor

    @InjectMockKs
    lateinit var createProject: CreateProject

    @BeforeEach
    fun reset() {
        clearMocks(auditPublisher)
        clearMocks(projectPersistence)
        clearMocks(collaboratorPersistence)
    }

    @Test
    fun `createProject - 1STEP mode - everything is OK`() {
        every { callPersistence.getCallById(CALL_ID) } returns call.copy(endDateStep1 = null)
        every { securityService.currentUser!!.user.id } returns USER_ID
        every { projectPersistence.createProjectWithStatus("test application", DRAFT, USER_ID, CALL_ID) } returns
            dummyProjectWithStatus(acronym = "test application", status = DRAFT)
        every { programmeService.get() } returns getProgrammeData("SK-AT_", true)
        every { projectPersistence.updateProjectCustomIdentifier(PROJECT_ID, "SK-AT_5400029") } answers {}
        every { createNewProjectVersion.create(PROJECT_ID) } returns projectVersionSummary()

        val slot = mutableListOf<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slot)) } answers { }

        val usersToPersistSlot = slot<Map<Long, CollaboratorLevel>>()
        every { collaboratorPersistence.changeUsersAssignedToProject(PROJECT_ID, capture(usersToPersistSlot)) } returns emptyList()

        val result = createProject.createProject("test application", CALL_ID)

        assertThat(result.projectStatus.status).isEqualTo(DRAFT)
        assertThat(result.projectStatus.user).isEqualTo(userEntity.toUserSummary())
        assertThat(result.acronym).isEqualTo("test application")
        assertThat(result.applicant.email).isEqualTo("some@applicant")

        verify(exactly = 1) { projectPersistence.updateProjectCustomIdentifier(PROJECT_ID, "SK-AT_5400029") }
        verify(exactly = 2) { auditPublisher.publishEvent(any()) }
        assertThat(slot[0].auditCandidate).isEqualTo(
            AuditCandidate(
                action = APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = "29", customIdentifier = "01", name = "test application"),
                description = "Project application created with status DRAFT",
            )
        )
        assertThat(slot[1].auditCandidate.action).isEqualTo(APPLICATION_VERSION_RECORDED)
        assertThat(slot[1].auditCandidate.project).isEqualTo(
            AuditProject(
                id = "29",
                customIdentifier = "SK-AT_5400029",
                name = "test application"
            )
        )
        assertThat(slot[1].auditCandidate.description).startsWith("New project version \"V.1.0\" is recorded by user: some@applicant")

        verify(exactly = 1) { collaboratorPersistence.changeUsersAssignedToProject(PROJECT_ID, any()) }
        assertThat(usersToPersistSlot.captured).containsExactlyEntriesOf(mapOf(USER_ID to MANAGE))
    }

    @Test
    fun `createProject - 2STEP mode - everything is OK`() {
        every { callPersistence.getCallById(CALL_ID) } returns call
        every { securityService.currentUser!!.user.id } returns USER_ID
        val acronym = "test acronym"
        every { projectPersistence.createProjectWithStatus(acronym, STEP1_DRAFT, USER_ID, CALL_ID) } returns
            dummyProjectWithStatus(acronym = acronym, status = STEP1_DRAFT)
        every { programmeService.get() } returns getProgrammeData("CZ-DE", false)
        every { projectPersistence.updateProjectCustomIdentifier(PROJECT_ID, "CZ-DE00029") } answers {}
        every { createNewProjectVersion.create(PROJECT_ID) } returns projectVersionSummary()

        val slot = mutableListOf<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slot)) } answers { }

        val usersToPersistSlot = slot<Map<Long, CollaboratorLevel>>()
        every { collaboratorPersistence.changeUsersAssignedToProject(PROJECT_ID, capture(usersToPersistSlot)) } returns emptyList()

        val result = createProject.createProject(acronym, CALL_ID)

        assertThat(result.projectStatus.status).isEqualTo(STEP1_DRAFT)
        assertThat(result.projectStatus.user).isEqualTo(userEntity.toUserSummary())
        assertThat(result.acronym).isEqualTo(acronym)
        assertThat(result.applicant.email).isEqualTo("some@applicant")

        verify(exactly = 1) { projectPersistence.updateProjectCustomIdentifier(PROJECT_ID, "CZ-DE00029") }
        verify(exactly = 2) { auditPublisher.publishEvent(any()) }
        assertThat(slot[0].auditCandidate).isEqualTo(
            AuditCandidate(
                action = APPLICATION_STATUS_CHANGED,
                project = AuditProject(id = "29", customIdentifier = "01", name = acronym),
                description = "Project application created with status STEP1_DRAFT",
            )
        )
        assertThat(slot[1].auditCandidate.action).isEqualTo(APPLICATION_VERSION_RECORDED)
        assertThat(slot[1].auditCandidate.project).isEqualTo(
            AuditProject(
                id = "29",
                customIdentifier = "CZ-DE00029",
                name = acronym
            )
        )
        assertThat(slot[1].auditCandidate.description).startsWith("New project version \"V.1.0\" is recorded by user: some@applicant")

        verify(exactly = 1) { collaboratorPersistence.changeUsersAssignedToProject(PROJECT_ID, any()) }
        assertThat(usersToPersistSlot.captured).containsExactlyEntriesOf(mapOf(USER_ID to MANAGE))
    }

    @Test
    fun `createProject - null programme prefix`() {
        every { callPersistence.getCallById(CALL_ID) } returns call
        every { securityService.currentUser!!.user.id } returns USER_ID
        val acronym = "test acronym"
        every { projectPersistence.createProjectWithStatus(acronym, STEP1_DRAFT, USER_ID, CALL_ID) } returns
            dummyProjectWithStatus(acronym = acronym, status = STEP1_DRAFT)
        every { programmeService.get() } returns getProgrammeData(null, false)
        every { projectPersistence.updateProjectCustomIdentifier(PROJECT_ID, any()) } answers {}
        every { createNewProjectVersion.create(PROJECT_ID) } returns projectVersionSummary()
        every { auditPublisher.publishEvent(any()) } answers { }

        val usersToPersistSlot = slot<Map<Long, CollaboratorLevel>>()
        every { collaboratorPersistence.changeUsersAssignedToProject(PROJECT_ID, capture(usersToPersistSlot)) } returns emptyList()

        createProject.createProject(acronym, CALL_ID)
        verify(exactly = 1) { projectPersistence.updateProjectCustomIdentifier(PROJECT_ID, "00029") }

        verify(exactly = 1) { collaboratorPersistence.changeUsersAssignedToProject(PROJECT_ID, any()) }
        assertThat(usersToPersistSlot.captured).containsExactlyEntriesOf(mapOf(USER_ID to MANAGE))
    }

    @Test
    fun `createProject - call start not happen yet`() {
        every { callPersistence.getCallById(CALL_ID) } returns call.copy(startDate = ZonedDateTime.now().plusDays(5))

        val slot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slot)) } answers { }

        assertThrows<CallNotOpen> { createProject.createProject("test application", CALL_ID) }
        assertThat(slot.captured.auditCandidate).isEqualTo(expectedCallNotOpenAudit)
    }

    @Test
    fun `createProject - STEP1 mode - call end already happen`() {
        every { callPersistence.getCallById(CALL_ID) } returns call.copy(
            endDateStep1 = ZonedDateTime.now().minusDays(5)
        )

        val slot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slot)) } answers { }

        assertThrows<CallNotOpen> { createProject.createProject("test application", CALL_ID) }
        assertThat(slot.captured.auditCandidate).isEqualTo(expectedCallNotOpenAudit)
    }

    @Test
    fun `createProject - STEP2 mode - call end already happen`() {
        every { callPersistence.getCallById(CALL_ID) } returns call.copy(
            endDateStep1 = null,
            endDate = ZonedDateTime.now().minusDays(5)
        )

        val slot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(slot)) } answers { }

        assertThrows<CallNotOpen> { createProject.createProject("test application", CALL_ID) }
        assertThat(slot.captured.auditCandidate).isEqualTo(expectedCallNotOpenAudit)
    }

    @Test
    fun `createProject - call is not public`() {
        every { callPersistence.getCallById(CALL_ID) } returns call.copy(status = CallStatus.DRAFT)
        assertThrows<CallNotFound> { createProject.createProject("test application", CALL_ID) }
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

}
