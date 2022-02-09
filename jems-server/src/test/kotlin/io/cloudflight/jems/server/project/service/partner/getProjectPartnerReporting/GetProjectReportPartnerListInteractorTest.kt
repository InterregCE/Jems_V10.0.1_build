package io.cloudflight.jems.server.project.service.partner.getProjectPartnerReporting

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.legalstatus.ProgrammeLegalStatusEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel
import io.cloudflight.jems.server.project.repository.partner.toModel
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.partner.model.NaceGroupLevel
import io.cloudflight.jems.server.project.service.partner.model.PartnerSubType
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.getProjectReportPartnerList.GetProjectReportPartnerList
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import io.cloudflight.jems.server.utils.partner.ProjectPartnerTestUtil
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Sort

internal class GetProjectReportPartnerListInteractorTest : UnitTest() {

    private val UNSORTED = Sort.unsorted()

    private val projectPartnerEntity = ProjectPartnerEntity(
        id = 1,
        project = ProjectPartnerTestUtil.project,
        abbreviation = "partner",
        role = ProjectPartnerRole.LEAD_PARTNER,
        partnerType = ProjectTargetGroup.BusinessSupportOrganisation,
        partnerSubType = PartnerSubType.LARGE_ENTERPRISE,
        nace = NaceGroupLevel.A,
        otherIdentifierNumber = "id-12",
        pic = "009",
        legalStatus = ProgrammeLegalStatusEntity(id = 1),
        vat = "test vat",
        vatRecovery = ProjectPartnerVatRecovery.Yes
    )

    private val projectPartnerSummary = projectPartnerEntity.toModel()
    private val projectPartnerReportingCollaborator = PartnerCollaborator(
        userId = 2,
        partnerId = 1,
        userEmail = "test",
        level = PartnerCollaboratorLevel.EDIT
    )

    @MockK
    lateinit var persistence: PartnerPersistence

    @MockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var userAuthorization: UserAuthorization

    @InjectMockKs
    lateinit var getInteractor: GetProjectReportPartnerList

    @Test
    fun findAllByProjectIdTestException() {
        every { persistence.findAllByProjectIdForDropdown(-1, UNSORTED) } throws ResourceNotFoundException("partner")
        assertThrows<ResourceNotFoundException> { getInteractor.findAllByProjectId(-1, UNSORTED) }
    }

    @Test
    fun findAllByProjectIdTest() {
        every { persistence.findAllByProjectIdForDropdown(1, UNSORTED) } returns listOf(projectPartnerSummary)
        every { userAuthorization.hasPermissionForProject(UserRolePermission.ProjectReportingView, 1) } returns true

        Assertions.assertThat(getInteractor.findAllByProjectId(1, UNSORTED))
            .isEqualTo(listOf(projectPartnerSummary))
    }

    @Test
    fun findAllByProjectIdEmptyForCollaboratorPermissionTest() {
        every { persistence.findAllByProjectIdForDropdown(1, UNSORTED) } returns listOf(projectPartnerSummary)
        every { userAuthorization.hasPermissionForProject(UserRolePermission.ProjectReportingView, 1) } returns false
        every { userAuthorization.hasPermissionForProject(UserRolePermission.ProjectReportingEdit, 1) } returns false

        every { securityService.getUserIdOrThrow() } returns 2
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(2, 1) } returns emptySet()

        val emptyList: List<ProjectPartnerSummary> = emptyList()
        Assertions.assertThat(getInteractor.findAllByProjectId(1, UNSORTED)).isEqualTo(emptyList)
    }

    @Test
    fun findAllByProjectIdForCollaboratorPermissionTest() {
        every { persistence.findAllByProjectIdForDropdown(1, UNSORTED) } returns listOf(projectPartnerSummary)
        every { userAuthorization.hasPermissionForProject(UserRolePermission.ProjectReportingView, 1) } returns false
        every { userAuthorization.hasPermissionForProject(UserRolePermission.ProjectReportingEdit, 1) } returns false

        every { securityService.getUserIdOrThrow() } returns 2
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(2, 1) } returns setOf(projectPartnerReportingCollaborator)

        Assertions.assertThat(getInteractor.findAllByProjectId(1, UNSORTED))
            .isEqualTo(listOf(projectPartnerSummary))
    }

}
