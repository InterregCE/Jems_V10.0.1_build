package io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.updateProjectPartnerReportProcurement

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialChange
import io.cloudflight.jems.server.project.service.report.model.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.ProjectReportProcurementBeneficialPersistence
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
import java.time.LocalDate

internal class UpdateProjectPartnerReportProcurementBeneficialTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 5922L
        private val YEARS_AGO_40 = LocalDate.now().minusYears(40)

        private val changeOwner1 = ProjectPartnerReportProcurementBeneficialChange(
            id = 0L,
            firstName = "firstName NEW",
            lastName = "lastName NEW",
            birth = YEARS_AGO_40.plusMonths(1),
            vatNumber = "vatNumber NEW",
        )

        private val beneficialOwner1 = ProjectPartnerReportProcurementBeneficialOwner(
            id = 100L,
            reportId = 218L,
            createdInThisReport = false,
            firstName = "firstName 100",
            lastName = "lastName 100",
            birth = YEARS_AGO_40,
            vatNumber = "vatNumber 100",
        )

        private val beneficialOwner2 = ProjectPartnerReportProcurementBeneficialOwner(
            id = 101L,
            reportId = 299L,
            createdInThisReport = false,
            firstName = "firstName 101",
            lastName = "lastName 101",
            birth = YEARS_AGO_40,
            vatNumber = "vatNumber 101",
        )
    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportProcurementPersistence: ProjectReportProcurementPersistence

    @MockK
    lateinit var reportProcurementBeneficialPersistence: ProjectReportProcurementBeneficialPersistence

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var interactor: UpdateProjectPartnerReportProcurementBeneficial

    @BeforeEach
    fun setup() {
        clearMocks(reportPersistence)
        clearMocks(reportProcurementPersistence)
        clearMocks(reportProcurementBeneficialPersistence)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), any(), any()) } returns emptyMap()
        every { generalValidator.notBlank(any<String>(), any()) } returns emptyMap()
    }

    @Test
    fun `update - success`() {
        val procurementId = 498L
        val reportId = 299L
        mockProcurement(id = procurementId)
        every { reportPersistence.exists(PARTNER_ID, reportId = reportId) } returns true

        every { reportProcurementBeneficialPersistence.countBeneficialOwnersCreatedBefore(procurementId, reportId = reportId) } returns 0L

        val slotChanges = slot<List<ProjectPartnerReportProcurementBeneficialChange>>()
        every { reportProcurementBeneficialPersistence
            .updateBeneficialOwners(PARTNER_ID, reportId, procurementId, capture(slotChanges))
        } returns listOf(beneficialOwner1, beneficialOwner2)

        val owners = listOf(changeOwner1)
        assertThat(interactor.update(PARTNER_ID, reportId, procurementId, owners)).containsExactly(
            beneficialOwner1.copy(createdInThisReport = false),
            beneficialOwner2.copy(createdInThisReport = true),
        )
        assertThat(slotChanges.captured).containsExactly(changeOwner1)
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

        val owners = listOf(changeOwner1)
        assertThrows<ReportNotFound> { interactor.update(PARTNER_ID, reportId, procurementId, owners) }
    }

    @Test
    fun `update - max amount reached`() {
        val procurementId = 690L
        val reportId = 255L
        mockProcurement(id = procurementId)
        every { reportPersistence.exists(PARTNER_ID, reportId = reportId) } returns true

        every { reportProcurementBeneficialPersistence.countBeneficialOwnersCreatedBefore(procurementId, reportId = reportId) } returns 10L

        val owners = listOf(changeOwner1)
        assertThrows<MaxAmountOfBeneficialReachedException> { interactor.update(PARTNER_ID, reportId, procurementId, owners) }
    }

    @Test
    fun `update - test input validations`() {
        val validationSlot = mutableListOf<Map<String, I18nMessage>?>()
        every { generalValidator.throwIfAnyIsInvalid(*varargAllNullable { validationSlot.add(it) }) } throws
            AppInputValidationException(emptyMap())
        every { generalValidator.maxLength(any<String>(), any(), any()) } answers {
            mapOf(thirdArg<String>() to I18nMessage(i18nKey = "${firstArg<String>()}---maxLength-${secondArg<Int>()}"))
        }
        every { generalValidator.notBlank(any<String>(), any()) } answers {
            mapOf(secondArg<String>() to I18nMessage(i18nKey = "${firstArg<String>()}---notBlank"))
        }

        val procurementId = 185L
        val reportId = 902L
        mockProcurement(id = procurementId)
        every { reportPersistence.exists(PARTNER_ID, reportId = reportId) } returns true

        every { reportProcurementBeneficialPersistence.countBeneficialOwnersCreatedBefore(procurementId, reportId = reportId) } returns 0L

        every { reportProcurementBeneficialPersistence
            .updateBeneficialOwners(PARTNER_ID, reportId, procurementId, any())
        } returns listOf(beneficialOwner1, beneficialOwner2)

        val owners = listOf(changeOwner1)
        assertThrows<AppInputValidationException> { interactor.update(PARTNER_ID, reportId, procurementId, owners) }
        assertThat(validationSlot).containsExactly(
            mapOf("firstName[0]" to I18nMessage("firstName NEW---maxLength-50")),
            mapOf("lastName[0]" to I18nMessage("lastName NEW---maxLength-50")),
            mapOf("vatNumber[0]" to I18nMessage("vatNumber NEW---maxLength-30")),
            mapOf("vatNumber[0]" to I18nMessage("vatNumber NEW---notBlank")),
        )
    }

}
