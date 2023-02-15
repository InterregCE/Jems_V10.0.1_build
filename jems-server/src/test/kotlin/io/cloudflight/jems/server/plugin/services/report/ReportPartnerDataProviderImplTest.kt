package io.cloudflight.jems.server.plugin.services.report

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.plugin.contract.models.common.InputTranslationData
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundData
import io.cloudflight.jems.plugin.contract.models.programme.fund.ProgrammeFundTypeData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerRoleData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.ProjectPartnerVatRecoveryData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerCoFinancingFundTypeData
import io.cloudflight.jems.plugin.contract.models.project.sectionB.partners.budget.ProjectPartnerContributionStatusData
import io.cloudflight.jems.plugin.contract.models.project.sectionC.relevance.ProjectTargetGroupData
import io.cloudflight.jems.plugin.contract.models.report.JemsFileMetadataData
import io.cloudflight.jems.plugin.contract.models.report.partner.contribution.ProjectPartnerReportContributionData
import io.cloudflight.jems.plugin.contract.models.report.partner.contribution.ProjectPartnerReportContributionOverviewData
import io.cloudflight.jems.plugin.contract.models.report.partner.contribution.ProjectPartnerReportContributionRowData
import io.cloudflight.jems.plugin.contract.models.report.partner.contribution.ProjectPartnerReportContributionWrapperData
import io.cloudflight.jems.plugin.contract.models.report.partner.expenditure.ProjectPartnerReportExpenditureCostData
import io.cloudflight.jems.plugin.contract.models.report.partner.expenditure.ReportBudgetCategoryData
import io.cloudflight.jems.plugin.contract.models.report.partner.financialOverview.ExpenditureCoFinancingBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.partner.financialOverview.ExpenditureCoFinancingBreakdownLineData
import io.cloudflight.jems.plugin.contract.models.report.partner.financialOverview.ExpenditureCostCategoryBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.partner.financialOverview.ExpenditureCostCategoryBreakdownLineData
import io.cloudflight.jems.plugin.contract.models.report.partner.financialOverview.ExpenditureInvestmentBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.partner.financialOverview.ExpenditureInvestmentBreakdownLineData
import io.cloudflight.jems.plugin.contract.models.report.partner.financialOverview.ExpenditureLumpSumBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.partner.financialOverview.ExpenditureLumpSumBreakdownLineData
import io.cloudflight.jems.plugin.contract.models.report.partner.financialOverview.ExpenditureUnitCostBreakdownData
import io.cloudflight.jems.plugin.contract.models.report.partner.financialOverview.ExpenditureUnitCostBreakdownLineData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProgrammeLegalStatusData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProgrammeLegalStatusTypeData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProjectPartnerReportData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProjectPartnerReportIdentificationData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProjectPartnerReportIdentificationTargetGroupData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProjectPartnerReportPeriodData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ProjectPartnerReportSpendingProfileData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ReportFileFormatData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ReportStatusData
import io.cloudflight.jems.plugin.contract.models.report.partner.identification.ReportTypeData
import io.cloudflight.jems.plugin.contract.models.report.partner.procurement.ProjectPartnerReportProcurementBeneficialOwnerData
import io.cloudflight.jems.plugin.contract.models.report.partner.procurement.ProjectPartnerReportProcurementData
import io.cloudflight.jems.plugin.contract.models.report.partner.procurement.ProjectPartnerReportProcurementSubcontractData
import io.cloudflight.jems.plugin.contract.models.report.partner.procurement.ProjectReportProcurementFileData
import io.cloudflight.jems.plugin.contract.models.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityData
import io.cloudflight.jems.plugin.contract.models.report.partner.workPlan.ProjectPartnerReportWorkPackageActivityDeliverableData
import io.cloudflight.jems.plugin.contract.models.report.partner.workPlan.ProjectPartnerReportWorkPackageData
import io.cloudflight.jems.plugin.contract.models.report.partner.workPlan.ProjectPartnerReportWorkPackageOutputData
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContributionStatus
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import io.cloudflight.jems.server.project.service.report.model.partner.PartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.contribution.withoutCalculations.ProjectPartnerReportEntityContribution
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ExpenditureParkingMetadata
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportExpenditureCost
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ReportBudgetCategory
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.coFinancing.ExpenditureCoFinancingBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.costCategory.ExpenditureCostCategoryBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdown
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentification
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportIdentificationTargetGroup
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportSpendingProfile
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportFileFormat
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportType
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectReportProcurementFile
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.beneficial.ProjectPartnerReportProcurementBeneficialOwner
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.subcontract.ProjectPartnerReportProcurementSubcontract
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.ProjectPartnerReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.ProjectPartnerReportWorkPackageActivity
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.ProjectPartnerReportWorkPackageActivityDeliverable
import io.cloudflight.jems.server.project.service.report.model.partner.workPlan.ProjectPartnerReportWorkPackageOutput
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.contribution.ProjectPartnerReportContributionPersistence
import io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure.GetProjectPartnerReportExpenditureCalculator
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown.GetReportExpenditureCoFinancingBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown.GetReportExpenditureCostCategoryCalculatorService
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown.GetReportExpenditureInvestmentsBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown.GetReportExpenditureLumpSumBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown.GetReportExpenditureUnitCostBreakdownCalculator
import io.cloudflight.jems.server.project.service.report.partner.identification.getProjectPartnerReportIdentification.GetProjectPartnerReportIdentificationService
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.getProjectPartnerReportProcurementAttachment.GetProjectPartnerReportProcurementAttachmentService
import io.cloudflight.jems.server.project.service.report.partner.procurement.beneficial.getProjectPartnerReportProcurementBeneficial.GetProjectPartnerReportProcurementBeneficialService
import io.cloudflight.jems.server.project.service.report.partner.procurement.subcontract.getProjectPartnerReportProcurementSubcontract.GetProjectPartnerReportProcurementSubcontractService
import io.cloudflight.jems.server.project.service.report.partner.workPlan.ProjectPartnerReportWorkPlanPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.math.BigDecimal
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.UUID

internal class ReportPartnerDataProviderImplTest : UnitTest() {

    companion object {
        private val DATE_TIME_1 = ZonedDateTime.now()
        private val DATE_1 = LocalDate.now()
        private val DATE_2 = LocalDate.now()
        private val UUID_1 = UUID.randomUUID()

        private val report = ProjectPartnerReport(
            id = 96L,
            reportNumber = 12,
            status = ReportStatus.Draft,
            version = "6.5.1",
            firstSubmission = DATE_TIME_1,

            identification = PartnerReportIdentification(
                projectIdentifier = "identifier",
                projectAcronym = "acr",
                partnerNumber = 4,
                partnerAbbreviation = "prtn abbr",
                partnerRole = ProjectPartnerRole.PARTNER,
                nameInOriginalLanguage = "orig name",
                nameInEnglish = "name EN",
                legalStatus = ProgrammeLegalStatus(
                    id = 4L,
                    type = ProgrammeLegalStatusType.PRIVATE,
                    description = setOf(InputTranslation(SystemLanguage.EN, "EN legal status")),
                ),
                partnerType = ProjectTargetGroup.EducationTrainingCentreAndSchool,
                vatRecovery = ProjectPartnerVatRecovery.Partly,
                country = "SK",
                currency = "EUR",
                coFinancing = listOf(
                    ProjectPartnerCoFinancing(
                        fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                        fund = ProgrammeFund(
                            id = 57L,
                            true,
                            type = ProgrammeFundType.ERDF,
                            setOf(InputTranslation(SystemLanguage.EN, "abbr EN")),
                            setOf(InputTranslation(SystemLanguage.EN, "desc EN")),
                        ),
                        percentage = BigDecimal.ONE,
                    )
                ),
            )
        )

        private val expectedReport = ProjectPartnerReportData(
            id = 96L,
            reportNumber = 12,
            status = ReportStatusData.Draft,
            version = "6.5.1",
            firstSubmission = DATE_TIME_1,

            projectIdentifier = "identifier",
            projectAcronym = "acr",
            partnerNumber = 4,
            partnerAbbreviation = "prtn abbr",
            partnerRole = ProjectPartnerRoleData.PARTNER,
            nameInOriginalLanguage = "orig name",
            nameInEnglish = "name EN",
            legalStatus = ProgrammeLegalStatusData(
                id = 4L,
                type = ProgrammeLegalStatusTypeData.PRIVATE,
                description = setOf(InputTranslationData(SystemLanguageData.EN, "EN legal status")),
            ),
            partnerType = ProjectTargetGroupData.EducationTrainingCentreAndSchool,
            vatRecovery = ProjectPartnerVatRecoveryData.Partly,
            country = "SK",
            currency = "EUR",
            coFinancing = listOf(
                ProjectPartnerCoFinancingData(
                    fundType = ProjectPartnerCoFinancingFundTypeData.MainFund,
                    fund = ProgrammeFundData(
                        id = 57L,
                        true,
                        type = ProgrammeFundTypeData.ERDF,
                        setOf(InputTranslationData(SystemLanguageData.EN, "abbr EN")),
                        setOf(InputTranslationData(SystemLanguageData.EN, "desc EN")),
                    ),
                    percentage = BigDecimal.ONE,
                )
            ),
        )

        private val reportIdentification = ProjectPartnerReportIdentification(
            startDate = DATE_1,
            endDate = DATE_2,
            summary = setOf(InputTranslation(SystemLanguage.EN, "summary EN")),
            problemsAndDeviations = setOf(InputTranslation(SystemLanguage.EN, "prob&dev EN")),
            spendingDeviations = setOf(InputTranslation(SystemLanguage.EN, "spend dev EN")),
            targetGroups = listOf(
                ProjectPartnerReportIdentificationTargetGroup(
                    type = ProjectTargetGroup.Sme,
                    sortNumber = 9,
                    specification = setOf(InputTranslation(SystemLanguage.EN, "specif EN")),
                    description = setOf(InputTranslation(SystemLanguage.EN, "descr EN")),
                )
            ),
            spendingProfile = ProjectPartnerReportSpendingProfile(
                periodDetail = ProjectPartnerReportPeriod(
                    number = 2, periodBudget = BigDecimal.ONE, periodBudgetCumulative = BigDecimal.TEN, start = 4, end = 6,
                ),
                currentReport = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ONE,
                differenceFromPlan = BigDecimal.TEN,
                differenceFromPlanPercentage = BigDecimal.ZERO,
                nextReportForecast = BigDecimal.ONE,
            ),
            controllerFormats = setOf(ReportFileFormat.Originals),
            type = ReportType.FinalReport,
        )

        private val expectedReportIdentification = ProjectPartnerReportIdentificationData(
            startDate = DATE_1,
            endDate = DATE_2,
            summary = setOf(InputTranslationData(SystemLanguageData.EN, "summary EN")),
            problemsAndDeviations = setOf(InputTranslationData(SystemLanguageData.EN, "prob&dev EN")),
            spendingDeviations = setOf(InputTranslationData(SystemLanguageData.EN, "spend dev EN")),
            targetGroups = listOf(
                ProjectPartnerReportIdentificationTargetGroupData(
                    type = ProjectTargetGroupData.Sme,
                    sortNumber = 9,
                    specification = setOf(InputTranslationData(SystemLanguageData.EN, "specif EN")),
                    description = setOf(InputTranslationData(SystemLanguageData.EN, "descr EN")),
                )
            ),
            spendingProfile = ProjectPartnerReportSpendingProfileData(
                periodDetail = ProjectPartnerReportPeriodData(
                    number = 2, periodBudget = BigDecimal.ONE, periodBudgetCumulative = BigDecimal.TEN, start = 4, end = 6,
                ),
                currentReport = BigDecimal.ZERO,
                previouslyReported = BigDecimal.ONE,
                differenceFromPlan = BigDecimal.TEN,
                differenceFromPlanPercentage = BigDecimal.ZERO,
                nextReportForecast = BigDecimal.ONE,
            ),
            controllerFormats = setOf(ReportFileFormatData.Originals),
            type = ReportTypeData.FinalReport,
        )

        private val contribution = ProjectPartnerReportEntityContribution(
            id = 18L,
            sourceOfContribution = "source public 1",
            legalStatus = ProjectPartnerContributionStatus.Public,
            idFromApplicationForm = 200L,
            historyIdentifier = UUID_1,
            createdInThisReport = true,
            amount = BigDecimal.ZERO,
            previouslyReported = BigDecimal.ONE,
            currentlyReported = BigDecimal.TEN,
            attachment = JemsFileMetadata(45L, "file_pub_1.txt", DATE_TIME_1),
        )

        private val expectedContribution = ProjectPartnerReportContributionWrapperData(
            contributions = listOf(
                ProjectPartnerReportContributionData(
                    id = 18L,
                    sourceOfContribution = "source public 1",
                    legalStatus = ProjectPartnerContributionStatusData.Public,
                    createdInThisReport = true,
                    numbers = ProjectPartnerReportContributionRowData(
                        amount = BigDecimal.ZERO,
                        previouslyReported = BigDecimal.ONE,
                        currentlyReported = BigDecimal.TEN,
                        totalReportedSoFar = BigDecimal.valueOf(11L),
                    ),
                    attachment = JemsFileMetadataData(45L, "file_pub_1.txt", DATE_TIME_1),
                )
            ),
            overview = ProjectPartnerReportContributionOverviewData(
                public = ProjectPartnerReportContributionRowData(
                    amount = BigDecimal.ZERO,
                    previouslyReported = BigDecimal.ONE,
                    currentlyReported = BigDecimal.TEN,
                    totalReportedSoFar = BigDecimal.valueOf(11L),
                ),
                automaticPublic = ProjectPartnerReportContributionRowData(
                    amount = BigDecimal.ZERO,
                    previouslyReported = BigDecimal.ZERO,
                    currentlyReported = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.ZERO,
                ),
                private = ProjectPartnerReportContributionRowData(
                    amount = BigDecimal.ZERO,
                    previouslyReported = BigDecimal.ZERO,
                    currentlyReported = BigDecimal.ZERO,
                    totalReportedSoFar = BigDecimal.ZERO,
                ),
                total = ProjectPartnerReportContributionRowData(
                    amount = BigDecimal.ZERO,
                    previouslyReported = BigDecimal.ONE,
                    currentlyReported = BigDecimal.TEN,
                    totalReportedSoFar = BigDecimal.valueOf(11L),
                ),
            ),
        )

        private val expenditure = ProjectPartnerReportExpenditureCost(
            id = 770L,
            number = 1,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategory.OfficeAndAdministrationCosts,
            investmentId = 49L,
            contractId = 28L,
            internalReferenceNumber = "irn",
            invoiceNumber = "invoice",
            invoiceDate = DATE_1,
            dateOfPayment = DATE_2,
            description = setOf(InputTranslation(SystemLanguage.EN, "desc EN")),
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
            totalValueInvoice = BigDecimal.ONE,
            vat = BigDecimal.ZERO,
            numberOfUnits = BigDecimal.valueOf(77),
            pricePerUnit = BigDecimal.valueOf(44),
            declaredAmount = BigDecimal.TEN,
            currencyCode = "GBP",
            currencyConversionRate = BigDecimal.valueOf(0.84),
            declaredAmountAfterSubmission = BigDecimal.valueOf(8.4),
            attachment = JemsFileMetadata(47L, "file.xlsx", DATE_TIME_1),
            parkingMetadata = ExpenditureParkingMetadata(reportOfOriginId = 75L, reportOfOriginNumber = 4, originalExpenditureNumber = 3),
        )

        private val expectedExpenditure = ProjectPartnerReportExpenditureCostData(
            id = 770L,
            lumpSumId = null,
            unitCostId = null,
            costCategory = ReportBudgetCategoryData.OfficeAndAdministrationCosts,
            investmentId = 49L,
            contractId = 28L,
            internalReferenceNumber = "irn",
            invoiceNumber = "invoice",
            invoiceDate = DATE_1,
            dateOfPayment = DATE_2,
            description = setOf(InputTranslationData(SystemLanguageData.EN, "desc EN")),
            comment = setOf(InputTranslationData(SystemLanguageData.EN, "comment EN")),
            totalValueInvoice = BigDecimal.ONE,
            vat = BigDecimal.ZERO,
            numberOfUnits = BigDecimal.valueOf(77),
            pricePerUnit = BigDecimal.valueOf(44),
            declaredAmount = BigDecimal.TEN,
            currencyCode = "GBP",
            currencyConversionRate = BigDecimal.valueOf(0.84),
            declaredAmountAfterSubmission = BigDecimal.valueOf(8.4),
            attachment = JemsFileMetadataData(47L, "file.xlsx", DATE_TIME_1),
        )

        private val dummyLineCoFin = ExpenditureCoFinancingBreakdownLine(
            fundId = null,
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            previouslyPaid = BigDecimal.valueOf(3),
            currentReport = BigDecimal.valueOf(4),
            totalEligibleAfterControl = BigDecimal.valueOf(8),
            totalReportedSoFar = BigDecimal.valueOf(5),
            totalReportedSoFarPercentage = BigDecimal.valueOf(6),
            remainingBudget = BigDecimal.valueOf(7),
        )

        private val coFinancing = ExpenditureCoFinancingBreakdown(
            funds = listOf(dummyLineCoFin.copy(fundId = 45L)),
            partnerContribution = dummyLineCoFin.copy(totalEligibleBudget = BigDecimal.TEN),
            publicContribution = dummyLineCoFin.copy(previouslyReported = BigDecimal.TEN),
            automaticPublicContribution = dummyLineCoFin.copy(previouslyPaid = BigDecimal.TEN),
            privateContribution = dummyLineCoFin.copy(currentReport = BigDecimal.TEN),
            total = dummyLineCoFin.copy(totalReportedSoFar = BigDecimal.TEN),
        )

        private val dummyLineCoFinData = ExpenditureCoFinancingBreakdownLineData(
            fundId = null,
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            previouslyPaid = BigDecimal.valueOf(3),
            currentReport = BigDecimal.valueOf(4),
            totalReportedSoFar = BigDecimal.valueOf(5),
            totalReportedSoFarPercentage = BigDecimal.valueOf(6),
            remainingBudget = BigDecimal.valueOf(7),
        )

        private val expectedCoFinancing = ExpenditureCoFinancingBreakdownData(
            funds = listOf(dummyLineCoFinData.copy(fundId = 45L)),
            partnerContribution = dummyLineCoFinData.copy(totalEligibleBudget = BigDecimal.TEN),
            publicContribution = dummyLineCoFinData.copy(previouslyReported = BigDecimal.TEN),
            automaticPublicContribution = dummyLineCoFinData.copy(previouslyPaid = BigDecimal.TEN),
            privateContribution = dummyLineCoFinData.copy(currentReport = BigDecimal.TEN),
            total = dummyLineCoFinData.copy(totalReportedSoFar = BigDecimal.TEN),
        )

        private val dummyCostCategoryLine = ExpenditureCostCategoryBreakdownLine(
            flatRate = 50,
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            previouslyReportedParked = BigDecimal.ZERO,
            currentReport = BigDecimal.valueOf(3),
            currentReportReIncluded = BigDecimal.ZERO,
            totalEligibleAfterControl = BigDecimal.valueOf(8),
            totalReportedSoFar = BigDecimal.valueOf(4),
            totalReportedSoFarPercentage = BigDecimal.valueOf(5),
            remainingBudget = BigDecimal.valueOf(6),
        )

        private val costCategory = ExpenditureCostCategoryBreakdown(
            staff = dummyCostCategoryLine.copy(flatRate = 1),
            office = dummyCostCategoryLine.copy(totalEligibleBudget = BigDecimal.TEN),
            travel = dummyCostCategoryLine.copy(previouslyReported = BigDecimal.TEN),
            external = dummyCostCategoryLine.copy(currentReport = BigDecimal.TEN),
            equipment = dummyCostCategoryLine.copy(totalReportedSoFar = BigDecimal.TEN),
            infrastructure = dummyCostCategoryLine.copy(totalReportedSoFarPercentage = BigDecimal.TEN),
            other = dummyCostCategoryLine.copy(remainingBudget = BigDecimal.TEN),
            lumpSum = dummyCostCategoryLine.copy(totalEligibleBudget = BigDecimal.TEN),
            unitCost = dummyCostCategoryLine.copy(previouslyReported = BigDecimal.TEN),
            total = dummyCostCategoryLine.copy(currentReport = BigDecimal.TEN),
        )

        private val dummyCostCategoryLineData = ExpenditureCostCategoryBreakdownLineData(
            flatRate = 50,
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            currentReport = BigDecimal.valueOf(3),
            totalReportedSoFar = BigDecimal.valueOf(4),
            totalReportedSoFarPercentage = BigDecimal.valueOf(5),
            remainingBudget = BigDecimal.valueOf(6),
        )

        private val expectedCostCategory = ExpenditureCostCategoryBreakdownData(
            staff = dummyCostCategoryLineData.copy(flatRate = 1),
            office = dummyCostCategoryLineData.copy(totalEligibleBudget = BigDecimal.TEN),
            travel = dummyCostCategoryLineData.copy(previouslyReported = BigDecimal.TEN),
            external = dummyCostCategoryLineData.copy(currentReport = BigDecimal.TEN),
            equipment = dummyCostCategoryLineData.copy(totalReportedSoFar = BigDecimal.TEN),
            infrastructure = dummyCostCategoryLineData.copy(totalReportedSoFarPercentage = BigDecimal.TEN),
            other = dummyCostCategoryLineData.copy(remainingBudget = BigDecimal.TEN),
            lumpSum = dummyCostCategoryLineData.copy(totalEligibleBudget = BigDecimal.TEN),
            unitCost = dummyCostCategoryLineData.copy(previouslyReported = BigDecimal.TEN),
            total = dummyCostCategoryLineData.copy(currentReport = BigDecimal.TEN),
        )

        private val investmentLine = ExpenditureInvestmentBreakdownLine(
            reportInvestmentId = 845L,
            investmentId = 22L,
            investmentNumber = 1,
            workPackageNumber = 2,
            title = setOf(InputTranslation(SystemLanguage.EN, "investment title EN")),
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            currentReport = BigDecimal.valueOf(3),
            totalEligibleAfterControl = BigDecimal.valueOf(8),
            totalReportedSoFar = BigDecimal.valueOf(4),
            totalReportedSoFarPercentage = BigDecimal.valueOf(5),
            remainingBudget = BigDecimal.valueOf(6),
            previouslyReportedParked = BigDecimal.ZERO,
            currentReportReIncluded = BigDecimal.ZERO,
            deactivated = false,
        )

        private val investment = ExpenditureInvestmentBreakdown(
            investments = listOf(investmentLine),
            total = investmentLine.copy(investmentNumber = 4),
        )

        private val investmentLineData = ExpenditureInvestmentBreakdownLineData(
            reportInvestmentId = 845L,
            investmentId = 22L,
            investmentNumber = 1,
            workPackageNumber = 2,
            title = setOf(InputTranslationData(SystemLanguageData.EN, "investment title EN")),
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            currentReport = BigDecimal.valueOf(3),
            totalReportedSoFar = BigDecimal.valueOf(4),
            totalReportedSoFarPercentage = BigDecimal.valueOf(5),
            remainingBudget = BigDecimal.valueOf(6),
        )

        private val expectedInvestment = ExpenditureInvestmentBreakdownData(
            investments = listOf(investmentLineData),
            total = investmentLineData.copy(investmentNumber = 4),
        )

        private val lineLumpSum = ExpenditureLumpSumBreakdownLine(
            reportLumpSumId = 36L,
            lumpSumId = 945L,
            name = setOf(InputTranslation(SystemLanguage.EN, "some lump sum 36 (or 945)")),
            period = 4,
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            previouslyPaid = BigDecimal.valueOf(3),
            currentReport = BigDecimal.valueOf(4),
            totalEligibleAfterControl = BigDecimal.valueOf(8),
            totalReportedSoFar = BigDecimal.valueOf(5),
            totalReportedSoFarPercentage = BigDecimal.valueOf(6),
            remainingBudget = BigDecimal.valueOf(7),
            previouslyReportedParked = BigDecimal.valueOf(1000),
            currentReportReIncluded = BigDecimal.valueOf(100)
        )

        private val lumpSum = ExpenditureLumpSumBreakdown(
            lumpSums = listOf(lineLumpSum),
            total = lineLumpSum.copy(remainingBudget = BigDecimal.TEN),
        )

        private val lineLumpSumData = ExpenditureLumpSumBreakdownLineData(
            reportLumpSumId = 36L,
            lumpSumId = 945L,
            name = setOf(InputTranslationData(SystemLanguageData.EN, "some lump sum 36 (or 945)")),
            period = 4,
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            previouslyPaid = BigDecimal.valueOf(3),
            currentReport = BigDecimal.valueOf(4),
            totalReportedSoFar = BigDecimal.valueOf(5),
            totalReportedSoFarPercentage = BigDecimal.valueOf(6),
            remainingBudget = BigDecimal.valueOf(7),
        )

        private val expectedLumpSum = ExpenditureLumpSumBreakdownData(
            lumpSums = listOf(lineLumpSumData),
            total = lineLumpSumData.copy(remainingBudget = BigDecimal.TEN),
        )

        private val lineUnitCost = ExpenditureUnitCostBreakdownLine(
            reportUnitCostId = 44L,
            unitCostId = 945L,
            name = setOf(InputTranslation(SystemLanguage.EN, "some unit cost 44 (or 945)")),
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            currentReport = BigDecimal.valueOf(3),
            totalEligibleAfterControl = BigDecimal.valueOf(8),
            totalReportedSoFar = BigDecimal.valueOf(4),
            totalReportedSoFarPercentage = BigDecimal.valueOf(5),
            remainingBudget = BigDecimal.valueOf(6),
            previouslyReportedParked = BigDecimal.ZERO,
            currentReportReIncluded = BigDecimal.ZERO
        )

        private val unitCost = ExpenditureUnitCostBreakdown(
            unitCosts = listOf(lineUnitCost),
            total = lineUnitCost.copy(remainingBudget = BigDecimal.TEN),
        )

        private val lineUnitCostData = ExpenditureUnitCostBreakdownLineData(
            reportUnitCostId = 44L,
            unitCostId = 945L,
            name = setOf(InputTranslationData(SystemLanguageData.EN, "some unit cost 44 (or 945)")),
            totalEligibleBudget = BigDecimal.valueOf(1),
            previouslyReported = BigDecimal.valueOf(2),
            currentReport = BigDecimal.valueOf(3),
            totalReportedSoFar = BigDecimal.valueOf(4),
            totalReportedSoFarPercentage = BigDecimal.valueOf(5),
            remainingBudget = BigDecimal.valueOf(6),
        )

        private val expectedUnitCost = ExpenditureUnitCostBreakdownData(
            unitCosts = listOf(lineUnitCostData),
            total = lineUnitCostData.copy(remainingBudget = BigDecimal.TEN),
        )

        private val procurement = ProjectPartnerReportProcurement(
            id = 265,
            reportId = 86L,
            reportNumber = 1,
            createdInThisReport = false,
            lastChanged = DATE_TIME_1,
            contractName = "contractName 265",
            referenceNumber = "referenceNumber 100",
            contractDate = DATE_1,
            contractType = "contractType 265",
            contractAmount = BigDecimal.TEN,
            currencyCode = "PLN",
            supplierName = "supplierName 265",
            vatNumber = "vat number 265",
            comment = "comment 265",
        )

        private val procurements: Page<ProjectPartnerReportProcurement> = PageImpl(listOf(procurement))

        private val procurementData = ProjectPartnerReportProcurementData(
            id = 265,
            reportId = 86L,
            reportNumber = 1,
            createdInThisReport = false,
            lastChanged = DATE_TIME_1,
            contractName = "contractName 265",
            referenceNumber = "referenceNumber 100",
            contractDate = DATE_1,
            contractType = "contractType 265",
            contractAmount = BigDecimal.TEN,
            currencyCode = "PLN",
            supplierName = "supplierName 265",
            vatNumber = "vat number 265",
            comment = "comment 265",
        )

        private val attachment = ProjectReportProcurementFile(
            id = 270,
            reportId = 83L,
            createdInThisReport = false,
            name = "name 270",
            type = JemsFileType.ProcurementAttachment,
            uploaded = DATE_TIME_1,
            author = UserSimple(45L, "dummy@email", name = "Dummy", surname = "Surname"),
            size = 653245L,
            description = "desc 270"
        )

        private val attachmentData = ProjectReportProcurementFileData(
            id = 270,
            reportId = 83L,
            createdInThisReport = false,
            name = "name 270",
            uploaded = DATE_TIME_1,
            size = 653245L,
            description = "desc 270",
        )

        private val beneficial = ProjectPartnerReportProcurementBeneficialOwner(
            id = 14L,
            reportId = 82L,
            createdInThisReport = false,
            firstName = "firstName",
            lastName = "lastName",
            birth = DATE_1,
            vatNumber = "vatNumber",
        )

        private val beneficialData = ProjectPartnerReportProcurementBeneficialOwnerData(
            id = 14L,
            reportId = 82L,
            createdInThisReport = false,
            firstName = "firstName",
            lastName = "lastName",
            birth = DATE_1,
            vatNumber = "vatNumber",
        )

        private val subcontract = ProjectPartnerReportProcurementSubcontract(
            id = 275,
            reportId = 81L,
            createdInThisReport = false,
            contractName = "firstName 275",
            referenceNumber = "referenceNumber 275",
            contractDate = DATE_2,
            contractAmount = BigDecimal.ONE,
            currencyCode = "PLN",
            supplierName = "supplierName 275",
            vatNumber = "vatNumber 275",
        )

        private val subcontractData = ProjectPartnerReportProcurementSubcontractData(
            id = 275,
            reportId = 81L,
            createdInThisReport = false,
            contractName = "firstName 275",
            referenceNumber = "referenceNumber 275",
            contractDate = DATE_2,
            contractAmount = BigDecimal.ONE,
            currencyCode = "PLN",
            supplierName = "supplierName 275",
            vatNumber = "vatNumber 275",
        )

        private val workPlan = ProjectPartnerReportWorkPackage(
            id = 750,
            number = 3,
            description = setOf(InputTranslation(SystemLanguage.EN, "WP1")),
            activities = listOf(
                ProjectPartnerReportWorkPackageActivity(
                    id = 7501,
                    number = 1,
                    title = setOf(InputTranslation(SystemLanguage.EN, "A1.1")),
                    progress = setOf(InputTranslation(SystemLanguage.EN, "custom title")),
                    deliverables = listOf(
                        ProjectPartnerReportWorkPackageActivityDeliverable(
                            id = 75013,
                            number = 3,
                            title = setOf(InputTranslation(SystemLanguage.EN, "D1.1.3")),
                            contribution = true,
                            evidence = false,
                            attachment = JemsFileMetadata(
                                id = 980L,
                                name = "cat.gif",
                                uploaded = DATE_TIME_1,
                            ),
                        ),
                    ),
                    attachment = JemsFileMetadata(id = 990L, name = "cat-2.docx", uploaded = DATE_TIME_1),
                ),
            ),
            outputs = listOf(
                ProjectPartnerReportWorkPackageOutput(
                    id = 757,
                    number = 1,
                    title = setOf(InputTranslation(SystemLanguage.EN, "O1")),
                    contribution = true,
                    evidence = false,
                    attachment = null,
                    deactivated = false,
                ),
            ),
        )

        private val workPlanData = ProjectPartnerReportWorkPackageData(
            id = 750,
            number = 3,
            description = setOf(InputTranslationData(SystemLanguageData.EN, "WP1")),
            activities = listOf(
                ProjectPartnerReportWorkPackageActivityData(
                    id = 7501,
                    number = 1,
                    title = setOf(InputTranslationData(SystemLanguageData.EN, "A1.1")),
                    progress = setOf(InputTranslationData(SystemLanguageData.EN, "custom title")),
                    deliverables = listOf(
                        ProjectPartnerReportWorkPackageActivityDeliverableData(
                            id = 75013,
                            number = 3,
                            title = setOf(InputTranslationData(SystemLanguageData.EN, "D1.1.3")),
                            contribution = true,
                            evidence = false,
                            attachment = JemsFileMetadataData(
                                id = 980L,
                                name = "cat.gif",
                                uploaded = DATE_TIME_1,
                            ),
                        ),
                    ),
                    attachment = JemsFileMetadataData(id = 990L, name = "cat-2.docx", uploaded = DATE_TIME_1),
                ),
            ),
            outputs = listOf(
                ProjectPartnerReportWorkPackageOutputData(
                    id = 757,
                    number = 1,
                    title = setOf(InputTranslationData(SystemLanguageData.EN, "O1")),
                    contribution = true,
                    evidence = false,
                    attachment = null,
                ),
            ),
        )

    }

    @MockK
    private lateinit var reportPersistence: ProjectPartnerReportPersistence
    @MockK
    private lateinit var serviceIdentification: GetProjectPartnerReportIdentificationService
    @MockK
    private lateinit var reportContributionPersistence: ProjectPartnerReportContributionPersistence
    @MockK
    private lateinit var calculatorExpenditure: GetProjectPartnerReportExpenditureCalculator
    @MockK
    private lateinit var calculatorCoFinancing: GetReportExpenditureCoFinancingBreakdownCalculator
    @MockK
    private lateinit var calculatorCostCategory: GetReportExpenditureCostCategoryCalculatorService
    @MockK
    private lateinit var calculatorInvestment: GetReportExpenditureInvestmentsBreakdownCalculator
    @MockK
    private lateinit var calculatorLumpSum: GetReportExpenditureLumpSumBreakdownCalculator
    @MockK
    private lateinit var calculatorUnitCost: GetReportExpenditureUnitCostBreakdownCalculator
    @MockK
    private lateinit var reportProcurementPersistence: ProjectPartnerReportProcurementPersistence
    @MockK
    private lateinit var serviceProcurementAttachment: GetProjectPartnerReportProcurementAttachmentService
    @MockK
    lateinit var serviceProcurementBeneficial: GetProjectPartnerReportProcurementBeneficialService
    @MockK
    private lateinit var serviceProcurementSubcontract: GetProjectPartnerReportProcurementSubcontractService
    @MockK
    private lateinit var reportWorkPlanPersistence: ProjectPartnerReportWorkPlanPersistence

    @InjectMockKs
    private lateinit var dataProvider: ReportPartnerDataProviderImpl

    @Test
    fun get() {
        every { reportPersistence.getPartnerReportById(partnerId = 21L, reportId = 96L) } returns report
        assertThat(dataProvider.get(21L, reportId = 96L)).isEqualTo(expectedReport)
    }

    @Test
    fun getIdentification() {
        every { serviceIdentification.getIdentification(partnerId = 23L, reportId = 95L) } returns reportIdentification
        assertThat(dataProvider.getIdentification(23L, reportId = 95L)).isEqualTo(expectedReportIdentification)
    }

    @Test
    fun getContribution() {
        every { reportContributionPersistence.getPartnerReportContribution(partnerId = 25L, reportId = 94L) } returns listOf(contribution)
        assertThat(dataProvider.getContribution(25L, reportId = 94L)).isEqualTo(expectedContribution)
    }

    @Test
    fun getExpenditureCosts() {
        every { calculatorExpenditure.getExpenditureCosts(partnerId = 27L, reportId = 93L) } returns listOf(expenditure)
        assertThat(dataProvider.getExpenditureCosts(27L, reportId = 93L)).containsExactly(expectedExpenditure)
    }

    @Test
    fun getCoFinancingOverview() {
        every { calculatorCoFinancing.get(partnerId = 29L, reportId = 92L) } returns coFinancing
        assertThat(dataProvider.getCoFinancingOverview(29L, reportId = 92L)).isEqualTo(expectedCoFinancing)
    }

    @Test
    fun getCostCategoryOverview() {
        every { calculatorCostCategory.getSubmittedOrCalculateCurrent(partnerId = 31L, reportId = 91L) } returns costCategory
        assertThat(dataProvider.getCostCategoryOverview(31L, reportId = 91L)).isEqualTo(expectedCostCategory)
    }

    @Test
    fun getInvestmentOverview() {
        every { calculatorInvestment.get(partnerId = 33L, reportId = 90L) } returns investment
        assertThat(dataProvider.getInvestmentOverview(33L, reportId = 90L)).isEqualTo(expectedInvestment)
    }

    @Test
    fun getLumpSumOverview() {
        every { calculatorLumpSum.get(partnerId = 35L, reportId = 89L) } returns lumpSum
        assertThat(dataProvider.getLumpSumOverview(35L, reportId = 89L)).isEqualTo(expectedLumpSum)
    }

    @Test
    fun getUnitCostOverview() {
        every { calculatorUnitCost.get(partnerId = 37L, reportId = 88L) } returns unitCost
        assertThat(dataProvider.getUnitCostOverview(37L, reportId = 88L)).isEqualTo(expectedUnitCost)
    }

    @Test
    fun `getProcurementList - empty`() {
        every { reportPersistence.exists(partnerId = 39L, reportId = -1L) } returns false
        assertThat(dataProvider.getProcurementList(39L, reportId = -1L)).isEmpty()
    }

    @Test
    fun getProcurementList() {
        every { reportPersistence.exists(partnerId = 41L, reportId = 86L) } returns true
        every { reportPersistence.getReportIdsBefore(partnerId = 41L, beforeReportId = 86L) } returns setOf(1L)
        val pageableSlot = slot<PageRequest>()
        every { reportProcurementPersistence.getProcurementsForReportIds(setOf(86L, 1L), capture(pageableSlot)) } returns procurements

        assertThat(dataProvider.getProcurementList(41L, reportId = 86L))
            .containsExactly(procurementData)
    }

    @Test
    fun `getProcurementById - empty`() {
        every { reportPersistence.exists(partnerId = 43L, reportId = -1L) } returns false
        assertThat(dataProvider.getProcurementById(43L, reportId = -1L, procurementId = 0L)).isNull()
    }

    @Test
    fun getProcurementById() {
        every { reportPersistence.exists(partnerId = 45L, reportId = 84L) } returns true
        every { reportProcurementPersistence.getById(partnerId = 45L, procurementId = 5L) } returns
            procurement.copy(createdInThisReport = false, reportId = 84L)

        assertThat(dataProvider.getProcurementById(45L, reportId = 84L, procurementId = 5L))
            .isEqualTo(procurementData.copy(createdInThisReport = true, reportId = 84L))
    }

    @Test
    fun getProcurementAttachment() {
        every { serviceProcurementAttachment.getAttachment(partnerId = 47L, reportId = 83L, procurementId = 6L) } returns
            listOf(attachment)

        assertThat(dataProvider.getProcurementAttachment(partnerId = 47L, reportId = 83L, procurementId = 6L))
            .containsExactly(attachmentData)
    }

    @Test
    fun getProcurementBeneficialOwner() {
        every { serviceProcurementBeneficial.getBeneficialOwner(partnerId = 49L, reportId = 82L, procurementId = 7L) } returns
            listOf(beneficial)

        assertThat(dataProvider.getProcurementBeneficialOwner(partnerId = 49L, reportId = 82L, procurementId = 7L))
            .containsExactly(beneficialData)
    }

    @Test
    fun getProcurementSubcontract() {
        every { serviceProcurementSubcontract.getSubcontract(partnerId = 49L, reportId = 82L, procurementId = 7L) } returns
            listOf(subcontract)

        assertThat(dataProvider.getProcurementSubcontract(partnerId = 49L, reportId = 82L, procurementId = 7L))
            .containsExactly(subcontractData)
    }

    @Test
    fun getWorkPlan() {
        every { reportWorkPlanPersistence.getPartnerReportWorkPlanById(partnerId = 51L, reportId = 81L) } returns listOf(workPlan)
        assertThat(dataProvider.getWorkPlan(partnerId = 51L, reportId = 81L)).containsExactly(workPlanData)
    }
}
