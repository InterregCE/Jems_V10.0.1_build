package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionPartnerAssignment

import io.cloudflight.jems.api.controllerInstitutions.dto.InstitutionPartnerSearchRequest
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.controllerInstitution.INSTITUTION_ID
import io.cloudflight.jems.server.controllerInstitution.nutsAustria
import io.cloudflight.jems.server.controllerInstitution.nutsRomania
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil.Companion.adminUser
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import java.time.ZonedDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class GetInstitutionPartnerAssignmentTest: UnitTest() {

    companion object {

        private val institutionEntities =  listOf(
            ControllerInstitutionEntity(
                id = 1L,
                name = "institution one",
                description = "",
                institutionNuts = mutableSetOf(nutsAustria),
                createdAt = ZonedDateTime.now().minusDays(1)
            ),
            ControllerInstitutionEntity(
                id = 2L,
                name = "institution two",
                description = "",
                institutionNuts = mutableSetOf(nutsRomania),
                createdAt = ZonedDateTime.now().minusDays(1)
            )
        )

        private val institutionPartnerDetailList = listOf(
            InstitutionPartnerDetails(
                institutionId = INSTITUTION_ID,
                partnerId = 1L,
                partnerName = "A",
                partnerStatus = true,
                partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                partnerSortNumber = 1,
                partnerNuts3 = "Wien (AT130)",
                partnerNuts3Code = "AT130",
                country = "Österreich",
                countryCode = "AT",
                city = "Wien",
                postalCode = "299281",
                callId = 1L,
                projectId = 1L,
                projectCustomIdentifier = "0001",
                projectAcronym = "Project Test"
            ),
            InstitutionPartnerDetails(
                institutionId = INSTITUTION_ID,
                partnerId = 2L,
                partnerName = "B",
                partnerStatus = true,
                partnerRole = ProjectPartnerRole.PARTNER,
                partnerSortNumber = 2,
                partnerNuts3 = "Cluj (RO113)",
                partnerNuts3Code = "RO113",
                country = "România",
                countryCode = "RO",
                city = "Cluj",
                postalCode = "1998307",
                callId = 1L,
                projectId = 2L,
                projectCustomIdentifier = "0002",
                projectAcronym = "Project Test #2"
            )
        )

    }

    @MockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @MockK
    lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var getInstitutionPartnerAssignment: GetInstitutionPartnerAssignment

    @BeforeEach
    fun setup() {
        every { securityService.currentUser } returns adminUser
        every { securityService.getUserIdOrThrow() } returns adminUser.user.id
        every { securityService.currentUser?.hasPermission(UserRolePermission.AssignmentsUnlimited) } returns true
    }

    @Test
    fun `get institutions partners assignments - empty search request`() {
        val institutionPartnerDetailListWithInstitutions = listOf(
            institutionPartnerDetailList[0].copy(
                partnerNutsCompatibleInstitutions = mutableSetOf(IdNamePair(1L, "institution one"))
            ),
            institutionPartnerDetailList[1].copy(
                partnerNutsCompatibleInstitutions = mutableSetOf(IdNamePair(2L, "institution two"))
            )
        )
        val emptyRequest = InstitutionPartnerSearchRequest(
            callId  = null,
            projectId = null,
            acronym = "",
            partnerName = "",
            partnerNuts = emptySet(),
            globallyRestrictedNuts = null // = not restricted
        )

        every { controllerInstitutionPersistence.getAllControllerInstitutions() } returns institutionEntities
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignments(Pageable.unpaged(), emptyRequest) } returns
                PageImpl(institutionPartnerDetailList)

        val result = getInstitutionPartnerAssignment.getInstitutionPartnerAssignments(Pageable.unpaged(), emptyRequest)

        assertThat(result.content).containsAll(institutionPartnerDetailListWithInstitutions)
    }

    @Test
    fun `get institutions partners assignments - active search request`() {
        val institutionPartnerDetailListWithInstitutions = listOf(
            institutionPartnerDetailList[0].copy(
                partnerNutsCompatibleInstitutions = mutableSetOf(IdNamePair(1L, "institution one"))
            ),
            institutionPartnerDetailList[1].copy(
                partnerNutsCompatibleInstitutions = mutableSetOf(IdNamePair(2L, "institution two"))
            )
        )
        val searchRequest = InstitutionPartnerSearchRequest(
            callId  = 1,
            projectId = null,
            acronym = "",
            partnerName = "",
            partnerNuts = emptySet(),
            globallyRestrictedNuts = null // = not restricted
        )

        every { controllerInstitutionPersistence.getAllControllerInstitutions() } returns institutionEntities
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignments(Pageable.unpaged(), searchRequest) } returns
                PageImpl(institutionPartnerDetailList)

        val result = getInstitutionPartnerAssignment.getInstitutionPartnerAssignments(Pageable.unpaged(), searchRequest)

        assertThat(result.content).containsAll(institutionPartnerDetailListWithInstitutions)
    }

    @Test
    fun `get institutions partners assignments - active search request including NUTS`() {
        val institutionPartnerDetailListWithInstitutions = listOf(
            institutionPartnerDetailList[1].copy(
                partnerNutsCompatibleInstitutions = mutableSetOf(IdNamePair(2L, "institution two"))
            )
        )
        val searchRequest = InstitutionPartnerSearchRequest(
            callId  = 1,
            projectId = "2",
            acronym = "Project",
            partnerName = "B",
            partnerNuts = setOf("RO1"),
            globallyRestrictedNuts = setOf("RO113")
        )

        every { controllerInstitutionPersistence.getAllControllerInstitutions() } returns institutionEntities
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignments(Pageable.unpaged(), searchRequest) } returns
                PageImpl(mutableListOf(institutionPartnerDetailList[1]))

        val result = getInstitutionPartnerAssignment.getInstitutionPartnerAssignments(Pageable.unpaged(), searchRequest)

        assertThat(result.content).containsAll(institutionPartnerDetailListWithInstitutions)
    }

    @Test
    fun `partner with incomplete address matches correct institution country nuts`() {
        val assignmentDetails =  InstitutionPartnerDetails(
            institutionId = INSTITUTION_ID,
            partnerId = 1L,
            partnerName = "A",
            partnerStatus = true,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerSortNumber = 1,
            partnerNuts3 = null,
            partnerNuts3Code = null,
            country = "Österreich",
            countryCode = "AT",
            city = null,
            postalCode = null,
            callId = 1L,
            projectId = 1L,
            projectCustomIdentifier = "0001",
            projectAcronym = "Project Test"
        )
        val emptyRequest = InstitutionPartnerSearchRequest(
            callId  = null,
            projectId = null,
            acronym = "",
            partnerName = "",
            partnerNuts = emptySet(),
            globallyRestrictedNuts = null // = not restricted
        )

        every { controllerInstitutionPersistence.getAllControllerInstitutions() } returns institutionEntities
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignments(Pageable.unpaged(), emptyRequest) } returns
                PageImpl(listOf(assignmentDetails))

        val result = getInstitutionPartnerAssignment.getInstitutionPartnerAssignments(Pageable.unpaged(), emptyRequest)

        assertThat(result.content).contains(
            assignmentDetails.copy(
                partnerNutsCompatibleInstitutions = mutableSetOf(IdNamePair(1L, "institution one"))
            )
        )
    }

    @Test
    fun `partner with no address is compatible with all institutions`() {
        val assignmentDetails =  InstitutionPartnerDetails(
            institutionId = INSTITUTION_ID,
            partnerId = 1L,
            partnerName = "A",
            partnerStatus = true,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerSortNumber = 1,
            partnerNuts3 = null,
            partnerNuts3Code = null,
            country = null,
            countryCode = null,
            city = null,
            postalCode = null,
            callId = 1L,
            projectId = 1L,
            projectCustomIdentifier = "0001",
            projectAcronym = "Project Test"
        )
        val emptyRequest = InstitutionPartnerSearchRequest(
            callId  = null,
            projectId = null,
            acronym = "",
            partnerName = "",
            partnerNuts = emptySet(),
            globallyRestrictedNuts = null // = not restricted
        )

        every { controllerInstitutionPersistence.getAllControllerInstitutions() } returns institutionEntities
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignments(Pageable.unpaged(), emptyRequest) } returns
                PageImpl(listOf(assignmentDetails))

        val result = getInstitutionPartnerAssignment.getInstitutionPartnerAssignments(Pageable.unpaged(), emptyRequest)

        assertThat(result.content).contains(
            assignmentDetails.copy(
                partnerNutsCompatibleInstitutions = mutableSetOf(IdNamePair(1L, "institution one"), IdNamePair(2L, "institution two"))
            )
        )
    }

    @Test
    fun `partner with missing nuts codes matches correct institution nuts`() {
        val assignmentDetails1 =  InstitutionPartnerDetails(
            institutionId = INSTITUTION_ID,
            partnerId = 1L,
            partnerName = "A",
            partnerStatus = true,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerSortNumber = 1,
            partnerNuts3 = "Wien (AT130)",
            partnerNuts3Code = null,
            country = "Österreich (AT)",
            countryCode = null,
            city = "Wien",
            postalCode = null,
            callId = 1L,
            projectId = 1L,
            projectCustomIdentifier = "0001",
            projectAcronym = "Project Test"
        )

        val assignmentDetails2 = InstitutionPartnerDetails(
            institutionId = INSTITUTION_ID,
            partnerId = 2L,
            partnerName = "B",
            partnerStatus = true,
            partnerRole = ProjectPartnerRole.PARTNER,
            partnerSortNumber = 2,
            partnerNuts3 = "Cluj (RO113)",
            partnerNuts3Code = null,
            country = "România (RO)",
            countryCode = null,
            city = "Cluj",
            postalCode = null,
            callId = 1L,
            projectId = 2L,
            projectCustomIdentifier = "0002",
            projectAcronym = "Project Test #2"
        )
        val emptyRequest = InstitutionPartnerSearchRequest(
            callId  = null,
            projectId = null,
            acronym = "",
            partnerName = "",
            partnerNuts = emptySet(),
            globallyRestrictedNuts = null // = not restricted
        )

        every { controllerInstitutionPersistence.getAllControllerInstitutions() } returns institutionEntities
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignments(Pageable.unpaged(), emptyRequest) } returns
                PageImpl(listOf(assignmentDetails1, assignmentDetails2))

        val result = getInstitutionPartnerAssignment.getInstitutionPartnerAssignments(Pageable.unpaged(), emptyRequest)

        assertThat(result.content).containsAll(
            listOf(
                assignmentDetails1.copy(
                    partnerNutsCompatibleInstitutions = mutableSetOf(IdNamePair(1L, "institution one"))
                ),
                assignmentDetails2.copy(
                    partnerNutsCompatibleInstitutions = mutableSetOf(IdNamePair(2L, "institution two"))
                )
            )
        )
    }
}
