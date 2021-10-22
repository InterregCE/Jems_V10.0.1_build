package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.programmeUser
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class ProjectWorkPackageAuthorizationTest : UnitTest() {

    companion object {
        private const val WORK_PACKAGE_ID = 4L
        private const val INVESTMENT_ID = 7L
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var workPackageService: WorkPackageService

    @MockK
    lateinit var workPackagePersistence: WorkPackagePersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistence

    @InjectMockKs
    lateinit var projectWorkPackageAuthorization: ProjectWorkPackageAuthorization

    @ParameterizedTest(name = "isUserOwnerOfWorkPackage - true (no-matter status - {0})")
    @EnumSource(value = ApplicationStatus::class)
    fun `isUserOwnerOfWorkPackage - true (no-matter status)`(status: ApplicationStatus) {
        every { workPackageService.getProjectForWorkPackageId(WORK_PACKAGE_ID) } returns ProjectApplicantAndStatus(
            applicantId = applicantUser.user.id,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectWorkPackageAuthorization.isUserOwnerOfWorkPackage(1L, WORK_PACKAGE_ID, null)).isTrue
    }

    @ParameterizedTest(name = "isUserOwnerOfWorkPackage - false (no-matter status - {0})")
    @EnumSource(value = ApplicationStatus::class)
    fun `isUserOwnerOfWorkPackage - false (no-matter status)`(status: ApplicationStatus) {
        every { workPackageService.getProjectForWorkPackageId(WORK_PACKAGE_ID) } returns  ProjectApplicantAndStatus(
            applicantId = 407L,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectWorkPackageAuthorization.isUserOwnerOfWorkPackage(1L, WORK_PACKAGE_ID, null)).isFalse
    }

    @Test
    fun `can update workpackage - no permissions`() {
        every { securityService.currentUser } returns programmeUser
        every { workPackageService.getProjectForWorkPackageId(WORK_PACKAGE_ID) } returns
            ProjectApplicantAndStatus(applicantId = 2480L, projectStatus = ApplicationStatus.SUBMITTED)

        assertThrows<ResourceNotFoundException> { projectWorkPackageAuthorization.canUpdateProjectWorkPackage(WORK_PACKAGE_ID) }
    }

    @ParameterizedTest(name = "can update workpackage - OWNER, but wrong status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT", "RETURNED_TO_APPLICANT_FOR_CONDITIONS"], mode = EnumSource.Mode.EXCLUDE)
    fun `can update workpackage - OWNER, but wrong status`(status: ApplicationStatus) {
        every { securityService.currentUser } returns applicantUser
        every { workPackageService.getProjectForWorkPackageId(WORK_PACKAGE_ID) } returns
            ProjectApplicantAndStatus(applicantId = applicantUser.user.id, projectStatus = status)

        assertThat(projectWorkPackageAuthorization.canUpdateProjectWorkPackage(WORK_PACKAGE_ID)).isFalse
    }

    @ParameterizedTest(name = "can update workpackage - HAS PERMISSION, but wrong status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT", "RETURNED_TO_APPLICANT_FOR_CONDITIONS"], mode = EnumSource.Mode.EXCLUDE)
    fun `can update workpackage - HAS PERMISSION, but wrong status`(status: ApplicationStatus) {
        every { securityService.currentUser } returns adminUser
        every { workPackageService.getProjectForWorkPackageId(WORK_PACKAGE_ID) } returns
            ProjectApplicantAndStatus(applicantId = 2699L, projectStatus = status)

        assertThat(projectWorkPackageAuthorization.canUpdateProjectWorkPackage(WORK_PACKAGE_ID)).isFalse
    }

    @Test
    fun `can update investment - no permissions`() {
        every { securityService.currentUser } returns programmeUser
        every { workPackagePersistence.getProjectFromWorkPackageInvestment(INVESTMENT_ID) } returns
            ProjectApplicantAndStatus(applicantId = 2480L, projectStatus = ApplicationStatus.SUBMITTED)

        assertThrows<ResourceNotFoundException> { projectWorkPackageAuthorization.canUpdateProjectInvestment(INVESTMENT_ID) }
    }

    @ParameterizedTest(name = "can update investment - OWNER, but wrong status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT", "RETURNED_TO_APPLICANT_FOR_CONDITIONS"], mode = EnumSource.Mode.EXCLUDE)
    fun `can update investment - OWNER, but wrong status`(status: ApplicationStatus) {
        every { securityService.currentUser } returns applicantUser
        every { workPackagePersistence.getProjectFromWorkPackageInvestment(INVESTMENT_ID) } returns
            ProjectApplicantAndStatus(applicantId = applicantUser.user.id, projectStatus = status)

        assertThat(projectWorkPackageAuthorization.canUpdateProjectInvestment(INVESTMENT_ID)).isFalse
    }

    @ParameterizedTest(name = "can update investment - HAS PERMISSION, but wrong status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT", "RETURNED_TO_APPLICANT_FOR_CONDITIONS"], mode = EnumSource.Mode.EXCLUDE)
    fun `can update investment - HAS PERMISSION, but wrong status`(status: ApplicationStatus) {
        every { securityService.currentUser } returns adminUser
        every { workPackagePersistence.getProjectFromWorkPackageInvestment(INVESTMENT_ID) } returns
            ProjectApplicantAndStatus(applicantId = 2699L, projectStatus = status)

        assertThat(projectWorkPackageAuthorization.canUpdateProjectInvestment(INVESTMENT_ID)).isFalse
    }

    @ParameterizedTest(name = "isUserOwnerOfInvestment - true (no-matter status - {0})")
    @EnumSource(value = ApplicationStatus::class)
    fun `isUserOwnerOfInvestment - true (no-matter status)`(status: ApplicationStatus) {
        every { workPackagePersistence.getProjectFromWorkPackageInvestment(INVESTMENT_ID) } returns ProjectApplicantAndStatus(
            applicantId = applicantUser.user.id,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectWorkPackageAuthorization.isUserOwnerOfInvestment(1L, INVESTMENT_ID, null)).isTrue
    }

    @ParameterizedTest(name = "isUserOwnerOfInvestment - false (no-matter status - {0})")
    @EnumSource(value = ApplicationStatus::class)
    fun `isUserOwnerOfInvestment - false (no-matter status)`(status: ApplicationStatus) {
        every { workPackagePersistence.getProjectFromWorkPackageInvestment(INVESTMENT_ID) } returns ProjectApplicantAndStatus(
            applicantId = 655L,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectWorkPackageAuthorization.isUserOwnerOfInvestment(1L, INVESTMENT_ID, null)).isFalse
    }

}
