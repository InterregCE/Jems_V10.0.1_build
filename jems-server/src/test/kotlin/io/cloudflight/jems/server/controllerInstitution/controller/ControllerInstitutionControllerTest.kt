package io.cloudflight.jems.server.controllerInstitution.controller

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.ControllerInstitutionListDTO
import io.cloudflight.jems.api.controllerInstitutions.dto.InstitutionPartnerDetailsDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.controllerInstitution.service.assignInstitutionToPartner.AssignInstitutionToPartnerInteractor
import io.cloudflight.jems.server.controllerInstitution.service.createControllerInstitution.CreateControllerInteractor
import io.cloudflight.jems.server.controllerInstitution.service.getControllerInstitution.GetControllerInteractor
import io.cloudflight.jems.server.controllerInstitution.service.getInstitutionPartnerAssignment.GetInstitutionPartnerAssignmentInteractor
import io.cloudflight.jems.server.controllerInstitution.service.getInstitutionUserAccessLevel.GetInstitutionUserAccessLevelInteractor
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import io.cloudflight.jems.server.controllerInstitution.service.updateControllerInstitution.UpdateControllerInteractor
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

class ControllerInstitutionControllerTest: UnitTest() {

    companion object {
        private val createdAt = ZonedDateTime.now().minusDays(1)
        private val dummyControllerInstitution = ControllerInstitutionList(
            id = 1L,
            name = "dummy controller",
            description = "dummy controller description",
            institutionNuts = emptyList(),
            createdAt = createdAt
        )

        private val expectedController = ControllerInstitutionListDTO(
            id = 1L,
            name = "dummy controller",
            description = "dummy controller description",
            institutionNuts = emptyList(),
            createdAt = createdAt
        )

    }

    @MockK
    lateinit var getControllerInteractor: GetControllerInteractor

    @MockK
    lateinit var createControllerInteractor: CreateControllerInteractor

    @MockK
    lateinit var updateControllerInstitution: UpdateControllerInteractor

    @MockK
    lateinit var getInstitutionPartnerAssignment: GetInstitutionPartnerAssignmentInteractor

    @MockK
    lateinit var getInstitutionUserAccessLevel: GetInstitutionUserAccessLevelInteractor

    @MockK
    lateinit var assignInstitutionToPartnerInteractor: AssignInstitutionToPartnerInteractor

    @InjectMockKs
    lateinit var controllerInstitutionController: ControllerInstitutionController


    @Test
    fun getControllers() {
        every { getControllerInteractor.getControllers(Pageable.unpaged()) } returns
            PageImpl(listOf(dummyControllerInstitution))
        assertThat(controllerInstitutionController.getControllers(Pageable.unpaged()).content).containsExactly(
            expectedController
        )
    }

    @Test
    fun getInstitutionPartnerAssignments() {
        val institutionPartnerAssignments = listOf(
            InstitutionPartnerDetails(
                institutionId = 1L,
                partnerId = 1L,
                partnerName = "A",
                partnerStatus = true,
                partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                partnerSortNumber = 1,
                partnerNuts3 = "Wien (AT130)",
                partnerNuts3Code = "AT130",
                country = "Ostösterreich",
                countryCode = "AT",
                city = "Wien",
                postalCode = "299281",
                callId = 1L,
                projectId = 1L,
                projectCustomIdentifier = "0001",
                projectAcronym = "Project Test",
                partnerNutsCompatibleInstitutions = mutableSetOf(IdNamePair(1L, "dummy institution"))
            ),
            InstitutionPartnerDetails(
                institutionId = 2L,
                partnerId = 2L,
                partnerName = "B",
                partnerStatus = true,
                partnerRole = ProjectPartnerRole.PARTNER,
                partnerSortNumber = 2,
                partnerNuts3 = null,
                partnerNuts3Code = null,
                country = "Ostösterreich",
                countryCode = "AT",
                city = "Wien",
                postalCode = null,
                callId = 1L,
                projectId = 1L,
                projectCustomIdentifier = "0001",
                projectAcronym = "Project Test",
                partnerNutsCompatibleInstitutions = mutableSetOf(IdNamePair(1L, "dummy institution"))
            )
        )

        val expectedInstitutionPartnerAssignments = listOf(
            InstitutionPartnerDetailsDTO(
                institutionId = 1L,
                partnerId = 1L,
                partnerName = "A",
                partnerStatus = true,
                partnerRole = ProjectPartnerRoleDTO.LEAD_PARTNER,
                partnerSortNumber = 1,
                partnerNuts3 = "Wien (AT130)",
                partnerAddress = "Ostösterreich, Wien, 299281",
                callId = 1L,
                projectId = 1L,
                projectCustomIdentifier = "0001",
                projectAcronym = "Project Test",
                partnerNutsCompatibleInstitutions = setOf(IdNamePairDTO(1L, "dummy institution"))
            ),
            InstitutionPartnerDetailsDTO(
                institutionId = 2L,
                partnerId = 2L,
                partnerName = "B",
                partnerStatus = true,
                partnerRole = ProjectPartnerRoleDTO.PARTNER,
                partnerSortNumber = 2,
                partnerNuts3 = null,
                partnerAddress = "",
                callId = 1L,
                projectId = 1L,
                projectCustomIdentifier = "0001",
                projectAcronym = "Project Test",
                partnerNutsCompatibleInstitutions = setOf(IdNamePairDTO(1L, "dummy institution"))
            )
        )

        every { getInstitutionPartnerAssignment.getInstitutionPartnerAssignments(Pageable.unpaged()) } returns
            PageImpl(institutionPartnerAssignments)
        assertThat(controllerInstitutionController.getInstitutionPartnerAssignments(Pageable.unpaged()).content)
            .containsAll(expectedInstitutionPartnerAssignments)
    }
}
