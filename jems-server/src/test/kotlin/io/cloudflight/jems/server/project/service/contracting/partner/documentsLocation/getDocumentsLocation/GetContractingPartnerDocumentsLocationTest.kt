package io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.getDocumentsLocation

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocationPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class GetContractingPartnerDocumentsLocationTest: UnitTest()  {

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
    }

    @MockK
    lateinit var documentsLocationPersistence: ContractingPartnerDocumentsLocationPersistence

    @InjectMockKs
    lateinit var interactor: GetContractingPartnerDocumentsLocation

    @Test
    fun `get documents location`() {
        every { documentsLocationPersistence
            .getDocumentsLocation(partnerId)
        } returns documentsLocation
        Assertions.assertThat(interactor.getDocumentsLocation(partnerId)).isEqualTo(documentsLocation)
    }
}
