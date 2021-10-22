package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.model.LocalCurrentUser
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.service.ProjectService
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.security.core.authority.SimpleGrantedAuthority

internal class ProjectStatusAuthorizationTest: UnitTest() {

    companion object {

        private val userWithProjectSubmissionPermission = LocalCurrentUser(
            AuthorizationUtil.userAdmin, "hash_pass",
            listOf(SimpleGrantedAuthority(UserRolePermission.ProjectSubmission.key))
        )

        private val userWithProjectRetrievePermission = LocalCurrentUser(
            AuthorizationUtil.userAdmin, "hash_pass",
            listOf(SimpleGrantedAuthority(UserRolePermission.ProjectRetrieve.key))
        )

        private val userApplicant = LocalCurrentUser(
            AuthorizationUtil.userApplicant, "hash_pass",
            emptyList()
        )

    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectService: ProjectService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var projectStatusAuthorization: ProjectStatusAuthorization

    @ParameterizedTest(name = "owner can submit project in status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT"])
    fun `owner can submit`(status: ApplicationStatus) {
        val user = userApplicant
        val PROJECT_ID = 14L

        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = user.user.id, projectStatus = status)
        every { securityService.currentUser } returns user

        assertThat(projectStatusAuthorization.canSubmit(PROJECT_ID)).isTrue
    }

    @ParameterizedTest(name = "owner can NOT submit project when status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT", "RETURNED_TO_APPLICANT_FOR_CONDITIONS"], mode = EnumSource.Mode.EXCLUDE)
    fun `owner can NOT submit`(status: ApplicationStatus) {
        val user = userApplicant
        val PROJECT_ID = 15L

        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = user.user.id, projectStatus = status)
        every { securityService.currentUser } returns user

        assertThat(projectStatusAuthorization.canSubmit(PROJECT_ID)).isFalse
    }

    @ParameterizedTest(name = "user with proper permission can submit project in status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT", "RETURNED_TO_APPLICANT_FOR_CONDITIONS"])
    fun `user with proper permission can submit`(status: ApplicationStatus) {
        val user = userWithProjectSubmissionPermission
        val PROJECT_ID = 16L
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = 4896L, projectStatus = status)
        every { securityService.currentUser } returns user

        assertThat(projectStatusAuthorization.canSubmit(PROJECT_ID)).isTrue
    }

    @ParameterizedTest(name = "user with proper permission can NOT submit project when status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT", "RETURNED_TO_APPLICANT_FOR_CONDITIONS"], mode = EnumSource.Mode.EXCLUDE)
    fun `user with proper permission can NOT submit`(status: ApplicationStatus) {
        val user = userWithProjectSubmissionPermission
        val PROJECT_ID = 17L

        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = 3699, projectStatus = status)
        every { securityService.currentUser } returns user

        assertThat(projectStatusAuthorization.canSubmit(PROJECT_ID)).isFalse
    }

    @ParameterizedTest(name = "not-owner with project-retrieve permission can NOT submit (no matter the status {0})")
    @EnumSource(value = ApplicationStatus::class)
    fun `not-owner with project-retrieve permission can NOT submit`(status: ApplicationStatus) {
        val user = userWithProjectRetrievePermission
        val PROJECT_ID = 18L

        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = 4022L, projectStatus = status)
        every { securityService.currentUser } returns user

        assertThat(projectStatusAuthorization.canSubmit(PROJECT_ID)).isFalse
    }

    @ParameterizedTest(name = "not owner without any permission can NOT find the project (no matter the status {0})")
    @EnumSource(value = ApplicationStatus::class)
    fun `not owner without any permission can NOT find the project`(status: ApplicationStatus) {
        val user = userApplicant
        val PROJECT_ID = 16L

        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = 1960L, projectStatus = status)
        every { securityService.currentUser } returns user

        assertThrows<ResourceNotFoundException> { projectStatusAuthorization.canSubmit(PROJECT_ID) }
    }

}
