package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.WorkPackageService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
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
        assertThat(projectWorkPackageAuthorization.isUserOwnerOfWorkPackage(WORK_PACKAGE_ID)).isTrue
    }

    @ParameterizedTest(name = "isUserOwnerOfWorkPackage - false (no-matter status - {0})")
    @EnumSource(value = ApplicationStatus::class)
    fun `isUserOwnerOfWorkPackage - false (no-matter status)`(status: ApplicationStatus) {
        every { workPackageService.getProjectForWorkPackageId(WORK_PACKAGE_ID) } returns  ProjectApplicantAndStatus(
            applicantId = 407L,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectWorkPackageAuthorization.isUserOwnerOfWorkPackage(WORK_PACKAGE_ID)).isFalse
    }

    @ParameterizedTest(name = "canOwnerUpdatePartner should return true, because {0} is valid and user is owner)")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT"])
    fun `canOwnerUpdateProjectWorkPackage - user is owner and status is open`(status: ApplicationStatus) {
        every { workPackageService.getProjectForWorkPackageId(WORK_PACKAGE_ID) } returns ProjectApplicantAndStatus(
            applicantId = applicantUser.user.id,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectWorkPackageAuthorization.canOwnerUpdateProjectWorkPackage(WORK_PACKAGE_ID)).isTrue
    }

    @ParameterizedTest(name = "canOwnerUpdateProjectWorkPackage should return false, because {0} is NOT valid although user is owner)")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT"], mode = EnumSource.Mode.EXCLUDE)
    fun `canOwnerUpdateProjectWorkPackage - user is owner but status is NOT open`(status: ApplicationStatus) {
        every { workPackageService.getProjectForWorkPackageId(WORK_PACKAGE_ID) } returns ProjectApplicantAndStatus(
            applicantId = applicantUser.user.id,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectWorkPackageAuthorization.canOwnerUpdateProjectWorkPackage(WORK_PACKAGE_ID)).isFalse
    }

    @ParameterizedTest(name = "canOwnerUpdateProjectWorkPackage should return false, {0} is valid, but user is NOT owner)")
    @EnumSource(value = ApplicationStatus::class)
    fun `canOwnerUpdatePartner - user is NOT owner (no-matter the status)`(status: ApplicationStatus) {
        every { workPackageService.getProjectForWorkPackageId(WORK_PACKAGE_ID) } returns ProjectApplicantAndStatus(
            applicantId = 363L,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectWorkPackageAuthorization.canOwnerUpdateProjectWorkPackage(WORK_PACKAGE_ID)).isFalse
    }



    @ParameterizedTest(name = "isUserOwnerOfInvestment - true (no-matter status - {0})")
    @EnumSource(value = ApplicationStatus::class)
    fun `isUserOwnerOfInvestment - true (no-matter status)`(status: ApplicationStatus) {
        every { workPackagePersistence.getProjectFromWorkPackageInvestment(INVESTMENT_ID) } returns ProjectApplicantAndStatus(
            applicantId = applicantUser.user.id,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectWorkPackageAuthorization.isUserOwnerOfInvestment(INVESTMENT_ID)).isTrue
    }

    @ParameterizedTest(name = "isUserOwnerOfInvestment - false (no-matter status - {0})")
    @EnumSource(value = ApplicationStatus::class)
    fun `isUserOwnerOfInvestment - false (no-matter status)`(status: ApplicationStatus) {
        every { workPackagePersistence.getProjectFromWorkPackageInvestment(INVESTMENT_ID) } returns ProjectApplicantAndStatus(
            applicantId = 655L,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectWorkPackageAuthorization.isUserOwnerOfInvestment(INVESTMENT_ID)).isFalse
    }

    @ParameterizedTest(name = "canOwnerUpdateProjectInvestment should return true, because {0} is valid and user is owner)")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT"])
    fun `canOwnerUpdateProjectInvestment - user is owner and status is open`(status: ApplicationStatus) {
        every { workPackagePersistence.getProjectFromWorkPackageInvestment(INVESTMENT_ID) } returns ProjectApplicantAndStatus(
            applicantId = applicantUser.user.id,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectWorkPackageAuthorization.canOwnerUpdateProjectInvestment(INVESTMENT_ID)).isTrue
    }

    @ParameterizedTest(name = "canOwnerUpdateProjectInvestment should return false, because {0} is NOT valid although user is owner)")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT"], mode = EnumSource.Mode.EXCLUDE)
    fun `canOwnerUpdateProjectInvestment - user is owner but status is NOT open`(status: ApplicationStatus) {
        every { workPackagePersistence.getProjectFromWorkPackageInvestment(INVESTMENT_ID) } returns ProjectApplicantAndStatus(
            applicantId = applicantUser.user.id,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectWorkPackageAuthorization.canOwnerUpdateProjectInvestment(INVESTMENT_ID)).isFalse
    }

    @ParameterizedTest(name = "canOwnerUpdateProjectInvestment should return false, {0} is valid, but user is NOT owner)")
    @EnumSource(value = ApplicationStatus::class)
    fun `canOwnerUpdateProjectInvestment - user is NOT owner (no-matter the status)`(status: ApplicationStatus) {
        every { workPackagePersistence.getProjectFromWorkPackageInvestment(INVESTMENT_ID) } returns ProjectApplicantAndStatus(
            applicantId = 368L,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectWorkPackageAuthorization.canOwnerUpdateProjectInvestment(INVESTMENT_ID)).isFalse
    }

}
