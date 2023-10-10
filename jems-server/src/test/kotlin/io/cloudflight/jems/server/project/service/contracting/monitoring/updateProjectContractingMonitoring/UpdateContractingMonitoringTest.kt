package io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingMonitoring

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.payments.entity.PaymentGroupingId
import io.cloudflight.jems.server.payments.model.regular.PaymentPartnerToCreate
import io.cloudflight.jems.server.payments.model.regular.PaymentPerPartner
import io.cloudflight.jems.server.payments.model.regular.PaymentToCreate
import io.cloudflight.jems.server.payments.model.regular.contributionMeta.ContributionMeta
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.budget.get_project_budget.GetProjectBudget
import io.cloudflight.jems.server.project.service.budget.model.PartnerBudget
import io.cloudflight.jems.server.project.service.contracting.ContractingModificationDeniedException
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringExtendedOption
import io.cloudflight.jems.server.project.service.contracting.model.ContractingMonitoringOption
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoring
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingMonitoringAddDate
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.model.ProjectFull
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.internal.util.collections.Sets
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal
import java.time.ZonedDateTime

class UpdateContractingMonitoringTest : UnitTest() {

    companion object {
        private const val projectId = 1L
        private const val version = "2.0"
        private const val lumpSumId = 2L
        private const val orderNr = 1

        private val TIME = ZonedDateTime.now()

        private val project = ProjectFull(
            id = projectId,
            customIdentifier = "TSTCM",
            callSettings = mockk(),
            acronym = "TCM",
            applicant = mockk(),
            projectStatus = mockk(),
            duration = 11
        )
        private val projectSummary = ProjectSummary(
            id = projectId,
            customIdentifier = "TSTCM",
            callId = 1L,
            callName = "Test contracting monitoring",
            acronym = "TCM",
            status = ApplicationStatus.APPROVED,
            firstSubmissionDate = ZonedDateTime.parse("2022-06-20T10:00:00+02:00"),
            lastResubmissionDate = ZonedDateTime.parse("2022-07-20T10:00:00+02:00"),
            specificObjectiveCode = "SO1.1",
            programmePriorityCode = "P1"
        )
        private val lumpSums = listOf(
            ProjectLumpSum(
                orderNr = orderNr,
                programmeLumpSumId = lumpSumId,
                period = 1,
                lumpSumContributions = listOf(
                    ProjectPartnerLumpSum(
                        partnerId = 52L,
                        amount = BigDecimal.valueOf(10041L, 2),
                    )
                ),
                fastTrack = true,
                readyForPayment = true,
                comment = null,
                paymentEnabledDate = TIME,
                lastApprovedVersionBeforeReadyForPayment = "v1.0"
            )
        )

        private val lumpSumsUpdated = listOf(
            ProjectLumpSum(
                orderNr = orderNr,
                programmeLumpSumId = lumpSumId,
                period = 1,
                lumpSumContributions = listOf(),
                fastTrack = true,
                readyForPayment = true,
                comment = "Test",
                paymentEnabledDate = TIME,
                lastApprovedVersionBeforeReadyForPayment = "v1.0"
            )
        )

        private val monitoring = ProjectContractingMonitoring(
            projectId = projectId,
            startDate = null,
            endDate = null,
            typologyProv94 = ContractingMonitoringExtendedOption.Partly,
            typologyProv94Comment = "typologyProv94Comment",
            typologyProv95 = ContractingMonitoringExtendedOption.Yes,
            typologyProv95Comment = "typologyProv95Comment",
            typologyStrategic = ContractingMonitoringOption.No,
            typologyStrategicComment = "typologyStrategicComment",
            typologyPartnership = ContractingMonitoringOption.Yes,
            typologyPartnershipComment = "typologyPartnershipComment",
            addDates = listOf(ProjectContractingMonitoringAddDate(
                projectId = projectId,
                number = 1,
                entryIntoForceDate = ZonedDateTime.parse("2022-07-22T10:00:00+02:00").toLocalDate(),
                comment = "comment"
            )),
            fastTrackLumpSums = lumpSumsUpdated,
            dimensionCodes = emptyList()
        )

        private val paymentPerPartner = PaymentPerPartner(
            projectId = projectId,
            partnerId = 1,
            orderNr = orderNr,
            programmeLumpSumId = lumpSumId,
            programmeFundId = 1,
            amountApprovedPerPartner = BigDecimal.ONE
        )

        private fun partnerBudget(partnerId: Long, total: BigDecimal): PartnerBudget {
            val mock = mockk<PartnerBudget>()
            every { mock.partner.id } returns partnerId
            every { mock.totalCosts } returns total
            return mock
        }

        val fund = mockk<ProgrammeFund>().also {
            every { it.id } returns 1L
        }

        private val partner_52_coFin = ProjectPartnerCoFinancingAndContribution(
            finances = listOf(
                ProjectPartnerCoFinancing(ProjectPartnerCoFinancingFundTypeDTO.MainFund, fund, BigDecimal.valueOf(15)),
                ProjectPartnerCoFinancing(ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution, null, BigDecimal.valueOf(85)),
            ),
            partnerContributions = listOf(
                ProjectPartnerContribution(null, null, ProjectPartnerContributionStatusDTO.Public, BigDecimal.valueOf(3755L, 2), true),
                ProjectPartnerContribution(null, null, ProjectPartnerContributionStatusDTO.AutomaticPublic, BigDecimal.valueOf(4250L, 2), false),
                ProjectPartnerContribution(null, null, ProjectPartnerContributionStatusDTO.Private, BigDecimal.valueOf(4750L, 2), false),
            ),
            partnerAbbreviation = "",
        )
    }

    @MockK
    lateinit var contractingMonitoringPersistence: ContractingMonitoringPersistence

    @MockK
    lateinit var projectPersistence: ProjectPersistenceProvider

    @MockK
    lateinit var versionPersistence: ProjectVersionPersistence

    @MockK
    lateinit var projectLumpSumPersistence: ProjectLumpSumPersistence

    @MockK
    lateinit var partnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistence

    @MockK
    lateinit var getProjectBudget: GetProjectBudget

    @RelaxedMockK
    lateinit var validator: ContractingValidator

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var paymentPersistence: PaymentPersistence

    @InjectMockKs
    lateinit var updateContractingMonitoring: UpdateContractingMonitoring

    @BeforeEach
    fun setup() {
        clearMocks(auditPublisher, validator, getProjectBudget, partnerCoFinancingPersistence)
    }

    @Test
    fun `add project management to approved application`() {
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoring.copy(
            fastTrackLumpSums = lumpSums
        )
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version
        every { contractingMonitoringPersistence.updateContractingMonitoring(monitoring) } returns monitoring
        every { projectLumpSumPersistence.getLumpSums(projectId, version)} returns lumpSums
        every { projectLumpSumPersistence.updateLumpSums(projectId, lumpSumsUpdated)} returns lumpSumsUpdated
        every { getProjectBudget.getBudget(projectId, version) } returns emptyList()

        assertThat(updateContractingMonitoring.updateContractingMonitoring(projectId, monitoring)).isEqualTo(monitoring)
        verify(exactly = 0) { auditPublisher.publishEvent(any()) }
    }

    @Test
    fun `add project management to contracted application without startDate`() {
        every {
            projectPersistence.getProjectSummary(projectId)
        } returns projectSummary.copy(status = ApplicationStatus.CONTRACTED)
        val monitoringOther = monitoring.copy(
            fastTrackLumpSums = lumpSumsUpdated
        )
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoringOther
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version
        every { contractingMonitoringPersistence.updateContractingMonitoring(monitoringOther) } returns monitoringOther
        every { projectLumpSumPersistence.getLumpSums(projectId, version) } returns lumpSumsUpdated
        every { projectLumpSumPersistence.updateLumpSums(projectId, lumpSumsUpdated)} returns lumpSumsUpdated
        every { getProjectBudget.getBudget(projectId, version) } returns emptyList()

        assertThat(updateContractingMonitoring.updateContractingMonitoring(projectId, monitoringOther))
            .isEqualTo(monitoringOther)
        val slotAudit = slot<AuditCandidateEvent>()
        verify(exactly = 0) { auditPublisher.publishEvent(capture(slotAudit)) }
    }

    @Test
    fun `add project management to contracted application and fields changed`() {
        mockkObject(ContractingValidator.Companion)
        every {
            projectPersistence.getProjectSummary(projectId)
        } returns projectSummary.copy(status = ApplicationStatus.CONTRACTED)
        every { ContractingValidator.validateProjectStatusForModification(projectSummary) } returns Unit
        val oldDate = ZonedDateTime.parse("2022-06-02T10:00:00+02:00").toLocalDate()
        val lumpSumsNotReady = listOf(lumpSums.first().copy(readyForPayment = false))
        every {
            contractingMonitoringPersistence.getContractingMonitoring(projectId)
        } returns monitoring.copy(
            startDate = oldDate,
            typologyProv94 = ContractingMonitoringExtendedOption.No,
            typologyProv95 = ContractingMonitoringExtendedOption.Partly,
            typologyStrategic = ContractingMonitoringOption.Yes,
            typologyPartnership = ContractingMonitoringOption.No,
            addDates = listOf(
                ProjectContractingMonitoringAddDate(projectId, 1, oldDate, "comment1"),
                ProjectContractingMonitoringAddDate(projectId, 2, oldDate, "comment2")
            ),
            fastTrackLumpSums = lumpSumsNotReady
        )
        val monitoringNew = monitoring.copy(
            startDate = ZonedDateTime.parse("2022-07-01T10:00:00+02:00").toLocalDate(),
            fastTrackLumpSums = listOf(lumpSumsUpdated.first()
                .copy(lumpSumContributions = lumpSumsNotReady.first().lumpSumContributions))
        )
        every { contractingMonitoringPersistence.existsSavedInstallment(projectId, lumpSumId, orderNr) } returns false
        every { contractingMonitoringPersistence.updateContractingMonitoring(monitoringNew) } returns monitoringNew
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version
        every { projectPersistence.getProject(projectId, version) } returns project
        every { projectPersistence.updateProjectContractedOnDates(projectId, monitoring.addDates.get(0).entryIntoForceDate) } answers {}
        every { projectLumpSumPersistence.getLumpSums(projectId, version)} returns lumpSumsNotReady
        every { projectLumpSumPersistence.updateLumpSums(projectId, any())} returns lumpSumsUpdated
        every { paymentPersistence.getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(1, Sets.newSet(1))} returns
            listOf(paymentPerPartner)
        val payments = slot<Map<PaymentGroupingId, PaymentToCreate>>()
        every { paymentPersistence.saveFTLSPayments(projectId, capture(payments)) } answers { }
        every { getProjectBudget.getBudget(projectId, version) } returns listOf(
            partnerBudget(partnerId = 52L, BigDecimal.valueOf(150L)),
            partnerBudget(partnerId = 53L, BigDecimal.valueOf(2112L, 2)),
        )
        every { partnerCoFinancingPersistence.getCoFinancingAndContributions(52L, version) } returns partner_52_coFin
        val payContribs = slot<Collection<ContributionMeta>>()
        every { paymentPersistence.storePartnerContributionsWhenReadyForPayment(capture(payContribs)) } answers { }

        val result = updateContractingMonitoring.updateContractingMonitoring(projectId, monitoringNew)
        assertThat(result)
            .isEqualTo(
                ProjectContractingMonitoring(
                    projectId = projectId,
                    startDate = ZonedDateTime.parse("2022-07-01T10:00:00+02:00").toLocalDate(),
                    endDate = ZonedDateTime.parse("2023-05-31T10:00:00+02:00").toLocalDate(),
                    typologyProv94 = ContractingMonitoringExtendedOption.Partly,
                    typologyProv94Comment = "typologyProv94Comment",
                    typologyProv95 = ContractingMonitoringExtendedOption.Yes,
                    typologyProv95Comment = "typologyProv95Comment",
                    typologyStrategic = ContractingMonitoringOption.No,
                    typologyStrategicComment = "typologyStrategicComment",
                    typologyPartnership = ContractingMonitoringOption.Yes,
                    typologyPartnershipComment = "typologyPartnershipComment",
                    addDates = listOf(ProjectContractingMonitoringAddDate(
                        projectId = projectId,
                        number = 1,
                        entryIntoForceDate = ZonedDateTime.parse("2022-07-22T10:00:00+02:00").toLocalDate(),
                        comment = "comment"
                    )),
                    fastTrackLumpSums = listOf(
                        ProjectLumpSum(
                            orderNr = orderNr,
                            programmeLumpSumId = lumpSumId,
                            period = 1,
                            lumpSumContributions = listOf(ProjectPartnerLumpSum(
                                partnerId = 52L,
                                amount = BigDecimal.valueOf(10041L, 2),
                            )),
                            fastTrack = true,
                            readyForPayment = true,
                            comment = "Test",
                            paymentEnabledDate = result.fastTrackLumpSums!!.first().paymentEnabledDate,
                            lastApprovedVersionBeforeReadyForPayment = "2.0",
                        ),
                    ),
                    dimensionCodes = emptyList(),
                )
            )
        val events = mutableListOf<AuditCandidateEvent>()
        verify(exactly = 2) { auditPublisher.publishEvent(capture(events)) }
        assertThat(events.first().auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.FTLS_READY_FOR_PAYMENT_CHANGE,
                project = AuditProject(id = "1", customIdentifier = "TSTCM", name = "TCM"),
                description = "Fast track lump sum 1 for project TSTCM set as YES for Ready for payment",
            )
        )
        assertThat(events.last().auditCandidate).isEqualTo(
            AuditCandidate(
                action = AuditAction.PROJECT_CONTRACT_MONITORING_CHANGED,
                project = AuditProject(id = "1", customIdentifier = "TSTCM", name = "TCM"),
                description = "Fields changed:\n" +
                    "startDate changed from 2022-06-02 to 2022-07-01,\n" +
                    "typologyProv94 changed from No to Partly,\n" +
                    "typologyProv95 changed from Partly to Yes,\n" +
                    "typologyStrategic changed from Yes to No,\n" +
                    "typologyPartnership changed from No to Yes,\n" +
                    "addSubContractDates changed from [\n" +
                    "  2022-06-02\n" +
                    "  2022-06-02\n" +
                    "] to [\n" +
                    "  2022-07-22\n" +
                    "]"
            )
        )
        assertThat(payments.captured).containsExactlyEntriesOf(
            mapOf(PaymentGroupingId(programmeFundId = 1L, orderNr = 1) to
                    PaymentToCreate(
                        2L,
                        listOf(PaymentPartnerToCreate(1L, null, BigDecimal.ONE)),
                        BigDecimal.ONE,
                        "TSTCM",
                        "TCM",
                        defaultPartnerContribution = BigDecimal.valueOf(85.35),
                        defaultOfWhichPublic = BigDecimal.valueOf(25.13),
                        defaultOfWhichAutoPublic = BigDecimal.valueOf(28.45),
                        defaultOfWhichPrivate = BigDecimal.valueOf(31.79),
                    )
            )
        )
        assertThat(payContribs.captured).containsExactly(
            ContributionMeta(
                projectId = projectId,
                partnerId = 52L,
                programmeLumpSumId = 2L,
                orderNr = 1,
                partnerContribution = BigDecimal.valueOf(8535L, 2),
                publicContribution = BigDecimal.valueOf(2513L, 2),
                automaticPublicContribution = BigDecimal.valueOf(2844L, 2),
                privateContribution = BigDecimal.valueOf(3179L, 2),
            )
        )
        // there can be rounding differences in "ofWhich" part
        with(payContribs.captured.first()) {
            assertThat(partnerContribution.minus(publicContribution).minus(automaticPublicContribution).minus(privateContribution).abs())
                .isLessThanOrEqualTo(BigDecimal.valueOf(3L, 2))
        }
    }

    @Test
    fun `add project management to NOT approved application`() {
        mockkObject(ContractingValidator.Companion)
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        every {
            ContractingValidator.validateProjectStatusForModification(projectSummary)
        } throws ContractingModificationDeniedException()

        assertThrows<ContractingModificationDeniedException> {
            updateContractingMonitoring.updateContractingMonitoring(projectId, monitoring)
        }
    }

    @Test
    fun `add project management with too many additional dates`() {
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        every {
            validator.validateMonitoringInput(monitoring)
        } throws ContractingModificationDeniedException()
        assertThrows<ContractingModificationDeniedException> {
            updateContractingMonitoring.updateContractingMonitoring(projectId, monitoring)
        }
    }

    @Test
    fun `set ready for payment to no for lump sum`() {
        every {
            projectPersistence.getProjectSummary(projectId)
        } returns projectSummary.copy(status = ApplicationStatus.CONTRACTED)
        val oldDate = ZonedDateTime.parse("2022-06-02T10:00:00+02:00").toLocalDate()
        every {
            contractingMonitoringPersistence.getContractingMonitoring(projectId)
        } returns monitoring.copy(
            startDate = oldDate,
            fastTrackLumpSums = lumpSums
        )
        val monitoringNew = monitoring.copy(
            fastTrackLumpSums = listOf(
                ProjectLumpSum(
                    orderNr = orderNr,
                    programmeLumpSumId = lumpSumId,
                    period = 1,
                    lumpSumContributions = listOf(),
                    fastTrack = true,
                    readyForPayment = false,
                    comment = null,
                    paymentEnabledDate = TIME,
                    lastApprovedVersionBeforeReadyForPayment = "v1.0"
                )
            )
        )
        every { contractingMonitoringPersistence.updateContractingMonitoring(monitoringNew) } returns monitoringNew
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version
        every { projectPersistence.getProject(projectId, version) } returns project
        every { projectLumpSumPersistence.getLumpSums(projectId, version) } returns lumpSums
        every { contractingMonitoringPersistence.existsSavedInstallment(projectId, lumpSumId, orderNr) } returns false

        every { projectLumpSumPersistence.updateLumpSums(any(), any()) } returns monitoringNew.fastTrackLumpSums!!
        every { paymentPersistence.getAmountPerPartnerByProjectIdAndLumpSumOrderNrIn(projectId, Sets.newSet(1))} returns
            listOf(paymentPerPartner)
        every { paymentPersistence.deleteFTLSByProjectIdAndOrderNrIn(projectId, Sets.newSet(1))} returns Unit
        every { getProjectBudget.getBudget(projectId, version) } returns emptyList()
        val slotDeleted = slot<Set<Int>>()
        every { paymentPersistence.deleteContributionsWhenReadyForPaymentReverted(projectId, capture(slotDeleted)) } answers { }

        val updatedMonitoring = updateContractingMonitoring.updateContractingMonitoring(projectId, monitoringNew)

        assertThat(updatedMonitoring.fastTrackLumpSums)
            .isEqualTo(
                    monitoringNew.fastTrackLumpSums
            )
        assertThat(slotDeleted.captured).containsExactly(1)
    }

    @Test
    fun `error on remove ready for payment for existing installments`() {
        every { projectPersistence.getProjectSummary(projectId) } returns projectSummary
        every { contractingMonitoringPersistence.getContractingMonitoring(projectId) } returns monitoring
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version
        every { projectLumpSumPersistence.getLumpSums(projectId, version) } returns lumpSums
        val monitoringNew = monitoring.copy(
            fastTrackLumpSums = listOf(
                ProjectLumpSum(
                    orderNr = orderNr,
                    programmeLumpSumId = lumpSumId,
                    period = 1,
                    fastTrack = true,
                    readyForPayment = false,
                    lastApprovedVersionBeforeReadyForPayment = version
                )
            )
        )
        every { contractingMonitoringPersistence.updateContractingMonitoring(monitoringNew) } returns monitoringNew
        every { contractingMonitoringPersistence.existsSavedInstallment(projectId, lumpSumId, orderNr) } returns true

        assertThrows<UpdateContractingMonitoringFTLSException> {
            updateContractingMonitoring.updateContractingMonitoring(projectId, monitoringNew)
        }

    }
}
