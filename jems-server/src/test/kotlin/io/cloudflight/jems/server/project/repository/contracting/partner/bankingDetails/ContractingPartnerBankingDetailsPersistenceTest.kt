package io.cloudflight.jems.server.project.repository.contracting.partner.bankingDetails

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerBankingDetailsEntity
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.ContractingPartnerBankingDetails
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.getBankingDetails.GetContractingPartnerBankingDetailsPartnerNotFoundException
import io.cloudflight.jems.server.project.service.contracting.partner.bankingDetails.updateBankingDetails.UpdateContractingPartnerBankingDetailsNotAllowedException
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.utils.partner.projectPartnerEntity
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.util.Optional

internal class ContractingPartnerBankingDetailsPersistenceTest : UnitTest() {

    companion object {
        const val projectId = 1L

        private val projectSummary = ProjectSummary(
            id = projectId,
            customIdentifier = "01",
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.CONTRACTED
        )

        private val bankingDetails = ContractingPartnerBankingDetails(
            partnerId = 1L,
            accountHolder = "Test",
            accountNumber = "123",
            accountIBAN = "RO99BT123",
            accountSwiftBICCode = "MIDT123",
            bankName = "BT",
            streetName = "Test",
            streetNumber = "42A",
            postalCode = "000123",
            country = "Österreich (AT)",
            nutsTwoRegion = "Wien (AT13)",
            nutsThreeRegion = "Wien (AT130)"
        )

        private val bankingDetailsToBeUpdatedTo = ContractingPartnerBankingDetails(
            partnerId = 1L,
            accountHolder = "Testing",
            accountNumber = "1234",
            accountIBAN = "RO99BT1234",
            accountSwiftBICCode = "MIDT1234",
            bankName = "BTI",
            streetName = "Testing",
            streetNumber = "42B",
            postalCode = "0001243",
            country = "Österreich (AT)",
            nutsTwoRegion = "Wien (AT13)",
            nutsThreeRegion = "Wien (AT130)"
        )

        private val bankingDetailsEntity = ProjectContractingPartnerBankingDetailsEntity(
            partnerId = 1L,
            accountHolder = "Test",
            accountNumber = "123",
            accountIBAN = "RO99BT123",
            accountSwiftBICCode = "MIDT123",
            bankName = "BT",
            streetName = "Test",
            streetNumber = "42A",
            postalCode = "000123",
            country = "Österreich (AT)",
            nutsTwoRegion = "Wien (AT13)",
            nutsThreeRegion = "Wien (AT130)"
        )
    }

    @MockK
    lateinit var bankingDetailsRepository: ContractingPartnerBankingDetailsRepository

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var projectPersistence: ProjectPersistenceProvider

    @InjectMockKs
    lateinit var persistence: ContractingPartnerBankingDetailsPersistenceProvider

    @BeforeEach
    fun setup() {
        clearMocks(auditPublisher)
    }

    @Test
    fun `get banking details - success`() {
        val partnerId = 1L

        every { bankingDetailsRepository.findByPartnerId(partnerId) } returns bankingDetailsEntity
        every { projectPartnerRepository.findById(partnerId) } returns Optional.of(projectPartnerEntity())
        Assertions.assertThat(persistence.getBankingDetails(partnerId)).isEqualTo(bankingDetails)
    }

    @Test
    fun `get banking details - invalid partner`() {
        val partnerId = 100L

        every { projectPartnerRepository.findById(partnerId) } returns Optional.empty()
        assertThrows<GetContractingPartnerBankingDetailsPartnerNotFoundException> {
            persistence.getBankingDetails(
                partnerId
            )
        }
    }

    @ParameterizedTest(name = "can update banking details and trigger an audit log")
    @EnumSource(value = ApplicationStatus::class, names = ["CONTRACTED"])
    fun `update banking details - success`() {
        val partnerId = 2L // from 'projectPartnerEntity()'
        val partnerName = "LP0" // from 'projectPartnerEntity()'

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { bankingDetailsRepository.findByPartnerId(partnerId) } returns bankingDetailsEntity
        every { projectPartnerRepository.findById(partnerId) } returns Optional.of(projectPartnerEntity())
        every { bankingDetailsRepository.save(any()) } returnsArgument 0
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary

        persistence.updateBankingDetails(partnerId, projectId, bankingDetailsToBeUpdatedTo)

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_CONTRACT_PARTNER_INFO_CHANGE,
                project = AuditProject(
                    id = projectSummary.id.toString(),
                    customIdentifier = projectSummary.customIdentifier,
                    name = projectSummary.acronym
                ),
                description = "Banking Details fields changed for partner $partnerName:\n" +
                        "accountHolder changed from '${bankingDetails.accountHolder}' to '${bankingDetailsToBeUpdatedTo.accountHolder}',\n" +
                        "accountNumber changed from '${bankingDetails.accountNumber}' to '${bankingDetailsToBeUpdatedTo.accountNumber}',\n" +
                        "accountIBAN changed from '${bankingDetails.accountIBAN}' to '${bankingDetailsToBeUpdatedTo.accountIBAN}',\n" +
                        "accountSwiftBICCode changed from '${bankingDetails.accountSwiftBICCode}' to '${bankingDetailsToBeUpdatedTo.accountSwiftBICCode}'"
            )
        )
    }

    @ParameterizedTest(name = "can update banking details and trigger an 'empty' audit log")
    @EnumSource(value = ApplicationStatus::class, names = ["CONTRACTED"])
    fun `update banking details - success but no changes from original input`() {
        val partnerId = 2L // from 'projectPartnerEntity()'
        val partnerName = "LP0" // from 'projectPartnerEntity()'

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { bankingDetailsRepository.findByPartnerId(partnerId) } returns bankingDetailsEntity
        every { projectPartnerRepository.findById(partnerId) } returns Optional.of(projectPartnerEntity())
        every { bankingDetailsRepository.save(any()) } returnsArgument 0
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        persistence.updateBankingDetails(partnerId, projectId, bankingDetails)

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_CONTRACT_PARTNER_INFO_CHANGE,
                project = AuditProject(
                    id = projectSummary.id.toString(),
                    customIdentifier = projectSummary.customIdentifier,
                    name = projectSummary.acronym
                ),
                description = "Banking Details fields changed for partner $partnerName:\n(no-change)"
            )
        )
    }

    @Test
    fun `update banking details - invalid partner`() {
        val partnerId = 100L

        every { projectPartnerRepository.findById(partnerId) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> {
            persistence.updateBankingDetails(
                partnerId,
                projectId,
                bankingDetailsToBeUpdatedTo
            )
        }
    }

    @Test
    fun `update banking details - different projectId`() {
        val partnerId = 1L
        val projectId = 2L // different from 'projectPartnerEntity()'

        every { projectPartnerRepository.findById(partnerId) } returns Optional.of(projectPartnerEntity())
        assertThrows<UpdateContractingPartnerBankingDetailsNotAllowedException> {
            persistence.updateBankingDetails(partnerId, projectId, bankingDetailsToBeUpdatedTo)
        }
    }
}