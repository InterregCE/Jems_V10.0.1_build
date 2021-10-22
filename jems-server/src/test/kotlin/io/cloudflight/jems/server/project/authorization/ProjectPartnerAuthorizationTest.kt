package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.programmeUser
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class ProjectPartnerAuthorizationTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 650L
        private const val PROJECT_ID = 21L
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectPersistence: ProjectPersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @InjectMockKs
    lateinit var projectPartnerAuthorization: ProjectPartnerAuthorization

    @BeforeAll
    fun setup() {
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, null) } returns PROJECT_ID
    }

    @Test
    fun `user is owner`() {
        every { securityService.currentUser } returns applicantUser
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = applicantUser.user.id, projectStatus = ApplicationStatus.SUBMITTED)

        assertThat(projectPartnerAuthorization.isOwnerOfPartner(PARTNER_ID, null)).isTrue
    }

    @Test
    fun `user is not owner`() {
        every { securityService.currentUser } returns applicantUser
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = 3659L, projectStatus = ApplicationStatus.SUBMITTED)

        assertThrows<ResourceNotFoundException> { projectPartnerAuthorization.isOwnerOfPartner(PARTNER_ID) }
    }

    @Test
    fun `can update partner - no permissions`() {
        every { securityService.currentUser } returns programmeUser
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = 2479L, projectStatus = ApplicationStatus.SUBMITTED)

        assertThrows<ResourceNotFoundException> { projectPartnerAuthorization.canUpdatePartner(PARTNER_ID) }
    }

    @ParameterizedTest(name = "can update partner - OWNER, but wrong status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT", "RETURNED_TO_APPLICANT_FOR_CONDITIONS"], mode = EnumSource.Mode.EXCLUDE)
    fun `can update partner - OWNER, but wrong status`(status: ApplicationStatus) {
        every { securityService.currentUser } returns applicantUser
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = applicantUser.user.id, projectStatus = status)

        assertThat(projectPartnerAuthorization.canUpdatePartner(PARTNER_ID)).isFalse
    }

    @ParameterizedTest(name = "can update partner - HAS PERMISSION, but wrong status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT", "RETURNED_TO_APPLICANT_FOR_CONDITIONS"], mode = EnumSource.Mode.EXCLUDE)
    fun `can update partner - HAS PERMISSION, but wrong status`(status: ApplicationStatus) {
        every { securityService.currentUser } returns adminUser
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = 2699L, projectStatus = status)

        assertThat(projectPartnerAuthorization.canUpdatePartner(PARTNER_ID)).isFalse
    }

    @ParameterizedTest(name = "can update partner - OK (status {0})")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT", "RETURNED_TO_APPLICANT_FOR_CONDITIONS"])
    fun `can update partner - OK`(status: ApplicationStatus) {
        every { securityService.currentUser } returns adminUser
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns
            ProjectApplicantAndStatus(applicantId = adminUser.user.id, projectStatus = status)

        assertThat(projectPartnerAuthorization.canUpdatePartner(PARTNER_ID)).isTrue
    }

}
