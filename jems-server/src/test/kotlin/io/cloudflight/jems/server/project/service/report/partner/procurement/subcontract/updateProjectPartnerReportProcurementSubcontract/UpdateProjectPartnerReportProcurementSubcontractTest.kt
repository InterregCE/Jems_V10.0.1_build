package io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.updateProjectPartnerReportProcurementSubcontract

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontract
import io.cloudflight.jems.server.project.service.report.model.procurement.subcontract.ProjectPartnerReportProcurementSubcontractChange
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.ProjectReportProcurementSubcontractPersistence
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

internal class UpdateProjectPartnerReportProcurementSubcontractTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 5922L
        private val YEARS_AGO_40 = LocalDate.now().minusYears(40)

        private val changeSubcontract1 = ProjectPartnerReportProcurementSubcontractChange(
            id = 0L,
            contractName = "contractName NEW",
            referenceNumber = "referenceNumber NEW",
            contractDate = YEARS_AGO_40.minusMonths(1),
            contractAmount = BigDecimal.TEN,
            currencyCode = "HUF",
            supplierName = "supplierName NEW",
            vatNumber = "vatNumber NEW",
        )

        private val subcontract1 = ProjectPartnerReportProcurementSubcontract(
            id = 100L,
            reportId = 218L,
            createdInThisReport = false,
            contractName = "contractName 100",
            referenceNumber = "referenceNumber 100",
            contractDate = YEARS_AGO_40,
            contractAmount = BigDecimal.ONE,
            currencyCode = "GBP",
            supplierName = "supplierName 100",
            vatNumber = "vatNumber 100",
        )

        private val subcontract2 = ProjectPartnerReportProcurementSubcontract(
            id = 101L,
            reportId = 299L,
            createdInThisReport = false,
            contractName = "contractName 101",
            referenceNumber = "referenceNumber 101",
            contractDate = YEARS_AGO_40,
            contractAmount = BigDecimal.ONE,
            currencyCode = "TRY",
            supplierName = "supplierName 101",
            vatNumber = "vatNumber 101",
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportProcurementPersistence: ProjectReportProcurementPersistence

    @MockK
    lateinit var reportProcurementSubcontractPersistence: ProjectReportProcurementSubcontractPersistence

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var interactor: UpdateProjectPartnerReportProcurementSubcontract

    @BeforeEach
    fun setup() {
        clearMocks(reportPersistence)
        clearMocks(reportProcurementPersistence)
        clearMocks(reportProcurementSubcontractPersistence)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), any(), any()) } returns emptyMap()
        every { generalValidator.numberBetween(any<BigDecimal>(), any(), any(), any()) } returns emptyMap()
        every { generalValidator.onlyValidCurrencies(any<Set<String>>(), any()) } returns emptyMap()
        every { generalValidator.notBlank(any<String>(), any()) } returns emptyMap()
    }

    @Test
    fun `update - success`() {
        val procurementId = 498L
        val reportId = 299L
        mockProcurement(id = procurementId)
        every { reportPersistence.exists(PARTNER_ID, reportId = reportId) } returns true

        every { reportProcurementSubcontractPersistence.countSubcontractorsCreatedBefore(procurementId, reportId = reportId) } returns 0L

        val slotChanges = slot<List<ProjectPartnerReportProcurementSubcontractChange>>()
        every { reportProcurementSubcontractPersistence
            .updateSubcontract(PARTNER_ID, reportId, procurementId, capture(slotChanges))
        } returns listOf(subcontract1, subcontract2)

        val subcontracts = listOf(changeSubcontract1)
        assertThat(interactor.update(PARTNER_ID, reportId, procurementId, subcontracts)).containsExactly(
            subcontract1.copy(createdInThisReport = false),
            subcontract2.copy(createdInThisReport = true),
        )
        assertThat(slotChanges.captured).containsExactly(changeSubcontract1)
    }

    private fun mockProcurement(id: Long) {
        val procurement = mockk<ProjectPartnerReportProcurement>()
        every { procurement.id } returns id
        every { reportProcurementPersistence.getById(PARTNER_ID, procurementId = id) } returns procurement
    }

    @Test
    fun `update - report does not exist`() {
        val procurementId = 258L
        val reportId = -1L
        mockProcurement(id = procurementId)
        every { reportPersistence.exists(PARTNER_ID, reportId = reportId) } returns false

        assertThrows<ReportNotFound> { interactor.update(PARTNER_ID, reportId, procurementId, listOf(changeSubcontract1)) }
    }

    @Test
    fun `update - max amount reached`() {
        val procurementId = 690L
        val reportId = 255L
        mockProcurement(id = procurementId)
        every { reportPersistence.exists(PARTNER_ID, reportId = reportId) } returns true

        every { reportProcurementSubcontractPersistence.countSubcontractorsCreatedBefore(procurementId, reportId = reportId) } returns 50L

        val subcontracts = listOf(changeSubcontract1)
        assertThrows<MaxAmountOfSubcontractorsReachedException> {
            interactor.update(PARTNER_ID, reportId, procurementId, subcontracts)
        }
    }

    @Test
    fun `update - test input validations`() {
        val validationSlot = mutableListOf<Map<String, I18nMessage>?>()
        every { generalValidator.throwIfAnyIsInvalid(*varargAllNullable { validationSlot.add(it) }) } throws
            AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), any(), any()) } answers {
            mapOf(thirdArg<String>() to I18nMessage(i18nKey = "${firstArg<String>()}---maxLength-${secondArg<Int>()}"))
        }
        every { generalValidator.numberBetween(any<BigDecimal>(), any(), any(), any()) } answers {
            mapOf(lastArg<String>() to I18nMessage(
                i18nKey = "${firstArg<BigDecimal>()}---numberBetween-${secondArg<BigDecimal>()}-and-${thirdArg<BigDecimal>()}"
            ))
        }
        every { generalValidator.onlyValidCurrencies(any<Set<String>>(), any()) } answers {
            mapOf(lastArg<String>() to I18nMessage(i18nKey = "${firstArg<Set<String>>()}---are-onlyValidCurrencies"))
        }
        every { generalValidator.notBlank(any<String>(), any()) } answers {
            mapOf(secondArg<String>() to I18nMessage(i18nKey = "${firstArg<String>()}---notBlank"))
        }

        val procurementId = 185L
        val reportId = 902L
        mockProcurement(id = procurementId)
        every { reportPersistence.exists(PARTNER_ID, reportId = reportId) } returns true

        every { reportProcurementSubcontractPersistence.countSubcontractorsCreatedBefore(procurementId, reportId = reportId) } returns 0L

        every { reportProcurementSubcontractPersistence
            .updateSubcontract(PARTNER_ID, reportId, procurementId, any())
        } returns listOf(subcontract1, subcontract2)

        val owners = listOf(changeSubcontract1)
        assertThrows<AppInputValidationException> { interactor.update(PARTNER_ID, reportId, procurementId, owners) }
        assertThat(validationSlot).containsExactly(
            mapOf("contractName[0]" to I18nMessage("contractName NEW---maxLength-50")),
            mapOf("referenceNumber[0]" to I18nMessage("referenceNumber NEW---maxLength-30")),
            mapOf("contractAmount[0]" to I18nMessage("10---numberBetween-0-and-999999999.99")),
            mapOf("currencyCode" to I18nMessage("[HUF]---are-onlyValidCurrencies")),
            mapOf("supplierName[0]" to I18nMessage("supplierName NEW---maxLength-50")),
            mapOf("vatNumber[0]" to I18nMessage("vatNumber NEW---maxLength-30")),
            mapOf("vatNumber[0]" to I18nMessage("vatNumber NEW---notBlank")),
        )
    }

}
