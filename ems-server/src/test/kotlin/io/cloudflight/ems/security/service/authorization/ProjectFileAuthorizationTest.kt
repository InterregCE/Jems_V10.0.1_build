package io.cloudflight.ems.security.service.authorization

import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.OutputProjectFile
import io.cloudflight.ems.api.dto.OutputProjectStatus
import io.cloudflight.ems.api.dto.ProjectApplicationStatus
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.APPROVED
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.APPROVED_WITH_CONDITIONS
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.DRAFT
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.ELIGIBLE
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.INELIGIBLE
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.NOT_APPROVED
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.RETURNED_TO_APPLICANT
import io.cloudflight.ems.api.dto.ProjectApplicationStatus.SUBMITTED
import io.cloudflight.ems.api.dto.ProjectFileType
import io.cloudflight.ems.api.dto.user.OutputUser
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.security.service.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.ems.security.service.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.ems.security.service.authorization.AuthorizationUtil.Companion.programmeUser
import io.cloudflight.ems.service.FileStorageService
import io.cloudflight.ems.service.ProjectService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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

        private fun getProject(id: Long, applicantId: Long, status: ProjectApplicationStatus): OutputProject {
            return OutputProject(
                id = id,
                acronym = "",
                applicant = OutputUser(applicantId, "", "", ""),
                projectStatus = OutputProjectStatus(null, status, OutputUser(null, "", "", ""), ZonedDateTime.now())
            )
        }

        private fun getProjectLastSubmitted(id: Long, applicantId: Long, status: ProjectApplicationStatus, lastSubmitted: LocalDate): OutputProject {
            return getProject(id, applicantId, status)
                .copy(lastResubmission = getSubmissionAt(lastSubmitted))
        }

        private fun dummyProjectWithStatus(status: OutputProjectStatus): OutputProject {
            return OutputProject(
                id = null,
                acronym = "",
                applicant = OutputUser(null, "", "", ""),
                projectStatus = status
            )
        }

        private fun getSubmissionAt(year: LocalDate): OutputProjectStatus {
            return OutputProjectStatus(
                id = null,
                status = SUBMITTED,
                user = OutputUser(null, "", "", ""),
                updated = toZonedDate(year)
            )
        }

        private fun toZonedDate(year: LocalDate) = ZonedDateTime.of(year, LocalTime.of(1, 1), ZoneId.systemDefault())
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectService: ProjectService

    @MockK
    lateinit var fileStorageService: FileStorageService

    lateinit var projectFileAuthorization: ProjectFileAuthorization

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectFileAuthorization = ProjectFileAuthorization(securityService, projectService, fileStorageService)

        every { fileStorageService.getFileDetail(any(), eq(applicantFile2005.id!!)) } returns applicantFile2005
        every { fileStorageService.getFileDetail(any(), eq(applicantFile2011.id!!)) } returns applicantFile2011
        every { fileStorageService.getFileDetail(any(), eq(assessmentFile2005.id!!)) } returns assessmentFile2005
        every { fileStorageService.getFileDetail(any(), eq(assessmentFile2011.id!!)) } returns assessmentFile2011
    }

    @Test
    fun `canUpload programme user can upload anytime besides DRAFT`() {
        every { securityService.currentUser } returns programmeUser

        every { projectService.getById(eq(1L)) } returns getProject(1, 10, DRAFT)
        assertFalse(projectFileAuthorization.canUploadFile(1, ProjectFileType.ASSESSMENT_FILE))
        assertFalse(projectFileAuthorization.canUploadFile(1, ProjectFileType.APPLICANT_FILE))

        listOf(
            SUBMITTED,
            RETURNED_TO_APPLICANT,
            ELIGIBLE,
            INELIGIBLE,
            APPROVED,
            APPROVED_WITH_CONDITIONS,
            NOT_APPROVED
        ).forEach {
            every { projectService.getById(eq(1L)) } returns getProject(1, 10, it)
            assertTrue(projectFileAuthorization.canUploadFile(1, ProjectFileType.ASSESSMENT_FILE),
                "${programmeUser.user.email} can upload ${ProjectFileType.ASSESSMENT_FILE} file when status is $it")
            assertFalse(projectFileAuthorization.canUploadFile(1, ProjectFileType.APPLICANT_FILE),
                "${programmeUser.user.email} cannot upload ${ProjectFileType.APPLICANT_FILE} file when status is $it")
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

        listOf( // boolean means if applicant is owner
            Pair(DRAFT, true), // isOwner
            Pair(RETURNED_TO_APPLICANT, true), // isOwner
            Pair(DRAFT, false), // !isOwner
            Pair(RETURNED_TO_APPLICANT, false) //!isOwner
        ).forEach {
            val project = getProject(1, if (it.second) applicantUser.user.id!! else 456L, it.first)
            every { projectService.getById(eq(1L)) } returns project

            assertFalse(projectFileAuthorization.canUploadFile(1, ProjectFileType.ASSESSMENT_FILE),
                "${applicantUser.user.email} (as owner=${it.second}) cannot upload ${ProjectFileType.ASSESSMENT_FILE} file when status is $it")
            assertThat(projectFileAuthorization.canUploadFile(1, ProjectFileType.APPLICANT_FILE))
                .overridingErrorMessage("${applicantUser.user.email} ${if (it.second) "as owner can" else "as not-owner cannot"} upload ${ProjectFileType.APPLICANT_FILE} file when status is $it")
                .isEqualTo(it.second) // == isOwner
        }

        listOf(
            SUBMITTED,
            ELIGIBLE,
            INELIGIBLE,
            APPROVED,
            APPROVED_WITH_CONDITIONS,
            NOT_APPROVED
        ).forEach {
            every { projectService.getById(eq(1L)) } returns getProject(1, applicantUser.user.id!!, it)
            assertFalse(projectFileAuthorization.canUploadFile(1, ProjectFileType.ASSESSMENT_FILE),
                "${programmeUser.user.email} cannot upload ${ProjectFileType.ASSESSMENT_FILE} file when status is $it")
            assertFalse(projectFileAuthorization.canUploadFile(1, ProjectFileType.APPLICANT_FILE),
                "${programmeUser.user.email} cannot upload ${ProjectFileType.APPLICANT_FILE} file when status is $it")
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
            SUBMITTED,
            RETURNED_TO_APPLICANT,
            ELIGIBLE,
            INELIGIBLE,
            APPROVED_WITH_CONDITIONS
        ).forEach {
            every { projectService.getById(eq(315)) } returns getProject(315, 42, it)

            val isAssessment = file.type == ProjectFileType.ASSESSMENT_FILE
            assertThat(projectFileAuthorization.canChangeFile(315, file.id!!))
                .overridingErrorMessage("${programmeUser.user.email} ${if (isAssessment) "can" else "cannot"} change ${file.type} when project status is $it")
                .isEqualTo(isAssessment)
        }

        listOf(
            DRAFT,
            APPROVED,
            NOT_APPROVED
        ).forEach {
            every { projectService.getById(eq(315)) } returns getProject(315, 42, it)
            assertFalse(projectFileAuthorization.canChangeFile(315, file.id!!),
                "${programmeUser.user.email} cannot change ${file.type} when project status is $it")
        }
    }

    @Test
    fun `canChangeFile applicant user`() {
        every { securityService.currentUser } returns applicantUser

        listOf( // boolean means if applicant is owner
            Pair(DRAFT, true), // isOwner
            Pair(RETURNED_TO_APPLICANT, true), // isOwner
            Pair(DRAFT, false), // !isOwner
            Pair(RETURNED_TO_APPLICANT, false) //!isOwner
        ).forEach {
            val project2008 = getProjectLastSubmitted(78, if (it.second) applicantUser.user.id!! else 270L, it.first, year2008)
            every { projectService.getById(eq(78L)) } returns project2008

            var file = applicantFile2005
            assertFalse(projectFileAuthorization.canChangeFile(78, file.id!!),
                "${applicantUser.user.email} (as owner=${it.second}) cannot change file when status is $it, " +
                    "but file is ${file.updated} and project submitted ${project2008.lastResubmission?.updated}"
            )

            file = applicantFile2011
            assertThat(projectFileAuthorization.canChangeFile(78, file.id!!))
                .withFailMessage("${applicantUser.user.email} (as owner=${it.second}) cannot change file when status is $it, " +
                    "but file is ${file.updated} and project submitted ${project2008.lastResubmission?.updated}")
                .isEqualTo(it.second) // == isOwner
        }

        // check assessment files
        ProjectApplicationStatus.values().forEach {
            every { projectService.getById(eq(667)) } returns getProject(667, applicantUser.user.id!!, it)
            assertFalse(projectFileAuthorization.canChangeFile(667, assessmentFile2011.id!!),
                "${applicantUser.user.email} cannot change ${assessmentFile2011.type} when project status is $it")
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
        ProjectApplicationStatus.values().forEach {
            every { projectService.getById(eq(420)) } returns getProject(420, -56, it)
            assertTrue(projectFileAuthorization.canDownloadFile(420, assessmentFile2011.id!!),
                "${programmeUser.user.email} can download ${assessmentFile2011.type} when project status is $it")
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
            every { projectService.getById(eq(421)) } returns getProject(421, -57, it)
            assertTrue(projectFileAuthorization.canDownloadFile(421, applicantFile2011.id!!),
                "${programmeUser.user.email} can download ${applicantFile2011.type} when project status is $it")
        }

        listOf( // not allowed statuses
            DRAFT
        ).forEach {
            every { projectService.getById(eq(422)) } returns getProject(422, -58, it)
            assertFalse(projectFileAuthorization.canDownloadFile(422, applicantFile2011.id!!),
                "${programmeUser.user.email} cannot download ${applicantFile2011.type} when project status is $it")
        }
    }

    @Test
    fun `canDownloadFile applicant user`() {
        every { securityService.currentUser } returns applicantUser

        // ## APPLICANT_FILE section: ##
        ProjectApplicationStatus.values().forEach {
            // is owner
            every { projectService.getById(eq(115)) } returns getProject(115, applicantUser.user.id!!, it)
            assertTrue(projectFileAuthorization.canDownloadFile(115, applicantFile2005.id!!),
                "${applicantUser.user.email} can download his(!!) ${applicantFile2005.type} (project status was $it)")

            // is not owner
            every { projectService.getById(eq(115)) } returns getProject(115, -6, it)
            assertFalse(projectFileAuthorization.canDownloadFile(115, applicantFile2005.id!!),
                "${applicantUser.user.email} cannot download ${applicantFile2005.type} when he is not owner(!!) (project status was $it)")
        }

        // ## ASSESSMENT_FILE section: ##
        ProjectApplicationStatus.values().forEach {
            every { projectService.getById(eq(180)) } returns getProject(180, programmeUser.user.id!!, it)
            assertFalse(projectFileAuthorization.canDownloadFile(180, assessmentFile2005.id!!),
                "${applicantUser.user.email} cannot download ${assessmentFile2005.type} (project status was $it)")
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

        // ## ASSESSMENT_FILE section: ##
        ProjectApplicationStatus.values().forEach {
            every { projectService.getById(eq(25)) } returns getProject(25, applicantUser.user.id!!, it)
            assertTrue(projectFileAuthorization.canListFiles(25, ProjectFileType.ASSESSMENT_FILE),
                "${programmeUser.user.email} can list files of type ${ProjectFileType.ASSESSMENT_FILE} when project status is $it")
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
            every { projectService.getById(eq(29)) } returns getProject(29, applicantUser.user.id!!, it)
            assertTrue(projectFileAuthorization.canListFiles(29, ProjectFileType.APPLICANT_FILE),
                "${programmeUser.user.email} can list files of type ${ProjectFileType.APPLICANT_FILE} when project status is $it")
        }

        listOf( // not allowed statuses
            DRAFT
        ).forEach {
            every { projectService.getById(eq(30)) } returns getProject(30, applicantUser.user.id!!, it)
            assertFalse(projectFileAuthorization.canListFiles(30, ProjectFileType.APPLICANT_FILE),
                "${programmeUser.user.email} cannot list files of type ${ProjectFileType.APPLICANT_FILE} when project status is $it")
        }
    }

    @Test
    fun `canListFiles applicant user`() {
        every { securityService.currentUser } returns applicantUser

        // ## APPLICANT_FILE section: ##
        ProjectApplicationStatus.values().forEach {
            // is owner
            every { projectService.getById(eq(396)) } returns getProject(396, applicantUser.user.id!!, it)
            assertTrue(projectFileAuthorization.canListFiles(396, ProjectFileType.APPLICANT_FILE),
                "${applicantUser.user.email} can list his(!!) files of type ${ProjectFileType.APPLICANT_FILE} (project status was $it)")

            // is not owner
            every { projectService.getById(eq(397)) } returns getProject(397, -15, it)
            assertFalse(projectFileAuthorization.canListFiles(397, ProjectFileType.APPLICANT_FILE),
                "${applicantUser.user.email} cannot list files of type ${ProjectFileType.APPLICANT_FILE} when he is not owner(!!) (project status was $it)")
        }

        // ## ASSESSMENT_FILE section: ##
        ProjectApplicationStatus.values().forEach {
            every { projectService.getById(eq(398)) } returns getProject(398, applicantUser.user.id!!, it)
            assertFalse(projectFileAuthorization.canListFiles(398, ProjectFileType.ASSESSMENT_FILE),
                "${applicantUser.user.email} cannot list files of type ${ProjectFileType.ASSESSMENT_FILE} (project status was $it)")
        }
    }

    @Test
    fun `get last submission date correctly` () {
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
