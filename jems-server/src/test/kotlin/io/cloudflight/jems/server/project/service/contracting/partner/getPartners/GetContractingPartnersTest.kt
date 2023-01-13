package io.cloudflight.jems.server.project.service.contracting.partner.getPartners

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.UserPartnerCollaboratorPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.user.service.authorization.UserAuthorization
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.cloudflight.jems.server.user.service.model.assignment.PartnerCollaborator
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

internal class GetContractingPartnersTest: UnitTest() {

    companion object {
        private val projectPartnerSummaryContracting = ProjectPartnerSummary(
            id = 1L,
            abbreviation = "partner",
            active = true,
            role = ProjectPartnerRole.LEAD_PARTNER,
            institutionName = null
        )

        private val projectPartnerSummary = ProjectPartnerSummary(
            id = 1L,
            abbreviation = "partner",
            active = true,
            role = ProjectPartnerRole.LEAD_PARTNER,
        )

        private val controllerInstitutionList = ControllerInstitutionList(
            id = 1L,
            name = "INSTITUTION",
            description = "DESCRIPTION",
            institutionNuts = emptyList(),
            createdAt = ZonedDateTime.now()
        )

        private fun collaborator(partnerId: Long): PartnerCollaborator {
            val mock = mockk<PartnerCollaborator>()
            every { mock.partnerId } returns partnerId
            return mock
        }
    }

    @MockK
    lateinit var partnerPersistence: PartnerPersistence

    @MockK
    lateinit var institutionPersistence: ControllerInstitutionPersistence

    @MockK
    lateinit var userPartnerCollaboratorPersistence: UserPartnerCollaboratorPersistence

    @MockK
    lateinit var securityService: SecurityService

    @MockK
    lateinit var userAuthorization: UserAuthorization

    @InjectMockKs
    lateinit var interactor: GetContractingPartners


    @Test
    fun findAllByProjectIdForContracting() {
        every { partnerPersistence.findAllByProjectIdForDropdown(1, any(), "v1.0") } returns listOf(
            projectPartnerSummary
        )
        every { userAuthorization.hasPermissionForProject(UserRolePermission.ProjectContractingPartnerView, 1L) } returns false
        every { securityService.getUserIdOrThrow() } returns 99L
        every { institutionPersistence.getControllerInstitutions(partnerIds = setOf(1L)) } returns
            mapOf(Pair(1L, controllerInstitutionList))
        every { userPartnerCollaboratorPersistence.findPartnersByUserAndProject(userId = 99L, 1L) } returns
            setOf(collaborator(partnerId = 1L))
        every { institutionPersistence.getRelatedProjectAndPartnerIdsForUser(userId = 99L) } returns
            mapOf(19L to setOf(57L))
        Assertions.assertThat(interactor.findAllByProjectIdForContracting(1, mockk(), "v1.0"))
            .isEqualTo(listOf(projectPartnerSummaryContracting))
    }
}
