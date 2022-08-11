package io.cloudflight.jems.server.project.service.report.partner.procurement.updateProjectPartnerReportProcurement

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

internal class UpdateProjectPartnerReportProcurementTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 5922L
        private val NEXT_WEEK = LocalDate.now().plusWeeks(1)
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportProcurementPersistence: ProjectReportProcurementPersistence

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var interactor: UpdateProjectPartnerReportProcurement

    @BeforeEach
    fun setup() {
        clearMocks(reportProcurementPersistence)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { true }) } returns Unit
        every { generalValidator.maxLength(any<String>(), any(), any()) } returns emptyMap()
        every { generalValidator.notBlank(any<String>(), any()) } returns emptyMap()
        every { generalValidator.numberBetween(any<BigDecimal>(), any(), any(), any()) } returns emptyMap()
        every { generalValidator.onlyValidCurrencies(any(), any()) } returns emptyMap()
    }

    @Test
    fun `update - successful`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns 18L
        every { report.status } returns ReportStatus.Draft
        every { report.identification.currency } returns "PLN"

        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 18L) } returns report
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = 18L) } returns setOf(17L)
        every { reportProcurementPersistence.getProcurementContractNamesForReportIds(setOf(17L, 18L)) } returns
            setOf(Pair(45L, "unique 1"), Pair(46L, "unique 2"))

        val mockedResult = mockk<ProjectPartnerReportProcurement>()
        val updateSlot = slot<ProjectPartnerReportProcurementChange>()
        every { reportProcurementPersistence.updatePartnerReportProcurement(PARTNER_ID, reportId = 18L, capture(updateSlot)) } returns mockedResult

        val change = ProjectPartnerReportProcurementChange(
            id = 47L,
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

        assertThat(interactor.update(PARTNER_ID, reportId = 18L, change)).isEqualTo(mockedResult)
        assertThat(updateSlot.captured).isEqualTo(change)
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
        every { generalValidator.notBlank(any<String>(), any()) } answers {
            mapOf(secondArg<String>() to I18nMessage(i18nKey = "${firstArg<String>()}---not blank"))
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
            id = 51L,
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

        assertThrows<AppInputValidationException> { interactor.update(PARTNER_ID, reportId = 0L, change) }
        assertThat(validationSlot).containsExactly(
            mapOf("contractName" to I18nMessage("contractName NEW---not blank")),
            mapOf("contractName" to I18nMessage("contractName NEW---50")),
            mapOf("referenceNumber" to I18nMessage("referenceNumber NEW---30")),
            mapOf("contractType" to I18nMessage("contractType NEW---30")),
            mapOf("supplierName" to I18nMessage("supplierName NEW---30")),
            mapOf("vatNumber" to I18nMessage("vatNumber NEW---not blank")),
            mapOf("vatNumber" to I18nMessage("vatNumber NEW---30")),
            mapOf("comment" to I18nMessage("comment NEW---2000")),
            mapOf("contractAmount" to I18nMessage("0---0-999999999.99")),
            mapOf("currencyCode" to I18nMessage("HUF---not blank")),
            mapOf("currencyCode" to I18nMessage("HUF")),
        )
    }

    @Test
    fun `update - wrong report status`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns 40L
        every { report.status } returns ReportStatus.Submitted

        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 40L) } returns report

        val change = ProjectPartnerReportProcurementChange(
            id = 66358L,
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

        assertThrows<ReportAlreadyClosed> { interactor.update(PARTNER_ID, reportId = 40L, change) }
    }

    @Test
    fun `update - invalid currencies`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns 45L
        every { report.status } returns ReportStatus.Draft
        every { report.identification.currency } returns "EUR"

        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 45L) } returns report
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = 45L) } returns emptySet()
        every { reportProcurementPersistence.getProcurementContractNamesForReportIds(setOf(45L)) } returns emptySet()

        val change = ProjectPartnerReportProcurementChange(
            id = 47L,
            contractName = "",
            referenceNumber = "",
            contractDate = NEXT_WEEK,
            contractType = "",
            contractAmount = BigDecimal.ZERO,
            currencyCode = "HUF",
            supplierName = "",
            vatNumber = "",
            comment = "",
        )

        assertThrows<InvalidCurrency> { interactor.update(PARTNER_ID, reportId = 45L, change) }
    }

    @Test
    fun `update - contract name is not unique`() {
        val report = mockk<ProjectPartnerReport>()
        every { report.id } returns 48L
        every { report.status } returns ReportStatus.Draft
        every { report.identification.currency } returns "GBP"

        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = 48L) } returns report
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = 48L) } returns setOf(47L)
        every { reportProcurementPersistence.getProcurementContractNamesForReportIds(setOf(47L, 48L)) } returns
            setOf(Pair(147L, "name 147"), Pair(148L, "name 148"))

        val change = ProjectPartnerReportProcurementChange(
            id = 148L,
            contractName = "name 147",
            referenceNumber = "",
            contractDate = NEXT_WEEK,
            contractType = "",
            contractAmount = BigDecimal.ZERO,
            currencyCode = "PLN",
            supplierName = "",
            vatNumber = "",
            comment = "",
        )

        assertThrows<ContractNameIsNotUnique> { interactor.update(PARTNER_ID, reportId = 48L, change) }
    }

}
