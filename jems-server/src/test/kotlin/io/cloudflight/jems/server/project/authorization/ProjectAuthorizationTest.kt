package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.programmeUser
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.application.ApplicationStatus.SUBMITTED
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class ProjectAuthorizationTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 598L
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var projectAuthorization: ProjectAuthorization

    @Test
    fun `user is owner`() {
        every { securityService.currentUser } returns adminUser
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = adminUser.user.id, projectStatus = SUBMITTED)

        assertThat(projectAuthorization.isUserOwnerOfProject(PROJECT_ID)).isTrue
    }

    @Test
    fun `user is not owner`() {
        every { securityService.currentUser } returns adminUser
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = 3658L, projectStatus = SUBMITTED)

        assertThrows<ResourceNotFoundException> { projectAuthorization.isUserOwnerOfProject(PROJECT_ID) }
    }

    @Test
    fun `can update project - no permissions`() {
        every { securityService.currentUser } returns programmeUser
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = 2478L, projectStatus = SUBMITTED)

        assertThrows<ResourceNotFoundException> { projectAuthorization.canUpdateProject(PROJECT_ID) }
    }

    @ParameterizedTest(name = "can update project - OWNER, but wrong status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT"], mode = EnumSource.Mode.EXCLUDE)
    fun `can update project - OWNER, but wrong status`(status: ApplicationStatus) {
        every { securityService.currentUser } returns applicantUser
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = applicantUser.user.id, projectStatus = status)

        assertThat(projectAuthorization.canUpdateProject(PROJECT_ID)).isFalse
    }

    @ParameterizedTest(name = "can update project - HAS PERMISSION, but wrong status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT"], mode = EnumSource.Mode.EXCLUDE)
    fun `can update project - HAS PERMISSION, but wrong status`(status: ApplicationStatus) {
        every { securityService.currentUser } returns adminUser
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = 2698L, projectStatus = status)

        assertThat(projectAuthorization.canUpdateProject(PROJECT_ID)).isFalse
    }

    @ParameterizedTest(name = "can update project - OK (status {0})")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT"])
    fun `can update project - OK`(status: ApplicationStatus) {
        every { securityService.currentUser } returns adminUser
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = adminUser.user.id, projectStatus = status)

        assertThat(projectAuthorization.canUpdateProject(PROJECT_ID)).isTrue
    }

}
