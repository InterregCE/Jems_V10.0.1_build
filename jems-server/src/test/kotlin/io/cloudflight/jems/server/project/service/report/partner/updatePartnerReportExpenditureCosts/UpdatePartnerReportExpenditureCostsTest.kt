package io.cloudflight.jems.server.project.service.report.partner.updatePartnerReportExpenditureCosts

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.entity.report.PartnerReportIdentificationEntity
import io.cloudflight.jems.server.project.entity.report.ProjectPartnerReportEntity
import io.cloudflight.jems.server.project.entity.report.expenditureCosts.PartnerReportExpenditureCostEntity
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.PartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.partnerReportExpenditureCosts.PartnerReportExpenditureCosts
import io.cloudflight.jems.server.project.service.report.partner.workPlan.updateProjectPartnerWorkPlan.ReportAlreadyClosed
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

internal class UpdatePartnerReportExpenditureCostsTest : UnitTest() {

    private val inputErrorMap = mapOf("error" to I18nMessage("error.key"))
    private val PARTNER_ID = 489L
    private val REPORT_ID = 489L

    private val reportExpenditureCost = PartnerReportExpenditureCost(
        id = 754,
        costCategory = "costCategory",
        investmentNumber = "number-1",
        contractId = "",
        internalReferenceNumber = "internal-1",
        invoiceNumber = "invoice-1",
        invoiceDate = LocalDate.of(2022, 1, 1),
        dateOfPayment = LocalDate.of(2022, 2, 1),
        description = emptySet(),
        comment = emptySet(),
        totalValueInvoice = BigDecimal.valueOf(22),
        vat = BigDecimal.valueOf(18.0),
        declaredAmount = BigDecimal.valueOf(1.3)
    )

    private val reportExpenditureCostEntity = PartnerReportExpenditureCostEntity(
        id = 754,
        costCategory = "costCategory",
        investmentNumber = "number-1",
        contractId = "",
        internalReferenceNumber = "internal-1",
        invoiceNumber = "invoice-1",
        invoiceDate = LocalDate.of(2022, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
        dateOfPayment = LocalDate.of(2022, 2, 1).atStartOfDay(ZoneId.systemDefault()).toInstant(),
        translatedValues = mutableSetOf(),
        totalValueInvoice = BigDecimal.valueOf(22),
        vat = BigDecimal.valueOf(18.0),
        declaredAmount = BigDecimal.valueOf(1.3),
        partnerReport = null
    )

    private val partnerReportEntity = ProjectPartnerReportEntity(
        id = 754,
        partnerId = PARTNER_ID,
        number = 1,
        status = ReportStatus.Draft,
        applicationFormVersion = "3.0",
        firstSubmission = null,
        identification = PartnerReportIdentificationEntity(
            projectIdentifier = "projectIdentifier",
            projectAcronym = "projectAcronym",
            partnerNumber = 4,
            partnerAbbreviation = "partnerAbbreviation",
            partnerRole = ProjectPartnerRole.PARTNER,
            nameInOriginalLanguage = "nameInOriginalLanguage",
            nameInEnglish = "nameInEnglish",
            legalStatus = null,
            partnerType = ProjectTargetGroup.SectoralAgency,
            vatRecovery = ProjectPartnerVatRecovery.Yes,
        ),
        createdAt = ZonedDateTime.of(LocalDateTime.MAX, ZoneId.systemDefault()),
        expenditureCosts = mutableSetOf(reportExpenditureCostEntity)
    )


    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @InjectMockKs
    lateinit var updatePartnerReportExpenditureCosts: PartnerReportExpenditureCosts

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @BeforeEach
    fun setup() {
        clearMocks(reportPersistence)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws AppInputValidationException(
            inputErrorMap
        )
    }

    @Test
    fun `update - succesfuly`() {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, REPORT_ID) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Draft, "4.12.0")
        every {
            reportPersistence.updatePartnerReportExpenditureCosts(
                REPORT_ID,
                listOf(reportExpenditureCost)
            )
        } returns partnerReportEntity

        assertThat(
            updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(
                PARTNER_ID,
                REPORT_ID,
                listOf(reportExpenditureCost)
            )
        ).containsExactly(reportExpenditureCost)
    }

    @Test
    fun `update - wrong status`() {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, REPORT_ID) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Submitted, "4.12.0")
        assertThrows<ReportAlreadyClosed> {
            updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(
                PARTNER_ID,
                REPORT_ID,
                emptyList()
            )
        }
    }

    @Test
    fun `update - check if comment length is validated`() {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, REPORT_ID) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Draft, "4.12.0")
        val comment = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(256)))
        every {
            generalValidator.maxLength(comment, 255, "comment")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(
                PARTNER_ID, REPORT_ID, listOf(reportExpenditureCost.copy(comment = comment))
            )
        }
        verify(exactly = 1) { generalValidator.maxLength(comment, 255, "comment") }
    }

    @Test
    fun `update - check if description length is validated`() {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, REPORT_ID) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Draft, "4.12.0")
        val description = setOf(InputTranslation(SystemLanguage.EN, getStringOfLength(256)))
        every {
            generalValidator.maxLength(description, 255, "description")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(
                PARTNER_ID, REPORT_ID, listOf(reportExpenditureCost.copy(description = description))
            )
        }
        verify(exactly = 1) { generalValidator.maxLength(description, 255, "description") }
    }

    @Test
    fun `update - check if internal reference number length is validated`() {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, REPORT_ID) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Draft, "4.12.0")
        val internalReferenceNumber = getStringOfLength(31)
        every {
            generalValidator.maxLength(internalReferenceNumber, 30, "internalReferenceNumber")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(
                PARTNER_ID,
                REPORT_ID,
                listOf(reportExpenditureCost.copy(internalReferenceNumber = internalReferenceNumber))
            )
        }
        verify(exactly = 1) { generalValidator.maxLength(internalReferenceNumber, 30, "internalReferenceNumber") }
    }

    @Test
    fun `update - check if invoiceNumber number length is validated`() {
        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, REPORT_ID) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Draft, "4.12.0")
        val invoiceNumber = getStringOfLength(31)
        every {
            generalValidator.maxLength(invoiceNumber, 30, "invoiceNumber")
        } returns inputErrorMap
        assertThrows<AppInputValidationException> {
            updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(
                PARTNER_ID, REPORT_ID, listOf(reportExpenditureCost.copy(invoiceNumber = invoiceNumber))
            )
        }
        verify(exactly = 1) { generalValidator.maxLength(invoiceNumber, 30, "invoiceNumber") }
    }
}
