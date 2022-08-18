package io.cloudflight.jems.server.controllerInstitution.service.getInstitutionPartnerAssignment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.AuthenticationService
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.INSTITUTION_ID
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class GetInstitutionPartnerAssignmentTest: UnitTest() {

    companion object {

       private val institutionPartnerDetailList = listOf(
           InstitutionPartnerDetails(
                institutionId = INSTITUTION_ID,
                partnerId = 1L,
                partnerName = "A",
                partnerStatus = true,
                partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                partnerSortNumber = 1,
                partnerNuts3 = "RO113",
                partnerAddress = "",
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
               partnerNuts3 = "RO113",
               partnerAddress = "",
               callId = 1L,
               projectId = 1L,
               projectCustomIdentifier = "0001",
               projectAcronym = "Project Test"
           )
       )
    }


    @MockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @RelaxedMockK
    lateinit var authenticationService: AuthenticationService

    @InjectMockKs
    lateinit var getInstitutionPartnerAssignment: GetInstitutionPartnerAssignment


    @Test
    fun `get institutions partners assignments`() {
        every { controllerInstitutionPersistence.getInstitutionPartnerAssignments(any()) } returns PageImpl(institutionPartnerDetailList)
        assertThat(getInstitutionPartnerAssignment.getInstitutionPartnerAssignments(Pageable.unpaged()).content).isEqualTo(
            institutionPartnerDetailList
        )
    }
}
