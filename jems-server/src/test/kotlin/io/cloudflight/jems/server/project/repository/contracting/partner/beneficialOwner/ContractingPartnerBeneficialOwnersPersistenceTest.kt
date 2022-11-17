package io.cloudflight.jems.server.project.repository.contracting.partner.beneficialOwner

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.project.entity.contracting.partner.ProjectContractingPartnerBeneficialOwnerEntity
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.contracting.partner.beneficialOwner.ContractingPartnerBeneficialOwner
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.utils.partner.projectPartnerEntity
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.springframework.context.ApplicationEventPublisher
import java.time.LocalDate
import java.util.Optional

internal class ContractingPartnerBeneficialOwnersPersistenceTest : UnitTest() {

    companion object {
        const val projectId = 1L

        private val projectSummary = ProjectSummary(
            id = projectId,
            customIdentifier = "01",
            callName = "",
            acronym = "project acronym",
            status = ApplicationStatus.CONTRACTED
        )

        private val beneficialOwner = ContractingPartnerBeneficialOwner(
            id = 18L,
            partnerId = 20L,
            firstName = "Test1",
            lastName = "Sample1",
            vatNumber = "123456",
            birth = null
        )

        private val beneficialOwnerToBeAdded = ContractingPartnerBeneficialOwner(
            id = 21L,
            partnerId = 20L,
            firstName = "Test3",
            lastName = "Sample3",
            vatNumber = "203041",
            birth = null
        )

        private val beneficialOwnerToBeUpdatedTo = ContractingPartnerBeneficialOwner(
            id = 18L,
            partnerId = 20L,
            firstName = "Test20",
            lastName = "Sample12",
            vatNumber = "1234567",
            birth = LocalDate.of(2022, 10, 10)
        )

        private val beneficialOwnerEntity = ProjectContractingPartnerBeneficialOwnerEntity(
            id = 18L,
            projectPartner = mockk(),
            firstName = "Test1",
            lastName = "Sample1",
            vatNumber = "123456",
            birth = null
        )

        private val beneficialOwnerEntityToBeDeleted = ProjectContractingPartnerBeneficialOwnerEntity(
            id = 19L,
            projectPartner = mockk(),
            firstName = "Test2",
            lastName = "Sample2",
            vatNumber = "203040",
            birth = null
        )

        private val beneficialOwnerEntityToBeAdded = ProjectContractingPartnerBeneficialOwnerEntity(
            id = 21L,
            projectPartner = projectPartnerEntity(),
            firstName = "Test3",
            lastName = "Sample3",
            vatNumber = "203041",
            birth = null
        )

        private val beneficialOwnerEntityToBeUpdatedTo = ProjectContractingPartnerBeneficialOwnerEntity(
            id = 18L,
            projectPartner = mockk(),
            firstName = "Test20",
            lastName = "Sample12",
            vatNumber = "1234567",
            birth = LocalDate.of(2022, 10, 10)
        )
    }

    @MockK
    lateinit var beneficialOwnersRepository: ContractingPartnerBeneficialOwnersRepository

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var projectPersistence: ProjectPersistenceProvider

    @InjectMockKs
    lateinit var persistence: ContractingPartnerBeneficialOwnersPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(auditPublisher)
    }

    @Test
    fun `get beneficial owners`() {
        val partnerId = 20L
        every { beneficialOwnersRepository.findTop10ByProjectPartnerId(partnerId) } returns
                mutableListOf(beneficialOwnerEntity)
        every { beneficialOwnerEntity.projectPartner.id } returns partnerId
        Assertions.assertThat(persistence.getBeneficialOwners(partnerId))
            .containsExactly(beneficialOwner)
    }

    @ParameterizedTest(name = "can add a beneficial owner and trigger an audit log")
    @EnumSource(value = ApplicationStatus::class, names = ["CONTRACTED"])
    fun `update beneficial owners - add an item`() {
        val partnerId = 20L
        val partnerName = "LP0" // from 'projectPartnerEntity()'

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { beneficialOwnersRepository.findTop10ByProjectPartnerId(partnerId) } returns
                mutableListOf(beneficialOwnerEntity) andThen
                mutableListOf(beneficialOwnerEntity, beneficialOwnerEntityToBeAdded)

        every { beneficialOwnerEntity.projectPartner.id } returns partnerId
        every { projectPartnerRepository.findById(partnerId) } returns Optional.of(projectPartnerEntity())
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary

        val addedSlot = slot<Iterable<ProjectContractingPartnerBeneficialOwnerEntity>>()
        every { beneficialOwnersRepository.deleteAll(capture(addedSlot)) } answers {}
        every { beneficialOwnersRepository.save(any()) } returns beneficialOwnerEntityToBeAdded

        persistence.updateBeneficialOwners(projectId, partnerId, listOf(beneficialOwner, beneficialOwnerToBeAdded))

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_BENEFICIAL_OWNER_ADDED,
                project = AuditProject(
                    id = projectSummary.id.toString(),
                    customIdentifier = projectSummary.customIdentifier,
                    name = projectSummary.acronym
                ),
                description = "Project beneficial owner added to partner $partnerName: " +
                        "firstName '${beneficialOwnerToBeAdded.firstName}', " +
                        "lastName '${beneficialOwnerToBeAdded.lastName}', " +
                        "birth '${beneficialOwnerToBeAdded.birth}', " +
                        "vatNumber '${beneficialOwnerToBeAdded.vatNumber}';\n"
            )
        )
    }

    @ParameterizedTest(name = "can remove a beneficial owner and trigger an audit log")
    @EnumSource(value = ApplicationStatus::class, names = ["CONTRACTED"])
    fun `update beneficial owners - remove an item`() {
        val partnerId = 20L
        val partnerName = "LP0" // from 'projectPartnerEntity()'

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { beneficialOwnersRepository.findTop10ByProjectPartnerId(partnerId) } returns
                mutableListOf(beneficialOwnerEntity, beneficialOwnerEntityToBeDeleted) andThen
                mutableListOf(beneficialOwnerEntity)

        every { beneficialOwnerEntity.projectPartner.id } returns partnerId
        every { beneficialOwnerEntityToBeDeleted.projectPartner.id } returns partnerId
        every { projectPartnerRepository.findById(partnerId) } returns Optional.of(projectPartnerEntity())
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary

        val deletedSlot = slot<Iterable<ProjectContractingPartnerBeneficialOwnerEntity>>()
        every { beneficialOwnersRepository.deleteAll(capture(deletedSlot)) } answers { }
        every { beneficialOwnersRepository.save(any()) } returnsArgument 0

        persistence.updateBeneficialOwners(projectId, partnerId, listOf(beneficialOwner))
        Assertions.assertThat(deletedSlot.captured.map { it.id }).containsExactly(beneficialOwnerEntityToBeDeleted.id)

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_BENEFICIAL_OWNER_REMOVED,
                project = AuditProject(
                    id = projectSummary.id.toString(),
                    customIdentifier = projectSummary.customIdentifier,
                    name = projectSummary.acronym
                ),
                description = "Project beneficial owner removed from partner $partnerName: " +
                        "firstName '${beneficialOwnerEntityToBeDeleted.firstName}', " +
                        "lastName '${beneficialOwnerEntityToBeDeleted.lastName}', " +
                        "birth '${beneficialOwnerEntityToBeDeleted.birth}', " +
                        "vatNumber '${beneficialOwnerEntityToBeDeleted.vatNumber}';\n"
            )
        )
    }

    @ParameterizedTest(name = "can update a beneficial owner and trigger an audit log")
    @EnumSource(value = ApplicationStatus::class, names = ["CONTRACTED"])
    fun `update beneficial owners - update an item`() {
        val partnerId = 20L
        val partnerName = "LP0" // from 'projectPartnerEntity()'

        val auditSlot = slot<AuditCandidateEvent>()
        every { auditPublisher.publishEvent(capture(auditSlot)) } answers {}
        every { beneficialOwnersRepository.findTop10ByProjectPartnerId(partnerId) } returns
                mutableListOf(beneficialOwnerEntity) andThen
                mutableListOf(beneficialOwnerEntity)

        every { beneficialOwnerEntity.projectPartner.id } returns partnerId
        every { beneficialOwnerEntityToBeUpdatedTo.projectPartner.id } returns partnerId
        every { projectPartnerRepository.findById(partnerId) } returns Optional.of(projectPartnerEntity())
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary

        val updatedSlot = slot<Iterable<ProjectContractingPartnerBeneficialOwnerEntity>>()
        every { beneficialOwnersRepository.deleteAll(capture(updatedSlot)) } answers {}
        every { beneficialOwnersRepository.save(any()) } returnsArgument 0

        persistence.updateBeneficialOwners(projectId, partnerId, listOf(beneficialOwnerToBeUpdatedTo))

        verify(exactly = 1) { auditPublisher.publishEvent(capture(auditSlot)) }
        Assertions.assertThat(auditSlot.captured.auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_BENEFICIAL_OWNER_CHANGED,
                project = AuditProject(
                    id = projectSummary.id.toString(),
                    customIdentifier = projectSummary.customIdentifier,
                    name = projectSummary.acronym
                ),
                description = "Project beneficial owner changed for partner $partnerName:\n" +
                        "firstName changed from '${beneficialOwner.firstName}' to '${beneficialOwnerToBeUpdatedTo.firstName}',\n" +
                        "lastName changed from '${beneficialOwner.lastName}' to '${beneficialOwnerToBeUpdatedTo.lastName}',\n" +
                        "birth changed from ${beneficialOwner.birth} to ${beneficialOwnerToBeUpdatedTo.birth},\n" +
                        "vatNumber changed from '${beneficialOwner.vatNumber}' to '${beneficialOwnerToBeUpdatedTo.vatNumber}'"
            )
        )
    }
}