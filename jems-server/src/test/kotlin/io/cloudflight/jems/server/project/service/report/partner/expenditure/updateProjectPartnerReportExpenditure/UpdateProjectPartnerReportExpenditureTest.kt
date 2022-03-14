package io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportStatusAndVersion
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Collections
import kotlin.collections.ArrayList

internal class UpdateProjectPartnerReportExpenditureTest : UnitTest() {

    private val PARTNER_ID = 489L

    private val reportExpenditureCost = ProjectPartnerReportExpenditureCost(
        id = 780,
        costCategory = "cc",
        investmentNumber = "in",
        contractId = "cid",
        internalReferenceNumber = "irn",
        invoiceNumber = "invoice",
        invoiceDate = LocalDate.now().minusDays(1),
        dateOfPayment = LocalDate.now().plusDays(1),
        description = setOf(InputTranslation(SystemLanguage.EN, "desc EN")),
        comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
        totalValueInvoice = BigDecimal.ONE,
        vat = BigDecimal.ZERO,
        declaredAmount = BigDecimal.TEN,
    )

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportExpenditurePersistence: ProjectReportExpenditurePersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var updatePartnerReportExpenditureCosts: UpdateProjectPartnerReportExpenditure

    @BeforeEach
    fun setup() {
        clearMocks(reportPersistence)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws
            AppInputValidationException(emptyMap())
    }

    @Test
    fun `update - successfully`() {
        val slotString = mutableListOf<String>()
        val slotTranslations = mutableListOf<Set<InputTranslation>>()
        every { generalValidator.maxLength(capture(slotString), any(), any()) } returns emptyMap()
        every { generalValidator.maxLength(capture(slotTranslations), any(), any()) } returns emptyMap()

        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, 84L) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Draft, "0.8")
        every {
            reportExpenditurePersistence.updatePartnerReportExpenditureCosts(
                partnerId = PARTNER_ID,
                reportId = 84L,
                any(),
            )
        } returnsArgument 2

        assertThat(
            updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(
                PARTNER_ID,
                84L,
                listOf(reportExpenditureCost)
            )
        ).containsExactly(reportExpenditureCost)

        assertThat(slotString).containsExactlyInAnyOrder("invoice", "irn")
        assertThat(slotTranslations.map { it.first().translation }).containsExactlyInAnyOrder("comment EN", "desc EN")
    }

    @Test
    fun `update - max amount reached`() {
        val listMock = ArrayList(Collections.nCopies(151, mockk<ProjectPartnerReportExpenditureCost>()))
        assertThrows<MaxAmountOfExpendituresReached> {
            updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId = 84L, listMock)
        }
    }

    @Test
    fun `update - report closed`() {
        every { generalValidator.maxLength(any<String>(), any(), any()) } returns emptyMap()
        every { generalValidator.maxLength(any<Set<InputTranslation>>(), any(), any()) } returns emptyMap()

        every { reportPersistence.getPartnerReportStatusAndVersion(PARTNER_ID, 87L) } returns
            ProjectPartnerReportStatusAndVersion(ReportStatus.Submitted, "0.9")

        assertThrows<ReportAlreadyClosed> {
            updatePartnerReportExpenditureCosts.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId = 87L, listOf(reportExpenditureCost))
        }
    }

}
