package io.cloudflight.jems.server.project.repository.report.expenditure

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.entity.report.expenditure.PartnerReportExpenditureCostTranslEntity
import io.cloudflight.jems.server.project.repository.report.ProjectPartnerReportRepository
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class ProjectReportExpenditurePersistenceProviderTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 380L
        private const val PROCUREMENT_ID = 18L
        private const val INVESTMENT_ID = 28L

        private const val EXPENDITURE_TO_UPDATE = 40L
        private const val EXPENDITURE_TO_DELETE = 41L
        private const val EXPENDITURE_TO_STAY = 42L

        private val YESTERDAY = LocalDate.now().minusDays(1)
        private val TOMORROW = LocalDate.now().plusDays(1)

        private fun dummyExpenditure(id: Long, report: ProjectPartnerReportEntity) = PartnerReportExpenditureCostEntity(
            id = id,
            partnerReport = report,
            costCategory = "cc",
            investmentId = INVESTMENT_ID,
            procurementId = PROCUREMENT_ID,
            internalReferenceNumber = "irn",
            invoiceNumber = "invoice",
            invoiceDate = YESTERDAY,
            dateOfPayment = TOMORROW,
            totalValueInvoice = BigDecimal.ONE,
            vat = BigDecimal.ZERO,
            declaredAmount = BigDecimal.TEN,
            translatedValues = mutableSetOf(),
        ).apply {
            translatedValues.add(
                PartnerReportExpenditureCostTranslEntity(
                    TranslationId(this, SystemLanguage.EN),
                    comment = "comment EN",
                    description = "desc EN",
                )
            )
        }

        private fun dummyExpectedExpenditure(id: Long) = ProjectPartnerReportExpenditureCost(
            id = id,
            costCategory = "cc",
            investmentId = INVESTMENT_ID,
            contractId = PROCUREMENT_ID,
            internalReferenceNumber = "irn",
            invoiceNumber = "invoice",
            invoiceDate = YESTERDAY,
            dateOfPayment = TOMORROW,
            description = setOf(InputTranslation(SystemLanguage.EN, "desc EN")),
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
            totalValueInvoice = BigDecimal.ONE,
            vat = BigDecimal.ZERO,
            declaredAmount = BigDecimal.TEN,
        )

        private fun dummyExpectedExpenditureNew(id: Long) = ProjectPartnerReportExpenditureCost(
            id = id,
            costCategory = "cc NEW",
            investmentId = INVESTMENT_ID + 10,
            contractId = PROCUREMENT_ID + 10,
            internalReferenceNumber = "irn NEW",
            invoiceNumber = "invoice NEW",
            invoiceDate = YESTERDAY.minusDays(1),
            dateOfPayment = TOMORROW.plusDays(1),
            description = setOf(InputTranslation(SystemLanguage.EN, "desc EN NEW")),
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN NEW")),
            totalValueInvoice = BigDecimal.ZERO,
            vat = BigDecimal.TEN,
            declaredAmount = BigDecimal.ONE,
        )
    }

    @MockK
    lateinit var reportRepository: ProjectPartnerReportRepository

    @MockK
    lateinit var reportExpenditureRepository: ProjectPartnerReportExpenditureRepository

    @InjectMockKs
    lateinit var persistence: ProjectReportExpenditurePersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(reportExpenditureRepository)
    }

    @Test
    fun getPartnerReportExpenditureCosts() {
        val report = mockk<ProjectPartnerReportEntity>()
        val expenditure = dummyExpenditure(id = 14L, report)
        every { reportExpenditureRepository.findTop150ByPartnerReportIdAndPartnerReportPartnerIdOrderById(
            reportId = 44L,
            partnerId = PARTNER_ID,
        ) } returns mutableListOf(expenditure)

        assertThat(persistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId = 44L))
            .containsExactly(dummyExpectedExpenditure(id = 14L))
    }

    @Test
    fun updatePartnerReportExpenditureCosts() {
        val report = mockk<ProjectPartnerReportEntity>()
        every { reportRepository.findByIdAndPartnerId(id = 58L, PARTNER_ID) } returns report
        every { reportExpenditureRepository.findExistingExpenditureIdsFor(report) } returns
            setOf(EXPENDITURE_TO_UPDATE, EXPENDITURE_TO_DELETE, EXPENDITURE_TO_STAY)

        val slotDeletedIds = slot<Set<Long>>()
        every { reportExpenditureRepository.deleteAllById(capture(slotDeletedIds)) } answers { }

        val slotSavedEntities = slot<Iterable<PartnerReportExpenditureCostEntity>>()
        every { reportExpenditureRepository.saveAll(capture(slotSavedEntities)) } returnsArgument 0

        val changes = listOf(
            dummyExpectedExpenditureNew(EXPENDITURE_TO_UPDATE),
            dummyExpectedExpenditure(EXPENDITURE_TO_STAY),
        )

        assertThat(persistence.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId = 58L, changes))
            .containsExactlyInAnyOrder(
                dummyExpectedExpenditureNew(EXPENDITURE_TO_UPDATE),
                dummyExpectedExpenditure(EXPENDITURE_TO_STAY),
            )

        assertThat(slotDeletedIds.captured).containsExactly(EXPENDITURE_TO_DELETE)
        assertThat(slotSavedEntities.captured).hasSize(2)

        with(slotSavedEntities.captured.first { it.id == EXPENDITURE_TO_UPDATE }) {
            assertThat(costCategory).isEqualTo("cc NEW")
            assertThat(investmentId).isEqualTo(INVESTMENT_ID + 10)
            assertThat(procurementId).isEqualTo(PROCUREMENT_ID + 10)
            assertThat(internalReferenceNumber).isEqualTo("irn NEW")
            assertThat(invoiceNumber).isEqualTo("invoice NEW")
            assertThat(invoiceDate).isEqualTo(YESTERDAY.minusDays(1))
            assertThat(dateOfPayment).isEqualTo(TOMORROW.plusDays(1))
            assertThat(translatedValues.first().comment).isEqualTo("comment EN NEW")
            assertThat(translatedValues.first().description).isEqualTo("desc EN NEW")
            assertThat(totalValueInvoice).isEqualByComparingTo(BigDecimal.ZERO)
            assertThat(vat).isEqualByComparingTo(BigDecimal.TEN)
            assertThat(declaredAmount).isEqualByComparingTo(BigDecimal.ONE)
        }
        with(slotSavedEntities.captured.first { it.id == EXPENDITURE_TO_STAY }) {
            assertThat(costCategory).isEqualTo("cc")
            assertThat(investmentId).isEqualTo(INVESTMENT_ID)
            assertThat(procurementId).isEqualTo(PROCUREMENT_ID)
            assertThat(internalReferenceNumber).isEqualTo("irn")
            assertThat(invoiceNumber).isEqualTo("invoice")
            assertThat(invoiceDate).isEqualTo(YESTERDAY)
            assertThat(dateOfPayment).isEqualTo(TOMORROW)
            assertThat(translatedValues.first().comment).isEqualTo("comment EN")
            assertThat(translatedValues.first().description).isEqualTo("desc EN")
            assertThat(totalValueInvoice).isEqualByComparingTo(BigDecimal.ONE)
            assertThat(vat).isEqualByComparingTo(BigDecimal.ZERO)
            assertThat(declaredAmount).isEqualByComparingTo(BigDecimal.TEN)
        }
    }

}
