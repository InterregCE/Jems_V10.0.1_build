package io.cloudflight.jems.server.payments.repository.account.correction

import com.querydsl.core.Tuple
import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.dsl.BooleanOperation
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.entity.account.PaymentAccountCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.entity.account.PaymentAccountEntity
import io.cloudflight.jems.server.payments.entity.account.PaymentAccountPriorityAxisOverviewEntity
import io.cloudflight.jems.server.payments.entity.account.QPaymentAccountCorrectionExtensionEntity
import io.cloudflight.jems.server.payments.model.account.PaymentAccountAmountSummaryLineTmp
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionExtension
import io.cloudflight.jems.server.payments.model.account.PaymentAccountCorrectionLinkingUpdate
import io.cloudflight.jems.server.payments.model.account.PaymentAccountOverviewType.Correction
import io.cloudflight.jems.server.payments.model.account.PaymentAccountStatus
import io.cloudflight.jems.server.payments.model.regular.PaymentSearchRequestScoBasis
import io.cloudflight.jems.server.payments.repository.account.PaymentAccountRepository
import io.cloudflight.jems.server.payments.service.account.corrections.sumUpProperColumns
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import io.cloudflight.jems.server.programme.repository.priority.ProgrammePriorityRepository
import io.cloudflight.jems.server.project.entity.auditAndControl.AuditControlCorrectionEntity
import io.cloudflight.jems.server.project.entity.auditAndControl.QAuditControlCorrectionEntity
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.AuditControlCorrectionRepository
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.CorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.ProjectCorrectionFinancialDescription
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PaymentAccountCorrectionLinkingPersistenceProviderTest : UnitTest() {

    companion object {
        private const val PAYMENT_ACCOUNT_ID = 103L
        private const val CORRECTION_ID = 105L

        val paymentAccount = mockk<PaymentAccountEntity> {
            every { id } returns PAYMENT_ACCOUNT_ID
            every { status } returns PaymentAccountStatus.DRAFT
        }

        fun correctionExtensionEntity(paymentAccount: PaymentAccountEntity?) = PaymentAccountCorrectionExtensionEntity(
            correctionId = CORRECTION_ID,
            correction = mockk {
                every { id } returns CORRECTION_ID
                every { auditControl.status } returns AuditControlStatus.Closed
            },
            paymentAccount = paymentAccount,
            comment = "comm",
            fundAmount = BigDecimal.valueOf(100),
            correctedFundAmount = BigDecimal.valueOf(100),
            publicContribution = BigDecimal.valueOf(50),
            correctedPublicContribution = BigDecimal.valueOf(50),
            autoPublicContribution = BigDecimal.valueOf(20),
            correctedAutoPublicContribution = BigDecimal.valueOf(20),
            privateContribution = BigDecimal.valueOf(20),
            correctedPrivateContribution = BigDecimal.valueOf(20),
            finalScoBasis = PaymentSearchRequestScoBasis.DoesNotFallUnderArticle94Nor95
        )

        val correctionExtension = PaymentAccountCorrectionExtension(
            correctionId = CORRECTION_ID,
            paymentAccountId = PAYMENT_ACCOUNT_ID,
            paymentAccountStatus = PaymentAccountStatus.DRAFT,
            auditControlStatus = AuditControlStatus.Closed,
            comment = "comm",
            fundAmount = BigDecimal.valueOf(100),
            correctedFundAmount = BigDecimal.valueOf(100),
            publicContribution = BigDecimal.valueOf(50),
            correctedPublicContribution = BigDecimal.valueOf(50),
            autoPublicContribution = BigDecimal.valueOf(20),
            correctedAutoPublicContribution = BigDecimal.valueOf(20),
            privateContribution = BigDecimal.valueOf(20),
            correctedPrivateContribution = BigDecimal.valueOf(20),
        )

        private val financialDescription = ProjectCorrectionFinancialDescription(
            correctionId = CORRECTION_ID,
            deduction = true,
            fundAmount = BigDecimal.TEN,
            publicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ONE,
            privateContribution = BigDecimal.ZERO,
            infoSentBeneficiaryDate = null,
            infoSentBeneficiaryComment = "sample comment",
            correctionType = CorrectionType.Ref1Dot15,
            clericalTechnicalMistake = false,
            goldPlating = false,
            suspectedFraud = true,
            correctionComment = null
        )

        private val correctionEntity = mockk<AuditControlCorrectionEntity>()

        private val correctionExtensionEntity = PaymentAccountCorrectionExtensionEntity(
            correctionId = CORRECTION_ID,
            correction = correctionEntity,
            paymentAccount = null,
            fundAmount = BigDecimal.TEN,
            correctedFundAmount = BigDecimal.TEN,
            publicContribution = BigDecimal.ZERO,
            correctedPublicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ONE,
            correctedAutoPublicContribution = BigDecimal.ONE,
            privateContribution = BigDecimal.ZERO,
            correctedPrivateContribution = BigDecimal.ZERO,
            comment = null,
            finalScoBasis = null,
        )

        private val paymentAccountSummaryLine = PaymentAccountAmountSummaryLineTmp(
            priorityId = 123L,
            priorityAxis = "PrioAxis",
            fundAmount = BigDecimal.valueOf(2L),
            partnerContribution = BigDecimal.valueOf(21),
            ofWhichPublic = BigDecimal.valueOf(3),
            ofWhichAutoPublic = BigDecimal.valueOf(4)
        )

        private val priorityAxisEntity = mockk<ProgrammePriorityEntity> {
            every { id } returns 321L
            every { code } returns "PrioAxis"
        }

        private val paymentAccountSummary = PaymentAccountPriorityAxisOverviewEntity(
            id = 0L,
            paymentAccount = paymentAccount,
            priorityAxis = priorityAxisEntity,
            type = Correction,
            totalEligibleExpenditure = BigDecimal.valueOf(23),
            totalPublicContribution = BigDecimal.valueOf(9)
        )
    }

    @MockK
    lateinit var paymentAccountRepository: PaymentAccountRepository

    @MockK
    lateinit var correctionExtensionRepository: PaymentAccountCorrectionExtensionRepository

    @MockK
    lateinit var auditControlCorrectionRepository: AuditControlCorrectionRepository

    @MockK
    lateinit var programmePriorityRepository: ProgrammePriorityRepository

    @MockK
    lateinit var priorityAxisOverviewRepository: PaymentAccountPriorityAxisOverviewRepository

    @MockK
    lateinit var jpaQueryFactory: JPAQueryFactory

    @InjectMockKs
    lateinit var persistence: PaymentAccountCorrectionLinkingPersistenceProvider

    @BeforeEach
    fun setup() {
        clearMocks(
            paymentAccountRepository,
            correctionExtensionRepository,
            auditControlCorrectionRepository,
            programmePriorityRepository,
            priorityAxisOverviewRepository,
            jpaQueryFactory,
        )
    }

    @Test
    fun getCorrectionExtension() {
        every { correctionExtensionRepository.getById(CORRECTION_ID) } returns correctionExtensionEntity(paymentAccount)
        assertThat(persistence.getCorrectionExtension(CORRECTION_ID)).isEqualTo(correctionExtension)
        verify { correctionExtensionRepository.getById(CORRECTION_ID) }
    }

    @Test
    fun createCorrectionExtension() {
        every { auditControlCorrectionRepository.getById(CORRECTION_ID) } returns correctionEntity
        val slot = slot<PaymentAccountCorrectionExtensionEntity>()
        every { correctionExtensionRepository.save(capture(slot)) } returns correctionExtensionEntity

        persistence.createCorrectionExtension(financialDescription)
        assertThat(slot.captured).usingRecursiveComparison().isEqualTo(correctionExtensionEntity)
    }

    @Test
    fun selectCorrectionToPaymentAccount() {
        val correction = correctionExtensionEntity(null)
        every { paymentAccountRepository.getById(PAYMENT_ACCOUNT_ID) } returns paymentAccount
        every { correctionExtensionRepository.findAllById(setOf(CORRECTION_ID)) } returns listOf(correction)

        persistence.selectCorrectionToPaymentAccount(setOf(CORRECTION_ID), PAYMENT_ACCOUNT_ID)
        assertThat(correction.paymentAccount).isEqualTo(paymentAccount)
    }

    @Test
    fun deselectCorrectionToPaymentAccount() {
        val correction = correctionExtensionEntity(paymentAccount)
        every { paymentAccountRepository.getById(PAYMENT_ACCOUNT_ID) } returns paymentAccount
        every { correctionExtensionRepository.findAllById(setOf(CORRECTION_ID)) } returns listOf(correction)

        persistence.deselectCorrectionFromPaymentAccountAndResetFields(setOf(CORRECTION_ID))
        assertThat(correction.paymentAccount).isEqualTo(null)
    }

    @Test
    fun updateCorrectionLinkedToPaymentAccountCorrectedAmounts() {
        val correction = correctionExtensionEntity(paymentAccount)
        every { correctionExtensionRepository.getById(CORRECTION_ID) } returns correction

        val update = PaymentAccountCorrectionLinkingUpdate(
            correctedFundAmount = BigDecimal.valueOf(10),
            correctedPublicContribution = BigDecimal.valueOf(8),
            correctedAutoPublicContribution = BigDecimal.valueOf(7),
            correctedPrivateContribution = BigDecimal.valueOf(6),
            comment = "newComm"
        )
        persistence.updateCorrectionLinkedToPaymentAccountCorrectedAmounts(CORRECTION_ID, update)

        assertThat(correction.correctedFundAmount).isEqualTo(BigDecimal.valueOf(10L))
        assertThat(correction.correctedPublicContribution).isEqualTo(BigDecimal.valueOf(8))
        assertThat(correction.correctedAutoPublicContribution).isEqualTo(BigDecimal.valueOf(7))
        assertThat(correction.correctedPrivateContribution).isEqualTo(BigDecimal.valueOf(6))
        assertThat(correction.comment).isEqualTo("newComm")
    }

    @Test
    fun getCorrectionIdsAvailableForPaymentAccounts() {
        val query = mockk<JPAQuery<Long>>()
        val wherePredicate = slot<BooleanOperation>()
        val correctionEntity = QAuditControlCorrectionEntity.auditControlCorrectionEntity
        val extensionEntity = QPaymentAccountCorrectionExtensionEntity.paymentAccountCorrectionExtensionEntity
        every { jpaQueryFactory.select(correctionEntity.id) } returns query
        every { query.from(correctionEntity) } returns query
        every { query.leftJoin(extensionEntity) } returns query
        every { query.on(correctionEntity.id.eq(extensionEntity.correctionId)) } returns query
        every { query.where(capture(wherePredicate)) } returns query
        every { query.fetch() } returns listOf(123L, 234L, 345L)

        assertThat(persistence.getCorrectionIdsAvailableForPaymentAccounts(1L)).isEqualTo(setOf(123L, 234L, 345L))
        assertThat(wherePredicate.captured.toString()).isEqualTo(
            "auditControlCorrectionEntity.programmeFund.id = 1 " +
                    "&& auditControlCorrectionEntity.status = Closed " +
                    "&& paymentAccountCorrectionExtensionEntity.paymentAccount is null"
        )
    }

    @Test
    fun calculateOverviewForDraftPaymentAccount() {
        val query = mockk<JPAQuery<Tuple>>()
        every { jpaQueryFactory.select(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns query
        every { query.from(any()) } returns query
        every { query.leftJoin(any<EntityPath<Any>>()) } returns query
        every { query.on(any()) } returns query
        every { query.where(any()) } returns query
        every { query.groupBy(any()) } returns query
        val tuple = mockk<Tuple> {
            every { get(0, Long::class.java) } returns 123L
            every { get(1, String::class.java) } returns "PrioAxis"
            every { get(2, BigDecimal::class.java) } returns BigDecimal.valueOf(2)
            every { get(3, BigDecimal::class.java) } returns BigDecimal.valueOf(3)
            every { get(4, BigDecimal::class.java) } returns BigDecimal.valueOf(4)
            every { get(5, BigDecimal::class.java) } returns BigDecimal.valueOf(5)
            every { get(6, BigDecimal::class.java) } returns BigDecimal.valueOf(6)
            every { get(7, BigDecimal::class.java) } returns BigDecimal.valueOf(7)
            every { get(8, BigDecimal::class.java) } returns BigDecimal.valueOf(8)
        }
        every { query.fetch() } returns listOf(tuple)

        assertThat(persistence.calculateOverviewForDraftPaymentAccount(PAYMENT_ACCOUNT_ID)).isEqualTo(
            mapOf(123L to paymentAccountSummaryLine)
        )
    }

    @Test
    fun saveTotalsWhenFinishingPaymentAccount() {
        every { programmePriorityRepository.findAllById(any()) } returns setOf(priorityAxisEntity)
        every { paymentAccountRepository.getById(PAYMENT_ACCOUNT_ID) } returns paymentAccount

        every { priorityAxisOverviewRepository.deleteAllByPaymentAccountId(PAYMENT_ACCOUNT_ID) } just runs
        every { priorityAxisOverviewRepository.flush() } just runs
        val slot = slot<Iterable<PaymentAccountPriorityAxisOverviewEntity>>()
        every { priorityAxisOverviewRepository.saveAll(capture(slot)) } returns listOf(paymentAccountSummary)

        val totals = mapOf<Long?, PaymentAccountAmountSummaryLineTmp>(priorityAxisEntity.id to paymentAccountSummaryLine).sumUpProperColumns()
        persistence.saveTotalsWhenFinishingPaymentAccount(PAYMENT_ACCOUNT_ID, totals)

        assertThat(slot.captured).usingRecursiveComparison().isEqualTo(listOf(paymentAccountSummary))
    }

    @Test
    fun getTotalsForFinishedPaymentAccount() {
        every { priorityAxisOverviewRepository.getAllByPaymentAccountIdAndType(PAYMENT_ACCOUNT_ID, Correction) } returns listOf(paymentAccountSummary)

        assertThat(persistence.getTotalsForFinishedPaymentAccount(PAYMENT_ACCOUNT_ID)).isEqualTo(listOf(paymentAccountSummary).toModel())
    }

}
