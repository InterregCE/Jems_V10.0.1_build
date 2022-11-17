package io.cloudflight.jems.server.project.repository.contracting.partner.documentsLocation

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerDocumentsLocationEntity
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.partner.documentsLocation.ContractingPartnerDocumentsLocation
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.utils.partner.projectPartnerEntity
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.util.Optional

internal class ContractingPartnerDocumentsLocationPersistenceTest : UnitTest() {

    companion object {
        const val partnerId = 20L
        const val projectId = 1L

        private val projectSummary = ProjectSummary(
            id = projectId,
            customIdentifier = "01",
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.CONTRACTED
        )

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

        private val documentsLocationToBeUpdatedTo = ContractingPartnerDocumentsLocation(
            id = 18L,
            partnerId = partnerId,
            firstName = "Testing",
            lastName = "Sample",
            nutsThreeRegionCode = "",
            city = "Istanbul",
            countryCode = "TR",
            nutsTwoRegionCode = "",
            country = "Turkey",
            emailAddress = "sample@mail.com",
            locationNumber = "123A",
            homepage = "homepage2",
            institutionName = "Sample name",
            nutsThreeRegion = "",
            nutsTwoRegion = "",
            postalCode = "340001",
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

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var projectPersistence: ProjectPersistenceProvider

    @InjectMockKs
    lateinit var persistence: ContractingPartnerDocumentsLocationPersistenceProvider

    @Test
    fun `get documents location`() {
        val partnerId = 20L
        every { documentsLocationsRepository.findByProjectPartnerId(partnerId) } returns Optional.of(
            documentsLocationEntity
        )
        every { documentsLocationEntity.projectPartner.id } returns partnerId
        Assertions.assertThat(persistence.getDocumentsLocation(partnerId)).isEqualTo(documentsLocation)
    }

    @ParameterizedTest(name = "can update documents location and trigger an audit log")
    @EnumSource(value = ApplicationStatus::class, names = ["CONTRACTED"])
    fun `update documents location`() {
        val partnerName = "LP0" // from 'projectPartnerEntity()'

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { documentsLocationsRepository.findByProjectPartnerId(partnerId) } returns Optional.of(
            documentsLocationEntity
        )
        every { projectPartnerRepository.findById(partnerId) } returns Optional.of(projectPartnerEntity())
        every { documentsLocationsRepository.save(any()) } returns documentsLocationEntity
        every { documentsLocationEntity.projectPartner.id } returns partnerId
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary

        persistence.updateDocumentsLocation(projectId, partnerId, documentsLocationToBeUpdatedTo)

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_CONTRACT_PARTNER_INFO_CHANGE,
                project = AuditProject(
                    id = projectSummary.id.toString(),
                    customIdentifier = projectSummary.customIdentifier,
                    name = projectSummary.acronym
                ),
                description = "Documents Location fields changed for partner $partnerName:\n" +
                        "firstName changed from '${documentsLocation.firstName}' to '${documentsLocationToBeUpdatedTo.firstName}',\n" +
                        "locationNumber changed from '${documentsLocation.locationNumber}' to '${documentsLocationToBeUpdatedTo.locationNumber}',\n" +
                        "postalCode changed from '${documentsLocation.postalCode}' to '${documentsLocationToBeUpdatedTo.postalCode}',\n" +
                        "homepage changed from '${documentsLocation.homepage}' to '${documentsLocationToBeUpdatedTo.homepage}'"
            )
        )
    }
}