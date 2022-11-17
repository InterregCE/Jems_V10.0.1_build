package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionPartnerAssignment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.INSTITUTION_ID
import io.cloudflight.jems.server.controllerInstitution.nutsAustria
import io.cloudflight.jems.server.controllerInstitution.nutsRomania
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

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

        private  val institutionPartnerDetailList = listOf(
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
                partnerSortNumber = 1,
                partnerNuts3 = "Cluj (RO113)",
                partnerNuts3Code = "RO113",
                country = "România",
                countryCode = "RO",
                city = "Cluj",
                postalCode = "1998307",
                callId = 1L,
                projectId = 1L,
                projectCustomIdentifier = "0001",
                projectAcronym = "Project Test"
            )
        )

    }


    @MockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @InjectMockKs
    lateinit var getInstitutionPartnerAssignment: GetInstitutionPartnerAssignment



    @Test
    fun `get institutions partners assignments`() {

        val institutionPartnerDetailListWithInstitutions = listOf(
            institutionPartnerDetailList[0].copy(
                partnerNutsCompatibleInstitutions = mutableSetOf(IdNamePair(1L, "institution one"))
            ),
            institutionPartnerDetailList[1].copy(
                partnerNutsCompatibleInstitutions = mutableSetOf(IdNamePair(2L, "institution two"))
            )
        )
        every { controllerInstitutionPersistence.getAllControllerInstitutions() } returns institutionEntities
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignments(Pageable.unpaged()) } returns PageImpl(
            institutionPartnerDetailList
        )
        assertThat(getInstitutionPartnerAssignment.getInstitutionPartnerAssignments(Pageable.unpaged()).content).containsAll(
            institutionPartnerDetailListWithInstitutions
        )

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

        every { controllerInstitutionPersistence.getAllControllerInstitutions() } returns institutionEntities
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignments(Pageable.unpaged()) } returns PageImpl(
            listOf(assignmentDetails)
        )
        assertThat(getInstitutionPartnerAssignment.getInstitutionPartnerAssignments(Pageable.unpaged()).content).contains(
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

        every { controllerInstitutionPersistence.getAllControllerInstitutions() } returns institutionEntities
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignments(Pageable.unpaged()) } returns PageImpl(
            listOf(assignmentDetails)
        )
        assertThat(getInstitutionPartnerAssignment.getInstitutionPartnerAssignments(Pageable.unpaged()).content).contains(
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
            partnerId = 1L,
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
            projectId = 1L,
            projectCustomIdentifier = "0001",
            projectAcronym = "Project Test"
        )

        every { controllerInstitutionPersistence.getAllControllerInstitutions() } returns institutionEntities
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignments(Pageable.unpaged()) } returns PageImpl(
            listOf(assignmentDetails1, assignmentDetails2)
        )
        assertThat(getInstitutionPartnerAssignment.getInstitutionPartnerAssignments(Pageable.unpaged()).content).containsAll(
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
