package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateSetupDTO
import io.cloudflight.jems.api.project.dto.ProjectCallSettingsDTO
import io.cloudflight.jems.api.project.dto.ProjectDetailDTO
import io.cloudflight.jems.api.project.dto.file.OutputProjectFile
import io.cloudflight.jems.api.project.dto.status.ProjectStatusDTO
import io.cloudflight.jems.api.project.dto.file.ProjectFileType
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.ProjectDecisionDTO
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.programmeUser
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.file.FileStorageService
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.APPROVED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.APPROVED_WITH_CONDITIONS
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.DRAFT
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.ELIGIBLE
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.INELIGIBLE
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.NOT_APPROVED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.RETURNED_TO_APPLICANT
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_APPROVED_WITH_CONDITIONS
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_ELIGIBLE
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.STEP1_SUBMITTED
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.SUBMITTED
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectDecision
import io.cloudflight.jems.server.project.service.model.ProjectStatus
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.cloudflight.jems.server.user.service.model.UserSummary
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ProjectFileAuthorizationTest {

    companion object {

        private val ID_APPLICANT_FILE_2005 = 1L
        private val ID_ASSESSMENT_FILE_2005 = 2L
        private val ID_APPLICANT_FILE_2011 = 3L
        private val ID_ASSESSMENT_FILE_2011 = 4L

        private val year2005 = LocalDate.of(2005, 1, 1)
        private val year2008 = LocalDate.of(2008, 1, 1)
        private val year2011 = LocalDate.of(2011, 1, 1)

        private val applicantFile2005 = getFile(
            id = ID_APPLICANT_FILE_2005,
            type = ProjectFileType.APPLICANT_FILE,
            updated = year2005
        )
        private val assessmentFile2005 = getFile(
            id = ID_ASSESSMENT_FILE_2005,
            type = ProjectFileType.ASSESSMENT_FILE,
            updated = year2005
        )

        private val applicantFile2011 = getFile(
            id = ID_APPLICANT_FILE_2011,
            type = ProjectFileType.APPLICANT_FILE,
            updated = year2011
        )
        private val assessmentFile2011 = getFile(
            id = ID_ASSESSMENT_FILE_2011,
            type = ProjectFileType.ASSESSMENT_FILE,
            updated = year2011
        )

        private fun getFile(id: Long, type: ProjectFileType, updated: LocalDate): OutputProjectFile {
            return OutputProjectFile(
                id = id,
                name = "",
                author = OutputUser(null, "", "", ""),
                type = type,
                description = null,
                size = 0,
                updated = toZonedDate(updated)
            )
        }

        private val dummyCall = ProjectCallSettings(
            callId = 1,
            callName = "call",
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            endDateStep1 = null,
            lengthOfPeriod = 12,
            isAdditionalFundAllowed = false,
            flatRates = emptySet(),
            lumpSums = emptyList(),
            unitCosts = emptyList(),
        )

        private val dummyCallDto = ProjectCallSettingsDTO(
            callId = 1,
            callName = "call",
            startDate = ZonedDateTime.now(),
            endDate = ZonedDateTime.now(),
            endDateStep1 = null,
            lengthOfPeriod = 12,
            isAdditionalFundAllowed = false,
            flatRates = FlatRateSetupDTO(),
            lumpSums = emptyList(),
            unitCosts = emptyList(),
        )

        private fun getProject(id: Long, applicantId: Long, status: ApplicationStatus): Project {
            return Project(
                id = id,
                callSettings = dummyCall,
                acronym = "",
                applicant = UserSummary(applicantId, "", "", "", UserRoleSummary(name = "")),
                projectStatus = ProjectStatus(null, status, UserSummary(0, "", "", "", UserRoleSummary(name = "")), ZonedDateTime.now()),
                step2Active = false,
                duration = 4,
            )
        }

        private fun getProjectLastSubmitted(
            id: Long,
            applicantId: Long,
            status: ApplicationStatus,
            lastSubmitted: LocalDate
        ): Project {
            return getProject(id, applicantId, status)
                .copy(lastResubmission = ProjectStatus(
                    id = null,
                    status = SUBMITTED,
                    user = UserSummary(0, "", "", "", UserRoleSummary(name = "")),
                    updated = toZonedDate(lastSubmitted)
                ))
        }

        private fun dummyProjectWithStatus(statusDTO: ProjectStatusDTO): ProjectDetailDTO {
            return ProjectDetailDTO(
                id = null,
                callSettings = dummyCallDto,
                acronym = "",
                applicant = OutputUser(null, "", "", ""),
                projectStatus = statusDTO
            )
        }

        private fun getSubmissionAt(year: LocalDate): ProjectStatusDTO {
            return ProjectStatusDTO(
                id = null,
                status = ApplicationStatusDTO.SUBMITTED,
                user = OutputUser(null, "", "", ""),
                updated = toZonedDate(year)
            )
        }

        private fun toZonedDate(year: LocalDate) = ZonedDateTime.of(year, LocalTime.of(1, 1), ZoneId.systemDefault())
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @MockK
    lateinit var fileStorageService: FileStorageService

    @MockK
    lateinit var projectAuthorization: ProjectAuthorization

    lateinit var projectFileAuthorization: ProjectFileAuthorization

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectFileAuthorization = ProjectFileAuthorization(
            securityService,
            projectPersistence,
            fileStorageService,
            projectAuthorization
        )

        every { fileStorageService.getFileDetail(any(), eq(applicantFile2005.id!!)) } returns applicantFile2005
        every { fileStorageService.getFileDetail(any(), eq(applicantFile2011.id!!)) } returns applicantFile2011
        every { fileStorageService.getFileDetail(any(), eq(assessmentFile2005.id!!)) } returns assessmentFile2005
        every { fileStorageService.getFileDetail(any(), eq(assessmentFile2011.id!!)) } returns assessmentFile2011
    }

    @Test
    fun `canUpload programme user can upload anytime besides DRAFT`() {
        every { securityService.currentUser } returns programmeUser

        every { projectPersistence.getProject(eq(10L)) } returns getProject(10, 10, ApplicationStatus.DRAFT)
        every { projectAuthorization.canReadProject(eq(10L)) } throws ResourceNotFoundException("project")

        var exception = assertThrows<ResourceNotFoundException>(
            "${programmeUser.user.email} cannot upload ${ProjectFileType.ASSESSMENT_FILE} file, because he cannot retrieve project at all at status ${DRAFT})"
        ) { projectFileAuthorization.canUploadFile(10, ProjectFileType.ASSESSMENT_FILE) }
        assertThat(exception.entity).isEqualTo("project")

        exception = assertThrows<ResourceNotFoundException>(
            "${programmeUser.user.email} cannot upload ${ProjectFileType.APPLICANT_FILE} file, because he cannot retrieve project at all at status $DRAFT)"
        ) { projectFileAuthorization.canUploadFile(10, ProjectFileType.APPLICANT_FILE) }
        assertThat(exception.entity).isEqualTo("project")

        // #### status no-DRAFT

        val statusesWithoutDraft = ApplicationStatus.values().toMutableSet()
        statusesWithoutDraft.remove(DRAFT)

        statusesWithoutDraft.forEach {
            every { projectPersistence.getProject(eq(1L)) } returns getProject(1, 10, it)
            assertTrue(
                projectFileAuthorization.canUploadFile(1, ProjectFileType.ASSESSMENT_FILE),
                "${programmeUser.user.email} can upload ${ProjectFileType.ASSESSMENT_FILE} file when status is $it"
            )
            assertFalse(
                projectFileAuthorization.canUploadFile(1, ProjectFileType.APPLICANT_FILE),
                "${programmeUser.user.email} cannot upload ${ProjectFileType.APPLICANT_FILE} file when status is $it"
            )
        }
    }

    @Test
    fun `canUpload admin user can upload anytime`() {
        every { securityService.currentUser } returns adminUser

        assertTrue(projectFileAuthorization.canUploadFile(1, ProjectFileType.ASSESSMENT_FILE))
        assertTrue(projectFileAuthorization.canUploadFile(1, ProjectFileType.APPLICANT_FILE))
    }

    @Test
    fun `canUpload applicant`() {
        every { securityService.currentUser } returns applicantUser

        // is Owner
        listOf(DRAFT, RETURNED_TO_APPLICANT).forEach {
            every { projectPersistence.getApplicantAndStatusById(eq(1L)) } returns ProjectApplicantAndStatus(
                applicantUser.user.id!!,
                it
            )
            every { projectAuthorization.canReadProject(eq(1L)) } returns true

            assertFalse(
                projectFileAuthorization.canUploadFile(1, ProjectFileType.ASSESSMENT_FILE),
                "${applicantUser.user.email} as owner cannot upload ${ProjectFileType.ASSESSMENT_FILE} file (status was $it)"
            )
            assertTrue(
                projectFileAuthorization.canUploadFile(1, ProjectFileType.APPLICANT_FILE),
                "${applicantUser.user.email} as owner CAN upload ${ProjectFileType.APPLICANT_FILE} file when status is $it"
            )
        }

        // is NOT Owner
        listOf(DRAFT, RETURNED_TO_APPLICANT).forEach {
            every { projectPersistence.getApplicantAndStatusById(eq(2L)) } returns ProjectApplicantAndStatus(456L, it)
            every { projectAuthorization.canReadProject(eq(2L)) } throws ResourceNotFoundException("project")

            var exception = assertThrows<ResourceNotFoundException>(
                "${applicantUser.user.email} (as NOT owner) cannot upload ${ProjectFileType.ASSESSMENT_FILE} file, because he cannot retrieve project at all (status was $it)"
            ) { projectFileAuthorization.canUploadFile(2, ProjectFileType.ASSESSMENT_FILE) }
            assertThat(exception.entity).isEqualTo("project")

            exception = assertThrows<ResourceNotFoundException>(
                "${applicantUser.user.email} (as NOT owner) cannot upload ${ProjectFileType.APPLICANT_FILE} file, because he cannot retrieve project at all (status was $it)"
            ) { projectFileAuthorization.canUploadFile(2, ProjectFileType.APPLICANT_FILE) }
            assertThat(exception.entity).isEqualTo("project")
        }

        listOf(
            SUBMITTED,
            ELIGIBLE,
            INELIGIBLE,
            APPROVED,
            APPROVED_WITH_CONDITIONS,
            NOT_APPROVED
        ).forEach {
            every { projectPersistence.getApplicantAndStatusById(eq(1L)) } returns ProjectApplicantAndStatus(
                applicantUser.user.id!!,
                it
            )
            assertFalse(
                projectFileAuthorization.canUploadFile(1, ProjectFileType.ASSESSMENT_FILE),
                "${programmeUser.user.email} cannot upload ${ProjectFileType.ASSESSMENT_FILE} file when status is $it"
            )
            assertFalse(
                projectFileAuthorization.canUploadFile(1, ProjectFileType.APPLICANT_FILE),
                "${programmeUser.user.email} cannot upload ${ProjectFileType.APPLICANT_FILE} file when status is $it"
            )
        }

    }

    @Test
    fun `canChangeFile admin`() {
        every { securityService.currentUser } returns adminUser

        assertTrue(projectFileAuthorization.canChangeFile(501, applicantFile2005.id!!))
        assertTrue(projectFileAuthorization.canChangeFile(501, assessmentFile2005.id!!))
    }

    @ParameterizedTest
    @MethodSource("provideBothFileTypes")
    fun `canChangeFile programme user`(file: OutputProjectFile) {
        every { securityService.currentUser } returns programmeUser

        listOf(
            STEP1_SUBMITTED,
            STEP1_ELIGIBLE,
            STEP1_APPROVED_WITH_CONDITIONS,
            SUBMITTED,
            RETURNED_TO_APPLICANT,
            ELIGIBLE,
            APPROVED_WITH_CONDITIONS
        ).forEach {
            every { projectPersistence.getProject(eq(315)) } returns getProject(315, 42, it)
            every { projectAuthorization.canReadProject(eq(315)) } returns true

            if (file.type == ProjectFileType.ASSESSMENT_FILE)
                assertTrue(
                    projectFileAuthorization.canChangeFile(315, file.id!!),
                    "${programmeUser.user.email} CAN change ${file.type} when project status is $it"
                )
            else {
                assertFalse(
                    projectFileAuthorization.canChangeFile(315, file.id!!),
                    "${programmeUser.user.email} CAN NOT change ${file.type} (project status was $it)"
                )
            }
        }

        listOf(
            DRAFT
        ).forEach {
            every { projectPersistence.getProject(eq(315)) } returns getProject(315, 42, it)
            every { projectAuthorization.canReadProject(eq(315L)) } throws ResourceNotFoundException("project")

            val exception = assertThrows<ResourceNotFoundException>(
                "${programmeUser.user.email} CAN NOT even retrieve project (${file.type}, project status was $it)"
            ) { projectFileAuthorization.canChangeFile(315, file.id!!) }
            assertThat(exception.entity).isEqualTo("project")
        }

    }

    @Test
    fun `canChangeFile programme user ASSESSMENT FILE finalized status`() {

        listOf(
            APPROVED,
            NOT_APPROVED
        ).forEach {
            val fundingDecision = ProjectStatus(
                id = null,
                status = it,
                updated = toZonedDate(year2008),
                user = UserSummary(0, "", "", "", UserRoleSummary(name = ""))
            )
            every { projectPersistence.getProject(eq(210)) } returns getProject(
                210,
                42,
                it
            ).copy(firstStepDecision = ProjectDecision(fundingDecision = fundingDecision))
            every { projectAuthorization.canReadProject(eq(210)) } returns true

            var file = assessmentFile2005
            assertFalse(
                projectFileAuthorization.canChangeFile(210, file.id!!),
                "${programmeUser.user.email} CAN NOT change ${file.type}, because it was uploaded before final $it decision"
            )

            file = assessmentFile2011
            assertTrue(
                projectFileAuthorization.canChangeFile(210, file.id!!),
                "${programmeUser.user.email} CAN change ${file.type}, because it was uploaded after final $it decision"
            )
        }

        listOf(
            INELIGIBLE
        ).forEach {
            val eligibilityDecision = ProjectStatus(
                id = null,
                status = it,
                updated = toZonedDate(year2008),
                user = UserSummary(0, "", "", "", UserRoleSummary(name = ""))
            )
            every { projectPersistence.getProject(eq(220)) } returns getProject(
                220,
                42,
                it
            ).copy(firstStepDecision = ProjectDecision(eligibilityDecision = eligibilityDecision))
            every { projectAuthorization.canReadProject(eq(220)) } returns true

            var file = assessmentFile2005
            assertFalse(
                projectFileAuthorization.canChangeFile(220, file.id!!),
                "${programmeUser.user.email} CAN NOT change ${file.type}, because it was uploaded before final $it decision"
            )

            file = assessmentFile2011
            assertTrue(
                projectFileAuthorization.canChangeFile(220, file.id!!),
                "${programmeUser.user.email} CAN change ${file.type}, because it was uploaded after final $it decision"
            )
        }

    }

    @Test
    fun `canChangeFile applicant user`() {
        every { securityService.currentUser } returns applicantUser

        listOf(
            DRAFT,
            RETURNED_TO_APPLICANT
        ).forEach {
            // ################ isOwner #################
            var project2008 = getProjectLastSubmitted(78, applicantUser.user.id!!, it, year2008)
            every { projectPersistence.getProject(eq(78L)) } returns project2008
            every { projectAuthorization.canReadProject(eq(78L)) } returns true

            var file = applicantFile2005
            assertFalse(
                projectFileAuthorization.canChangeFile(78, file.id!!),
                "${applicantUser.user.email} (as owner) cannot change file when status is $it, " +
                    "but file is ${file.updated} and project submitted ${project2008.lastResubmission?.updated}"
            )

            file = applicantFile2011
            assertTrue(
                projectFileAuthorization.canChangeFile(78, file.id!!),
                "${applicantUser.user.email} (as owner) cannot change file when status is $it, " +
                    "but file is ${file.updated} and project submitted ${project2008.lastResubmission?.updated}"
            )

            // ################ isNotOwner #################
            project2008 = getProjectLastSubmitted(78, 270L, it, year2008)
            every { projectPersistence.getProject(eq(78L)) } returns project2008
            every { projectAuthorization.canReadProject(eq(78L)) } throws ResourceNotFoundException("project")

            file = applicantFile2005
            val exception = assertThrows<ResourceNotFoundException>(
                "${applicantUser.user.email} (as NOT owner) cannot retrieve project at all"
            ) { projectFileAuthorization.canChangeFile(78, file.id!!) }
            assertThat(exception.entity).isEqualTo("project")
        }

        // check assessment files
        ApplicationStatus.values().forEach {
            every { projectPersistence.getProject(eq(667)) } returns getProject(667, applicantUser.user.id!!, it)
            every { projectAuthorization.canReadProject(eq(667L)) } returns true
            val exception = assertThrows<ResourceNotFoundException>(
                "${applicantUser.user.email} cannot found ${assessmentFile2011.type} (project status was $it)"
            ) { projectFileAuthorization.canChangeFile(667, assessmentFile2011.id!!) }
            assertThat(exception.entity).isEqualTo("project_file")
        }
    }

    @Test
    fun `canDownloadFile admin`() {
        every { securityService.currentUser } returns adminUser

        assertTrue(projectFileAuthorization.canDownloadFile(602, applicantFile2005.id!!))
        assertTrue(projectFileAuthorization.canDownloadFile(602, assessmentFile2005.id!!))
    }

    @Test
    fun `canDownloadFile programme user`() {
        every { securityService.currentUser } returns programmeUser

        // ## ASSESSMENT_FILE section: ##
        listOf( // allowed statuses
            SUBMITTED,
            RETURNED_TO_APPLICANT,
            ELIGIBLE,
            INELIGIBLE,
            APPROVED,
            APPROVED_WITH_CONDITIONS,
            NOT_APPROVED
        ).forEach {
            every { projectPersistence.getApplicantAndStatusById(eq(420)) } returns ProjectApplicantAndStatus(-56, it)
            every { projectAuthorization.canReadProject(eq(420)) } returns true
            assertTrue(
                projectFileAuthorization.canDownloadFile(420, assessmentFile2011.id!!),
                "${programmeUser.user.email} can download ${assessmentFile2011.type} when project status is $it"
            )
        }

        // ## APPLICANT_FILE section: ##
        listOf( // allowed statuses
            SUBMITTED,
            RETURNED_TO_APPLICANT,
            ELIGIBLE,
            INELIGIBLE,
            APPROVED,
            APPROVED_WITH_CONDITIONS,
            NOT_APPROVED
        ).forEach {
            every { projectPersistence.getApplicantAndStatusById(eq(421)) } returns ProjectApplicantAndStatus(-57, it)
            every { projectAuthorization.canReadProject(eq(421)) } returns true
            assertTrue(
                projectFileAuthorization.canDownloadFile(421, applicantFile2011.id!!),
                "${programmeUser.user.email} can download ${applicantFile2011.type} when project status is $it"
            )
        }

        listOf( // not allowed statuses for BOTH
            DRAFT
        ).forEach {
            every { projectAuthorization.canReadProject(eq(422)) } throws ResourceNotFoundException("project")

            every { projectPersistence.getApplicantAndStatusById(eq(422)) } returns ProjectApplicantAndStatus(-58, it)
            var exception = assertThrows<ResourceNotFoundException>(
                "${programmeUser.user.email} cannot download ${assessmentFile2011.type} when project status is $it"
            ) { projectFileAuthorization.canDownloadFile(422, assessmentFile2011.id!!) }
            assertThat(exception.entity).isEqualTo("project")

            every { projectPersistence.getApplicantAndStatusById(eq(42)) } returns ProjectApplicantAndStatus(-58, it)
            exception = assertThrows<ResourceNotFoundException>(
                "${programmeUser.user.email} cannot download ${applicantFile2011.type} when project status is $it"
            ) { projectFileAuthorization.canDownloadFile(422, applicantFile2011.id!!) }
            assertThat(exception.entity).isEqualTo("project")
        }
    }

    @Test
    fun `canDownloadFile applicant user`() {
        every { securityService.currentUser } returns applicantUser

        // ## APPLICANT_FILE section: ##
        ApplicationStatus.values().forEach {
            // is owner
            every { projectPersistence.getApplicantAndStatusById(eq(115)) } returns ProjectApplicantAndStatus(
                applicantUser.user.id!!,
                it
            )
            every { projectAuthorization.canReadProject(eq(115)) } returns true
            assertTrue(
                projectFileAuthorization.canDownloadFile(115, applicantFile2005.id!!),
                "${applicantUser.user.email} can download his(!!) ${applicantFile2005.type} (project status was $it)"
            )

            // is not owner
            every { projectPersistence.getApplicantAndStatusById(eq(115)) } returns ProjectApplicantAndStatus(-6, it)
            every { projectAuthorization.canReadProject(eq(115)) } throws ResourceNotFoundException("project")
            val exception = assertThrows<ResourceNotFoundException>(
                "${applicantUser.user.email} cannot download ${applicantFile2005.type} when he is not owner(!!) (project status was $it)"
            ) { projectFileAuthorization.canDownloadFile(115, applicantFile2005.id!!) }
            assertThat(exception.entity).isEqualTo("project")
        }

        // ## ASSESSMENT_FILE section: ##
        ApplicationStatus.values().forEach {
            every { projectPersistence.getApplicantAndStatusById(eq(180)) } returns ProjectApplicantAndStatus(
                applicantUser.user.id!!,
                it
            )
            every { projectAuthorization.canReadProject(eq(180)) } returns true

            val exception = assertThrows<ResourceNotFoundException>(
                "${applicantUser.user.email} cannot download ${assessmentFile2005.type} (project status was $it)"
            ) { projectFileAuthorization.canDownloadFile(180, assessmentFile2005.id!!) }
            assertThat(exception.entity).isEqualTo("project_file")
        }
    }

    @Test
    fun `canListFiles admin`() {
        every { securityService.currentUser } returns adminUser

        assertTrue(projectFileAuthorization.canListFiles(19, ProjectFileType.APPLICANT_FILE))
        assertTrue(projectFileAuthorization.canListFiles(19, ProjectFileType.ASSESSMENT_FILE))
    }

    @Test
    fun `canListFiles programme user`() {
        every { securityService.currentUser } returns programmeUser

        val statusesWithoutDraft = ApplicationStatus.values().toMutableSet()
        statusesWithoutDraft.remove(DRAFT)

        // ## ASSESSMENT_FILE section: ##
        statusesWithoutDraft.forEach {
            every { projectPersistence.getProject(eq(25)) } returns getProject(25, applicantUser.user.id!!, it)
            every { projectAuthorization.canReadProject(eq(25)) } returns true
            assertTrue(
                projectFileAuthorization.canListFiles(25, ProjectFileType.ASSESSMENT_FILE),
                "${programmeUser.user.email} can list files of type ${ProjectFileType.ASSESSMENT_FILE} when project status is $it"
            )
        }

        // ## APPLICANT_FILE section: ##
        statusesWithoutDraft.forEach {
            every { projectPersistence.getProject(eq(29)) } returns getProject(29, applicantUser.user.id!!, it)
            every { projectAuthorization.canReadProject(eq(29)) } returns true
            assertTrue(
                projectFileAuthorization.canListFiles(29, ProjectFileType.APPLICANT_FILE),
                "${programmeUser.user.email} can list files of type ${ProjectFileType.APPLICANT_FILE} when project status is $it"
            )
        }

        listOf( // not allowed statuses
            DRAFT
        ).forEach {
            // applicant file
            every { projectPersistence.getProject(eq(30)) } returns getProject(30, applicantUser.user.id!!, it)
            every { projectAuthorization.canReadProject(eq(30)) } throws ResourceNotFoundException("project")
            var exception = assertThrows<ResourceNotFoundException>(
                "${programmeUser.user.email} CANNOT list files of type ${ProjectFileType.APPLICANT_FILE} when project STATUS is $it"
            ) { projectFileAuthorization.canListFiles(30, ProjectFileType.APPLICANT_FILE) }
            assertThat(exception.entity).isEqualTo("project")

            // assessment file
            every { projectPersistence.getProject(eq(31)) } returns getProject(31, applicantUser.user.id!!, it)
            every { projectAuthorization.canReadProject(eq(31)) } throws ResourceNotFoundException("project")
            exception = assertThrows<ResourceNotFoundException>(
                "${programmeUser.user.email} CANNOT list files of type ${ProjectFileType.APPLICANT_FILE} when project STATUS is $it"
            ) { projectFileAuthorization.canListFiles(31, ProjectFileType.ASSESSMENT_FILE) }
            assertThat(exception.entity).isEqualTo("project")
        }
    }

    @Test
    fun `canListFiles applicant user`() {
        every { securityService.currentUser } returns applicantUser

        // ## APPLICANT_FILE section: ##
        ApplicationStatus.values().forEach {
            // is owner
            every { projectPersistence.getApplicantAndStatusById(eq(396)) } returns ProjectApplicantAndStatus(
                applicantUser.user.id!!,
                it
            )
            every { projectAuthorization.canReadProject(eq(396)) } returns true
            assertTrue(
                projectFileAuthorization.canListFiles(396, ProjectFileType.APPLICANT_FILE),
                "${applicantUser.user.email} can list his(!!) files of type ${ProjectFileType.APPLICANT_FILE} (project status was $it)"
            )

            // is not owner
            every { projectPersistence.getApplicantAndStatusById(eq(397)) } returns ProjectApplicantAndStatus(-15, it)
            every { projectAuthorization.canReadProject(eq(397)) } throws ResourceNotFoundException("project")
            val exception = assertThrows<ResourceNotFoundException>(
                "${applicantUser.user.email} cannot list files of type ${ProjectFileType.APPLICANT_FILE} when he is not owner(!!) (project status was $it)"
            ) { projectFileAuthorization.canListFiles(397, ProjectFileType.APPLICANT_FILE) }
            assertThat(exception.entity).isEqualTo("project")
        }

        // ## ASSESSMENT_FILE section: ##
        ApplicationStatus.values().forEach {
            every { projectPersistence.getApplicantAndStatusById(eq(398)) } returns ProjectApplicantAndStatus(
                applicantUser.user.id!!,
                it
            )
            every { projectAuthorization.canReadProject(eq(398)) } returns true
            assertFalse(
                projectFileAuthorization.canListFiles(398, ProjectFileType.ASSESSMENT_FILE),
                "${applicantUser.user.email} cannot list files of type ${ProjectFileType.ASSESSMENT_FILE} (project status was $it)"
            )
        }
    }

    @Test
    fun `get last submission date correctly`() {
        val finalStatus = getSubmissionAt(year2011)
        val dummyProject = dummyProjectWithStatus(finalStatus)

        var project = dummyProject.copy(
            firstSubmission = getSubmissionAt(year2005),
            lastResubmission = getSubmissionAt(year2008),
            projectStatus = finalStatus
        )
        assertThat(projectFileAuthorization.getLastSubmissionFor(project)).isEqualTo(toZonedDate(year2008))

        project = dummyProject.copy(
            firstSubmission = null,
            lastResubmission = null,
            projectStatus = finalStatus
        )
        assertThat(projectFileAuthorization.getLastSubmissionFor(project)).isEqualTo(toZonedDate(year2011))

        project = dummyProject.copy(
            firstSubmission = getSubmissionAt(year2008),
            lastResubmission = null,
            projectStatus = getSubmissionAt(year2011)
        )
        assertThat(projectFileAuthorization.getLastSubmissionFor(project)).isEqualTo(toZonedDate(year2008))

        project = dummyProject.copy(
            firstSubmission = getSubmissionAt(year2011),
            lastResubmission = getSubmissionAt(year2008),
            projectStatus = finalStatus
        )
        assertThat(projectFileAuthorization.getLastSubmissionFor(project)).isEqualTo(toZonedDate(year2008))
    }

    private fun provideBothFileTypes(): Stream<Arguments> {
        return Stream.of(
            Arguments.of(applicantFile2005),
            Arguments.of(assessmentFile2005)
        )
    }

}
