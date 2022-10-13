package io.cloudflight.jems.server.project.controller.contracting.partner.documentsLocation

import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerDocumentsLocationDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.getDocumentsLocation.GetContractingPartnerDocumentsLocationInteractor
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.updateDocumentsLocation.UpdateContractingPartnerDocumentsLocationInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class ContractingPartnerDocumentsLocationControllerTest : UnitTest() {

    companion object {
        const val partnerId = 20L
        const val projectId = 1L

        private val documentsLocation = ContractingPartnerDocumentsLocation(
            id = 18L,
            partnerId = partnerId,
            firstName = "Test",
            lastName = "Sample",
            nutsThreeRegionCode = "",
            city = "Istanbul",
            countryCode = "TR",
            nutsTwoRegionCode = "",
            country = "Turkey",
            emailAddress = "sample@mail.com",
            locationNumber = "12A",
            homepage = "homepage",
            institutionName = "Sample name",
            nutsThreeRegion = "",
            nutsTwoRegion = "",
            postalCode = "34000",
            street = "Sample street",
            telephoneNo = "1020304050",
            title = "Title"
        )

        private val documentsLocationDTO = ContractingPartnerDocumentsLocationDTO(
            id = 18L,
            partnerId = partnerId,
            firstName = "Test",
            lastName = "Sample",
            nutsThreeRegionCode = "",
            city = "Istanbul",
            countryCode = "TR",
            nutsTwoRegionCode = "",
            country = "Turkey",
            emailAddress = "sample@mail.com",
            locationNumber = "12A",
            homepage = "homepage",
            institutionName = "Sample name",
            nutsThreeRegion = "",
            nutsTwoRegion = "",
            postalCode = "34000",
            street = "Sample street",
            telephoneNo = "1020304050",
            title = "Title"
        )
    }

    @MockK
    lateinit var getDocumentsLocationInteractor: GetContractingPartnerDocumentsLocationInteractor

    @MockK
    lateinit var updateDocumentsLocationInteractor: UpdateContractingPartnerDocumentsLocationInteractor

    @InjectMockKs
    lateinit var controller: ContractingPartnerDocumentsLocationController

    @Test
    fun `get contract partner documents location`() {
        every { getDocumentsLocationInteractor.getDocumentsLocation(partnerId) } returns documentsLocation
        Assertions.assertThat(controller.getDocumentsLocation(projectId, partnerId))
            .isEqualTo(documentsLocationDTO)
    }

    @Test
    fun `update contract partner documents location`() {
        val updateModelSlot = slot<ContractingPartnerDocumentsLocation>()
        every {
            updateDocumentsLocationInteractor.updateDocumentsLocation(projectId, partnerId, capture(updateModelSlot))
        } returns documentsLocation

        val toCreate = documentsLocationDTO
        Assertions.assertThat(controller.updateDocumentsLocation(projectId, partnerId, toCreate))
            .isEqualTo(documentsLocationDTO)

        Assertions.assertThat(updateModelSlot.captured).isEqualTo(documentsLocation)
    }
}