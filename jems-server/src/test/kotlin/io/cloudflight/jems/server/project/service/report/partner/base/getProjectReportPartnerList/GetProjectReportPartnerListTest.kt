package io.cloudflight.jems.server.project.service.report.partner.base.getProjectReportPartnerList

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Sort

internal class GetProjectReportPartnerListTest : UnitTest() {

    private fun dummySummary(id: Long): ProjectPartnerSummary {
        val mock = mockk<ProjectPartnerSummary>()
        every { mock.id } returns id
        return mock
    }

    private fun collaborator(partnerId: Long): PartnerCollaborator {
        val mock = mockk<PartnerCollaborator>()
        every { mock.partnerId } returns partnerId
        return mock
    }

    @MockK
    lateinit var persistence: PartnerPersistence

    @MockK
    lateinit var userAuthorization: UserAuthorization

    @MockK
    lateinit var partnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var interactor: GetProjectReportPartnerList

    @Test
    fun `findAllByProjectId - when global View permission`() {
        val partner = dummySummary(65L)
        every { persistence.findAllByProjectIdForDropdown(18L, any(), "Y") } returns listOf(partner)

        every { userAuthorization.hasPermissionForProject(UserRolePermission.ProjectReportingView, 18L) } returns true

        assertThat(interactor.findAllByProjectId(18L, Sort.unsorted(), "Y")).containsExactly(partner)
    }

    @Test
    fun `findAllByProjectId - no global View - filter only allowed`() {
        val userId = 991L
        val partner_55 = dummySummary(55L)
        val partner_56 = dummySummary(56L)
        val partner_57 = dummySummary(57L)

        every { persistence.findAllByProjectIdForDropdown(19L, any(), "X") } returns
            listOf(partner_55, partner_56, partner_57)

        every { userAuthorization.hasPermissionForProject(UserRolePermission.ProjectReportingView, 19L) } returns false
        every { securityService.getUserIdOrThrow() } returns userId
        every { partnerCollaboratorPersistence.findPartnersByUserAndProject(userId = userId, 19L) } returns
            setOf(collaborator(partnerId = 56L))
        every { controllerInstitutionPersistence.getRelatedProjectAndPartnerIdsForUser(userId = userId) } returns
            mapOf(19L to setOf(57L))

        assertThat(interactor.findAllByProjectId(19L, Sort.unsorted(), "X")).containsExactly(partner_56, partner_57)
    }

}
