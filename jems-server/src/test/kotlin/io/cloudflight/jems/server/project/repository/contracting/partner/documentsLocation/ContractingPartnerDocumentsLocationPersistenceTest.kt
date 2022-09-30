package io.cloudflight.jems.server.project.repository.contracting.partner.documentsLocation

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerDocumentsLocationEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.util.Optional

internal class ContractingPartnerDocumentsLocationPersistenceTest: UnitTest()  {

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

        private val documentsLocationEntity = ProjectContractingPartnerDocumentsLocationEntity(
            id = 18L,
            projectPartner = mockk(),
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
    lateinit var documentsLocationsRepository: ContractingPartnerDocumentsLocationRepository

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @InjectMockKs
    lateinit var persistence: ContractingPartnerDocumentsLocationPersistenceProvider

    @Test
    fun `get documents location`() {
        val partnerId = 20L
        every { documentsLocationsRepository.findByProjectPartnerId(partnerId) } returns Optional.of(documentsLocationEntity)
        every { documentsLocationEntity.projectPartner.id } returns partnerId
        Assertions.assertThat(persistence.getDocumentsLocation(partnerId)).isEqualTo(documentsLocation)
    }

    @Test
    fun `update documents location`() {
        every { documentsLocationsRepository.findByProjectPartnerId(partnerId) } returns Optional.of(documentsLocationEntity)
        every { projectPartnerRepository.findById(any()) } returns Optional.of(mockk())
        every { documentsLocationsRepository.save(any()) } returns documentsLocationEntity
        every { documentsLocationEntity.projectPartner.id } returns partnerId
        Assertions.assertThat(persistence.updateDocumentsLocation(partnerId, documentsLocation))
            .isEqualTo(documentsLocation)
    }
}
