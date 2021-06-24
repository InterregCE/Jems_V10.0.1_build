package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.applicantUser
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

internal class ProjectPartnerAuthorizationTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 1L
        private const val PROJECT_ID = 22L
    }

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var projectPersistence: ProjectPersistence
    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @InjectMockKs
    lateinit var projectPartnerAuthorization: ProjectPartnerAuthorization

    @Test
    fun `isUserOwnerOfProjectOfPartner - true`() {
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, "1.0") } returns PROJECT_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns ProjectApplicantAndStatus(
            applicantId = applicantUser.user.id,
            projectStatus = ApplicationStatus.DRAFT,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectPartnerAuthorization.isUserOwnerOfProjectOfPartner(PARTNER_ID, null)).isTrue
        assertThat(projectPartnerAuthorization.isUserOwnerOfProjectOfPartner(PARTNER_ID, "1.0")).isTrue
    }

    @Test
    fun `isUserOwnerOfProjectOfPartner - false`() {
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns ProjectApplicantAndStatus(
            applicantId = 598L,
            projectStatus = ApplicationStatus.DRAFT,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectPartnerAuthorization.isUserOwnerOfProjectOfPartner(PARTNER_ID, null)).isFalse
    }

    @Test
    fun `isUserOwnerOfProjectOfPartner - version not found`() {
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID, "404") } throws ResourceNotFoundException("partner")
        assertThrows<ResourceNotFoundException> { projectPartnerAuthorization.isUserOwnerOfProjectOfPartner(PARTNER_ID, "404") }
    }

    @ParameterizedTest(name = "canOwnerUpdatePartner should return true, because {0} is valid and user is owner)")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT"])
    fun `canOwnerUpdatePartner - user is owner and status is open`(status: ApplicationStatus) {
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns ProjectApplicantAndStatus(
            applicantId = applicantUser.user.id,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectPartnerAuthorization.canOwnerUpdatePartner(PARTNER_ID)).isTrue
    }

    @ParameterizedTest(name = "canOwnerUpdatePartner should return false, because {0} is NOT valid although user is owner)")
    @EnumSource(value = ApplicationStatus::class, names = ["DRAFT", "STEP1_DRAFT", "RETURNED_TO_APPLICANT"], mode = EnumSource.Mode.EXCLUDE)
    fun `canOwnerUpdatePartner - user is owner but status is NOT open`(status: ApplicationStatus) {
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns ProjectApplicantAndStatus(
            applicantId = applicantUser.user.id,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectPartnerAuthorization.canOwnerUpdatePartner(PARTNER_ID)).isFalse
    }

    @ParameterizedTest(name = "canOwnerUpdatePartner should return false, {0} is valid, but user is NOT owner)")
    @EnumSource(value = ApplicationStatus::class)
    fun `canOwnerUpdatePartner - user is NOT owner (no-matter the status)`(status: ApplicationStatus) {
        every { partnerPersistence.getProjectIdForPartnerId(PARTNER_ID) } returns PROJECT_ID
        every { projectPersistence.getApplicantAndStatusById(PROJECT_ID) } returns ProjectApplicantAndStatus(
            applicantId = 363L,
            projectStatus = status,
        )
        every { securityService.currentUser } returns applicantUser
        assertThat(projectPartnerAuthorization.canOwnerUpdatePartner(PARTNER_ID)).isFalse
    }

}
