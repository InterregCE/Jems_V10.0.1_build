package io.cloudflight.jems.server.project.service.report.partner.procurement.createProjectPartnerReportProcurement

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementChange
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate

internal class CreateProjectPartnerReportProcurementTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 5833L
        private val NEXT_WEEK = LocalDate.now().plusWeeks(1)
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportProcurementPersistence: ProjectReportProcurementPersistence

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var interactor: CreateProjectPartnerReportProcurement

    @BeforeEach
    fun setup() {
        clearMocks(reportProcurementPersistence)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { true }) } returns Unit
        every { generalValidator.maxLength(any<String>(), any(), any()) } returns emptyMap()
        every { generalValidator.numberBetween(any<BigDecimal>(), any(), any(), any()) } returns emptyMap()
        every { generalValidator.onlyValidCurrencies(any(), any()) } returns emptyMap()
    }

    @Test
    fun `create - successful`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns 28L
        every { report.status } returns ReportStatus.Draft
        every { report.identification.currency } returns "PLN"

        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 28L) } returns report
        every { reportProcurementPersistence.countProcurementsForPartner(PARTNER_ID) } returns 10L
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = 28L) } returns setOf(27L)
        every { reportProcurementPersistence.getProcurementContractNamesForReportIds(setOf(27L, 28L)) } returns
            setOf(Pair(45L, "unique 1"), Pair(46L, "unique 2"))

        val mockedResult = mockk<ProjectPartnerReportProcurement>()
        val createSlot = slot<ProjectPartnerReportProcurementChange>()
        every { reportProcurementPersistence.createPartnerReportProcurement(PARTNER_ID, reportId = 28L, capture(createSlot)) } returns mockedResult

        val change = ProjectPartnerReportProcurementChange(
            id = 0L,
            contractName = "unique 3",
            referenceNumber = "referenceNumber NEW",
            contractDate = NEXT_WEEK,
            contractType = "contractType NEW",
            contractAmount = BigDecimal.ZERO,
            currencyCode = "HUF",
            supplierName = "supplierName NEW",
            vatNumber = "vatNumber NEW",
            comment = "comment NEW",
        )

        assertThat(interactor.create(PARTNER_ID, reportId = 28L, change)).isEqualTo(mockedResult)
        assertThat(createSlot.captured).isEqualTo(change)
    }

    @Test
    fun `update - test input field validations`() {
        val validationSlot = mutableListOf<Map<String, I18nMessage>?>()
        every { generalValidator.throwIfAnyIsInvalid(*varargAllNullable { validationSlot.add(it) }) } throws
            AppInputValidationException(emptyMap())

        every { generalValidator.maxLength(any<String>(), any(), any()) } answers {
            mapOf(thirdArg<String>()
                to I18nMessage(i18nKey = "${firstArg<String>()}---${secondArg<Int>()}")
            )
        }
        every { generalValidator.numberBetween(any<BigDecimal>(), any(), any(), any()) } answers {
            mapOf(lastArg<String>()
                to I18nMessage(i18nKey = "${firstArg<BigDecimal>()}---${secondArg<BigDecimal>()}-${thirdArg<BigDecimal>()}")
            )
        }
        every { generalValidator.onlyValidCurrencies(any(), any()) } answers {
            mapOf(secondArg<String>()
                to I18nMessage(i18nKey = firstArg<Set<String>>().joinToString(","))
            )
        }

        val change = ProjectPartnerReportProcurementChange(
            id = 0L,
            contractName = "contractName NEW",
            referenceNumber = "referenceNumber NEW",
            contractDate = NEXT_WEEK,
            contractType = "contractType NEW",
            contractAmount = BigDecimal.ZERO,
            currencyCode = "HUF",
            supplierName = "supplierName NEW",
            vatNumber = "vatNumber NEW",
            comment = "comment NEW",
        )

        assertThrows<AppInputValidationException> { interactor.create(PARTNER_ID, reportId = 0L, change) }
        assertThat(validationSlot).containsExactly(
            mapOf("contractName" to I18nMessage("contractName NEW---50")),
            mapOf("referenceNumber" to I18nMessage("referenceNumber NEW---30")),
            mapOf("contractType" to I18nMessage("contractType NEW---30")),
            mapOf("supplierName" to I18nMessage("supplierName NEW---30")),
            mapOf("vatNumber" to I18nMessage("vatNumber NEW---30")),
            mapOf("comment" to I18nMessage("comment NEW---2000")),
            mapOf("contractAmount" to I18nMessage("0---0-999999999.99")),
            mapOf("currencyCode" to I18nMessage("HUF")),
        )
    }

    @Test
    fun `create - wrong report status`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns 80L
        every { report.status } returns ReportStatus.Submitted

        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 80L) } returns report

        val change = ProjectPartnerReportProcurementChange(
            id = 0,
            contractName = "",
            referenceNumber = "",
            contractDate = NEXT_WEEK,
            contractType = "",
            contractAmount = BigDecimal.ZERO,
            currencyCode = "",
            supplierName = "",
            vatNumber = "",
            comment = "",
        )

        assertThrows<ReportAlreadyClosed> { interactor.create(PARTNER_ID, reportId = 80L, change) }
    }

    @Test
    fun `create - invalid currencies`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns 85L
        every { report.status } returns ReportStatus.Draft
        every { report.identification.currency } returns "EUR"

        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 85L) } returns report
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = 85L) } returns emptySet()
        every { reportProcurementPersistence.getProcurementContractNamesForReportIds(setOf(85L)) } returns emptySet()

        val change = ProjectPartnerReportProcurementChange(
            id = 0L,
            contractName = "",
            referenceNumber = "",
            contractDate = NEXT_WEEK,
            contractType = "",
            contractAmount = BigDecimal.ZERO,
            currencyCode = "GBP",
            supplierName = "",
            vatNumber = "",
            comment = "",
        )

        assertThrows<InvalidCurrency> { interactor.create(PARTNER_ID, reportId = 85L, change) }
    }

    @Test
    fun `update - reached max amount of procurements`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns 86L
        every { report.status } returns ReportStatus.Draft
        every { report.identification.currency } returns "CZK"

        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 86L) } returns report
        every { reportProcurementPersistence.countProcurementsForPartner(PARTNER_ID) } returns 50L
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = 86L) } returns emptySet()
        every { reportProcurementPersistence.getProcurementContractNamesForReportIds(setOf(86L)) } returns emptySet()

        val change = ProjectPartnerReportProcurementChange(
            id = 0L,
            contractName = "",
            referenceNumber = "",
            contractDate = NEXT_WEEK,
            contractType = "",
            contractAmount = BigDecimal.ZERO,
            currencyCode = "",
            supplierName = "",
            vatNumber = "",
            comment = "",
        )

        assertThrows<MaxAmountOfProcurementsReachedException> { interactor.create(PARTNER_ID, reportId = 86L, change) }
    }

    @Test
    fun `update - contract name is not unique`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns 88L
        every { report.status } returns ReportStatus.Draft
        every { report.identification.currency } returns "GBP"

        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 88L) } returns report
        every { reportProcurementPersistence.countProcurementsForPartner(PARTNER_ID) } returns 10L
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = 88L) } returns setOf(87L)
        every { reportProcurementPersistence.getProcurementContractNamesForReportIds(setOf(87L, 88L)) } returns
            setOf(Pair(137L, "name 137"), Pair(138L, "name 138"))

        val change = ProjectPartnerReportProcurementChange(
            id = 0L,
            contractName = "name 137",
            referenceNumber = "",
            contractDate = NEXT_WEEK,
            contractType = "",
            contractAmount = BigDecimal.ZERO,
            currencyCode = "GBP",
            supplierName = "",
            vatNumber = "",
            comment = "",
        )

        assertThrows<ContractNameIsNotUnique> { interactor.create(PARTNER_ID, reportId = 88L, change) }
    }

}
