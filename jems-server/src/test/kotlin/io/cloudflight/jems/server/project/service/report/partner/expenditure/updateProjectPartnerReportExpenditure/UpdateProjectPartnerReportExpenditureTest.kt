package io.cloudflight.jems.server.project.service.report.partner.expenditure.updateProjectPartnerReportExpenditure

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.authorization.AuthorizationUtil
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportUnitCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectPartnerReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
import io.cloudflight.jems.server.user.service.model.UserRolePermission
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal
import java.time.LocalDate

internal class UpdateProjectPartnerReportExpenditureTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 877L
        private val DAYS_AGO_2 = LocalDate.now().minusDays(1)
        private val DAYS_AGO_3 = LocalDate.now().minusDays(1)

        private fun report(id: Long, status: ReportStatus): ProjectPartnerReport {
            val report = mockk<ProjectPartnerReport>()
            every { report.id } returns id
            every { report.status } returns status
            every { report.identification.currency } returns "GBP"
            return report
        }

        private fun lumpSum(id: Long): ProjectPartnerReportLumpSum {
            val lumpSum = mockk<ProjectPartnerReportLumpSum>()
            every { lumpSum.id } returns id
            every { lumpSum.cost } returns BigDecimal.TEN
            return lumpSum
        }

        private fun unitCost(id: Long): ProjectPartnerReportUnitCost {
            val unitCost = mockk<ProjectPartnerReportUnitCost>()
            every { unitCost.id } returns id
            every { unitCost.category } returns ReportBudgetCategory.EquipmentCosts
            every { unitCost.costPerUnitForeignCurrency } returns BigDecimal.valueOf(7211L, 2)
            return unitCost
        }

        private fun investment(id: Long): ProjectPartnerReportInvestment {
            val investment = mockk<ProjectPartnerReportInvestment>()
            every { investment.id } returns id
            return investment
        }

        private val expenditureDummy = ProjectPartnerReportExpenditureCost(
            id = 389L,
            number = 1,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.StaffCosts,
            gdpr = false,
            investmentId = null,
            contractId = null,
            internalReferenceNumber = "irn",
            invoiceNumber = null,
            invoiceDate = DAYS_AGO_2,
            dateOfPayment = DAYS_AGO_3,
            description = setOf(InputTranslation(SystemLanguage.EN, "desc EN")),
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
            totalValueInvoice = BigDecimal.valueOf(10L),
            vat = null,
            numberOfUnits = BigDecimal.ZERO,
            pricePerUnit = BigDecimal.ZERO,
            declaredAmount = BigDecimal.valueOf(14L),
            currencyCode = "PLN",
            currencyConversionRate = null,
            declaredAmountAfterSubmission = null,
            attachment = null,
            parkingMetadata = null,
        )

    }

    @MockK lateinit var reportPersistence: ProjectPartnerReportPersistence
    @MockK lateinit var reportExpenditurePersistence: ProjectPartnerReportExpenditurePersistence
    @MockK lateinit var reportProcurementPersistence: ProjectPartnerReportProcurementPersistence
    @MockK lateinit var sensitiveDataAuthorization: SensitiveDataAuthorizationService
    @RelaxedMockK lateinit var generalValidator: GeneralValidatorService
    @MockK lateinit var securityService: SecurityService

    @InjectMockKs
    lateinit var interactor: UpdateProjectPartnerReportExpenditure

    @BeforeEach
    fun setup() {
        clearMocks(reportPersistence)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws AppInputValidationException(emptyMap())
        every { securityService.currentUser } returns AuthorizationUtil.programmeUser
        every { sensitiveDataAuthorization.isCurrentUserCollaboratorWithSensitiveFor(PARTNER_ID) } returns true
        every { reportExpenditurePersistence.updatePartnerReportExpenditureCosts(PARTNER_ID, any(), any()) } returnsArgument 2
    }

    @ParameterizedTest(name = "updatePartnerReportExpenditureCosts - remove not existing lumpSum and invalid StaffCosts fields - {0}")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"])
    fun `updatePartnerReportExpenditureCosts - remove not existing lumpSum and invalid StaffCosts fields`(status: ReportStatus) {
        val reportId = 642L

        mockGenericData(reportId, status)
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId) } returns
                listOf(expenditureDummy)

        val newValues = listOf(expenditureDummy.copy(
            lumpSumId = 6666L,
            investmentId = 6666L,
            contractId = 6666L,
            invoiceNumber = "ignore-me",
            vat = BigDecimal.valueOf(6666L),
            numberOfUnits = BigDecimal.valueOf(6666L),
            pricePerUnit = BigDecimal.valueOf(6666L),
            declaredAmountAfterSubmission = BigDecimal.valueOf(16L),
        ))

        assertThat(interactor.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId, newValues))
            .containsExactly(expenditureDummy)
    }

    @ParameterizedTest(name = "updatePartnerReportExpenditureCosts - only limited - {0}")
    @EnumSource(value = ReportStatus::class, names = ["ReOpenSubmittedLimited", "ReOpenInControlLimited"])
    fun `updatePartnerReportExpenditureCosts - only limited`(status: ReportStatus) {
        val reportId = 644L

        // cover case for programme user
        every { sensitiveDataAuthorization.isCurrentUserCollaboratorWithSensitiveFor(PARTNER_ID) } returns true
        every { securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingEdit) } returns true

        mockGenericData(reportId, status)
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId) } returns
                listOf(expenditureDummy.copy(costCategory = ReportBudgetCategory.OfficeAndAdministrationCosts))

        val newValue = expenditureDummy.copy(
            costCategory = ReportBudgetCategory.OfficeAndAdministrationCosts,
            investmentId = 540L,
            contractId = 530L,
            internalReferenceNumber = "new value",
            invoiceNumber = "new value",
            invoiceDate = LocalDate.now(),
            dateOfPayment = LocalDate.now(),
            description = setOf(InputTranslation(SystemLanguage.PL, "desc PL")),
            comment = setOf(InputTranslation(SystemLanguage.PL, "comment PL")),
            totalValueInvoice = BigDecimal.valueOf(999L),
            vat = BigDecimal.valueOf(999L),
            numberOfUnits = BigDecimal.valueOf(999L),
            pricePerUnit  = BigDecimal.valueOf(999L),
            declaredAmount  = BigDecimal.valueOf(999L),
            currencyCode = "CZK",
            gdpr = true
        )

        assertThat(interactor.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId, listOf(newValue)))
            .containsExactly(
                expenditureDummy.copy(
                    costCategory = ReportBudgetCategory.OfficeAndAdministrationCosts,
                    gdpr = true,
                    description = setOf(InputTranslation(SystemLanguage.PL, "desc PL")),
                    comment = setOf(InputTranslation(SystemLanguage.PL, "comment PL")),
                    contractId = 530L
                )
            )
    }

    @ParameterizedTest(name = "updatePartnerReportExpenditureCosts - only limited - gdpr sensitive - {0}")
    @EnumSource(value = ReportStatus::class, names = ["ReOpenSubmittedLimited", "ReOpenInControlLimited"])
    fun `updatePartnerReportExpenditureCosts - only limited - gdpr sensitive`(status: ReportStatus) {
        val reportId = 646L

        // cover case for programme user
        every { sensitiveDataAuthorization.isCurrentUserCollaboratorWithSensitiveFor(PARTNER_ID) } returns false
        every { securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingEdit) } returns false

        mockGenericData(reportId, status)
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId) } returns
                listOf(expenditureDummy.copy(costCategory = ReportBudgetCategory.OfficeAndAdministrationCosts, gdpr = true))

        val newValue = expenditureDummy.copy(
            costCategory = ReportBudgetCategory.OfficeAndAdministrationCosts,
            contractId = 530L,
            description = setOf(InputTranslation(SystemLanguage.PL, "desc PL")),
            comment = setOf(InputTranslation(SystemLanguage.PL, "comment PL")),
        )

        // no change
        assertThat(interactor.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId, listOf(newValue)))
            .containsExactly(expenditureDummy.copy(
                costCategory = ReportBudgetCategory.OfficeAndAdministrationCosts,
                gdpr = true,
                description = setOf(InputTranslation(SystemLanguage.EN, "************")),
                comment = setOf(InputTranslation(SystemLanguage.EN, "************")),
            ))
    }

    @ParameterizedTest(name = "updatePartnerReportExpenditureCosts - remove not existing unitCost - OfficeAndAdministrationCosts - {0}")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"])
    fun `updatePartnerReportExpenditureCosts - remove not existing unitCost - OfficeAndAdministrationCosts`(status: ReportStatus) {
        val reportId = 648L

        mockGenericData(reportId, status)
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId) } returns
                listOf(expenditureDummy.copy(costCategory = ReportBudgetCategory.OfficeAndAdministrationCosts))

        val newValues = listOf(expenditureDummy.copy(
            costCategory = ReportBudgetCategory.OfficeAndAdministrationCosts,
            unitCostId = 6666L,
            investmentId = 540L,
            contractId = 530L,
            invoiceNumber = "DO-NOT-ignore-me",
            vat = BigDecimal.valueOf(6666L),
            numberOfUnits = BigDecimal.valueOf(6666L),
            pricePerUnit = BigDecimal.valueOf(6666L),
            declaredAmountAfterSubmission = BigDecimal.valueOf(16L),
        ))

        assertThat(interactor.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId, newValues))
            .containsExactly(expenditureDummy.copy(
                costCategory = ReportBudgetCategory.OfficeAndAdministrationCosts,
                investmentId = 540L,
                contractId = 530L,
                invoiceNumber = "DO-NOT-ignore-me",
                vat = BigDecimal.valueOf(6666L),
            ))
    }

    @ParameterizedTest(name = "updatePartnerReportExpenditureCosts - lumpSum - {0}")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"])
    fun `updatePartnerReportExpenditureCosts - lumpSum`(status: ReportStatus) {
        val reportId = 650L

        mockGenericData(reportId, status)
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId) } returns
                listOf(expenditureDummy.copy(costCategory = ReportBudgetCategory.ExternalCosts /* should be fixed */))

        val newValues = listOf(expenditureDummy.copy(
            costCategory = ReportBudgetCategory.ExternalCosts /* should be fixed */,
            lumpSumId = 510L,
        ))

        assertThat(interactor.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId, newValues))
            .containsExactly(expenditureDummy.copy(
                costCategory = ReportBudgetCategory.Multiple,
                lumpSumId = 510L,
                numberOfUnits = BigDecimal.ONE,
                pricePerUnit = BigDecimal.TEN,
                declaredAmount = BigDecimal.TEN,
            ))
    }

    @ParameterizedTest(name = "updatePartnerReportExpenditureCosts - unitCost - {0}")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"])
    fun `updatePartnerReportExpenditureCosts - unitCost`(status: ReportStatus) {
        val reportId = 652L

        mockGenericData(reportId, status)
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId) } returns
                listOf(expenditureDummy.copy(costCategory = ReportBudgetCategory.ExternalCosts /* should be fixed */))

        val newValues = listOf(expenditureDummy.copy(
            costCategory = ReportBudgetCategory.ExternalCosts /* should be fixed */,
            unitCostId = 520L,
            numberOfUnits = BigDecimal.valueOf(2L),
        ))

        assertThat(interactor.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId, newValues))
            .containsExactly(expenditureDummy.copy(
                costCategory = ReportBudgetCategory.EquipmentCosts,
                unitCostId = 520L,
                numberOfUnits = BigDecimal.valueOf(2L),
                pricePerUnit = BigDecimal.valueOf(7211L, 2),
                declaredAmount = BigDecimal.valueOf(14422L, 2),
            ))
    }

    @ParameterizedTest(name = "updatePartnerReportExpenditureCosts - can not edit sensitive data - {0}")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"])
    fun `updatePartnerReportExpenditureCosts - can not edit sensitive data`(status: ReportStatus) {
        val reportId = 654L

        mockGenericData(reportId, status)
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId) } returns
                listOf(expenditureDummy.copy(gdpr = true))

        every { sensitiveDataAuthorization.isCurrentUserCollaboratorWithSensitiveFor(PARTNER_ID) } returns false
        every { securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingEdit) } returns false

        val newValues = listOf(expenditureDummy.copy(
            costCategory = ReportBudgetCategory.EquipmentCosts,
            unitCostId = 520L,
            numberOfUnits = BigDecimal.valueOf(2L),
        ))

        // no change
        assertThat(interactor.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId, newValues))
            .containsExactly(expenditureDummy.copy(
                gdpr = true,
                description = setOf(InputTranslation(SystemLanguage.EN, "************")),
                comment = setOf(InputTranslation(SystemLanguage.EN, "************")),
            ))
    }

    @ParameterizedTest(name = "updatePartnerReportExpenditureCosts - can edit sensitiveness flag - {0}")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"])
    fun `updatePartnerReportExpenditureCosts - can edit sensitiveness flag`(status: ReportStatus) {
        val reportId = 656L

        mockGenericData(reportId, status)
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId) } returns
                listOf(expenditureDummy.copy(gdpr = true))

        val newValues = listOf(expenditureDummy.copy(gdpr = false))

        assertThat(interactor.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId, newValues))
            .containsExactly(expenditureDummy.copy(gdpr = false))
    }

    @ParameterizedTest(name = "updatePartnerReportExpenditureCosts - canNOT edit sensitiveness flag - {0}")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"])
    fun `updatePartnerReportExpenditureCosts - canNOT edit sensitiveness flag`(status: ReportStatus) {
        val reportId = 656L

        mockGenericData(reportId, status)
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId) } returns
                listOf(expenditureDummy.copy(gdpr = true))

        every { sensitiveDataAuthorization.isCurrentUserCollaboratorWithSensitiveFor(PARTNER_ID) } returns false
        every { securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingEdit) } returns true

        val newValues = listOf(expenditureDummy.copy(gdpr = false))

        assertThat(interactor.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId, newValues))
            .containsExactly(expenditureDummy.copy(gdpr = true))
    }


    @ParameterizedTest(name = "updatePartnerReportExpenditureCosts - deletion - canNOT edit sensitiveness flag - {0}")
    @EnumSource(value = ReportStatus::class, names = ["Draft"])
    fun `updatePartnerReportExpenditureCosts - deletion - canNOT edit sensitiveness flag`(status: ReportStatus) {
        val reportId = 658L

        mockGenericData(reportId, status)
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId) } returns
                listOf(expenditureDummy.copy(gdpr = true))

        every { sensitiveDataAuthorization.isCurrentUserCollaboratorWithSensitiveFor(PARTNER_ID) } returns false
        every { securityService.currentUser?.hasPermission(UserRolePermission.ProjectReportingEdit) } returns true

        assertThrows<ExpenditureSensitiveDataRemoved> { interactor.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId, emptyList()) }
    }

    @ParameterizedTest(name = "updatePartnerReportExpenditureCosts - deletion - wrong status - {0}")
    @EnumSource(value = ReportStatus::class, names = ["ReOpenSubmittedLast", "ReOpenSubmittedLimited", "ReOpenInControlLast", "ReOpenInControlLimited"])
    fun `updatePartnerReportExpenditureCosts - deletion - wrong status`(status: ReportStatus) {
        val reportId = 660L

        mockGenericData(reportId, status)
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId) } returns
                listOf(expenditureDummy.copy(gdpr = true))

        assertThrows<DeletionNotAllowedAnymore> { interactor.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId, emptyList()) }
    }

    @ParameterizedTest(name = "updatePartnerReportExpenditureCosts - reNumber reIncluded ones - {0}")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"])
    fun `updatePartnerReportExpenditureCosts - reNumber reIncluded ones`(status: ReportStatus) {
        val reportId = 662L

        mockGenericData(reportId, status)
        val parkingMetaFor2 = mockk<ExpenditureParkingMetadata>()
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId) } returns listOf(
            expenditureDummy.copy(number = 1),
            expenditureDummy.copy(
                id = 390L,
                currencyConversionRate = BigDecimal.ONE,
                declaredAmountAfterSubmission = BigDecimal.valueOf(14L),
                parkingMetadata = parkingMetaFor2,
            ),
        )

        val newValues = listOf(
            expenditureDummy.copy(number = 1),
            expenditureDummy.copy(id = 390L),
            expenditureDummy.copy(id = null),
        )

        assertThat(interactor.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId, newValues))
            .containsExactly(
                expenditureDummy.copy(number = 1),
                expenditureDummy.copy(
                    id = 390L,
                    number = 0,
                    currencyConversionRate = BigDecimal.ONE,
                    declaredAmountAfterSubmission = BigDecimal.valueOf(14L),
                    parkingMetadata = parkingMetaFor2,
                ),
                expenditureDummy.copy(id = 0L, number = 2),
            )
    }

    @ParameterizedTest(name = "updatePartnerReportExpenditureCosts - unitCost combined with lumpSum - {0}")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"])
    fun `updatePartnerReportExpenditureCosts - unitCost combined with lumpSum`(status: ReportStatus) {
        val reportId = 664L

        mockGenericData(reportId, status)
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId) } returns emptyList()

        val newValues = listOf(
            expenditureDummy.copy(lumpSumId = 510L, unitCostId = 520L),
        )

        assertThrows<LumpSumCannotBeSelectedTogetherWithUnitCost> {
            interactor.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId, newValues)
        }
    }

    @ParameterizedTest(name = "updatePartnerReportExpenditureCosts - default EUR but provided different - {0}")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"])
    fun `updatePartnerReportExpenditureCosts - default EUR but provided different`(status: ReportStatus) {
        val reportId = 666L

        val report = mockGenericData(reportId, status)
        every { report.identification.currency } returns "EUR"

        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId) } returns emptyList()

        val newValues = listOf(
            expenditureDummy.copy(currencyCode = "PLN"),
        )

        assertThrows<PartnerWithDefaultEurCannotSelectOtherCurrency> {
            interactor.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId, newValues)
        }
    }

    @ParameterizedTest(name = "updatePartnerReportExpenditureCosts not allowed to change regular to an SCO - {0}")
    @EnumSource(value = ReportStatus::class, names = ["Draft", "ReOpenSubmittedLast", "ReOpenInControlLast"])
    fun `updatePartnerReportExpenditureCosts - change to SCO throws`(status: ReportStatus) {
        val reportId = 512L

        mockGenericData(reportId, status)
        every { reportExpenditurePersistence.getPartnerReportExpenditureCosts(PARTNER_ID, reportId) } returns listOf(
            expenditureDummy.copy(
                gdpr = false,
                costCategory = ReportBudgetCategory.StaffCosts,
                number = 1,
                parkingMetadata = ExpenditureParkingMetadata(
                    reportOfOriginId = 388L,
                    reportProjectOfOriginId = null,
                    reportOfOriginNumber = 2,
                    originalExpenditureNumber = 2
                )
            ),
        )

        val newValues = listOf(
            expenditureDummy.copy(
                gdpr = false,
                costCategory = ReportBudgetCategory.Multiple,
                number = 1,
                parkingMetadata = ExpenditureParkingMetadata(
                    reportOfOriginId = 388L,
                    reportProjectOfOriginId = null,
                    reportOfOriginNumber = 2,
                    originalExpenditureNumber = 2
                )
            ),
        )

        assertThrows<SCOCannotBeSelectedInsteadOfRealCost> { interactor.updatePartnerReportExpenditureCosts(PARTNER_ID, reportId, newValues) }

    }


    private fun mockGenericData(reportId: Long, status: ReportStatus): ProjectPartnerReport {
        val report = report(reportId, status)

        every { reportPersistence.getPartnerReportById(PARTNER_ID, reportId = reportId) } returns report

        every { reportExpenditurePersistence.getAvailableLumpSums(PARTNER_ID, reportId) } returns listOf(lumpSum(510L))
        every { reportExpenditurePersistence.getAvailableUnitCosts(PARTNER_ID, reportId) } returns listOf(unitCost(520L))
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = reportId) } returns setOf(77L)
        every { reportProcurementPersistence.getProcurementContractNamesForReportIds(setOf(reportId, 77L)) } returns setOf(Pair(530L, "proc-name-530"))
        every { reportExpenditurePersistence.getAvailableInvestments(PARTNER_ID, reportId) } returns listOf(investment(540L))

        return report
    }

}
