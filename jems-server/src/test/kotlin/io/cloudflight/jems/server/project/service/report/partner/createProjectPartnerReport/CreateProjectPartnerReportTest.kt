package io.cloudflight.jems.server.project.service.report.partner.createProjectPartnerReport

import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingFundTypeDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.model.ProjectTargetGroup
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.ProjectPartnerCoFinancingPersistence
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancing
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerVatRecovery
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.PartnerReportIdentificationCreate
import io.cloudflight.jems.server.project.service.report.model.ProjectPartnerReportCreate
import io.cloudflight.jems.server.project.service.report.model.ReportStatus
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import java.math.BigDecimal

internal class CreateProjectPartnerReportTest : UnitTest() {

    companion object {
        private const val PROJECT_ID = 426L

        private fun projectSummary(status: ApplicationStatus) = ProjectSummary(
            id = PROJECT_ID,
            customIdentifier = "XE.1_0001",
            callName = "",
            acronym = "project acronym",
            status = status,
        )

        private fun partnerDetail(id: Long) = ProjectPartnerDetail(
            projectId = PROJECT_ID,
            id = id,
            active = true,
            abbreviation = "abbr",
            role = ProjectPartnerRole.PARTNER,
            sortNumber = 4,
            nameInOriginalLanguage = "name in orig",
            nameInEnglish = "name in eng",
            partnerType = ProjectTargetGroup.SectoralAgency,
            partnerSubType = null,
            nace = null,
            otherIdentifierNumber = null,
            pic = "123456789",
            legalStatusId = 697854L,
            vat = "",
            vatRecovery = ProjectPartnerVatRecovery.Yes,
        )

        private val coFinancing = listOf(
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.MainFund,
                fund = ProgrammeFund(
                    id = 7748L,
                    selected = true,
                    type = ProgrammeFundType.ERDF,
                ),
                percentage = BigDecimal.ONE,
            ),
            ProjectPartnerCoFinancing(
                fundType = ProjectPartnerCoFinancingFundTypeDTO.PartnerContribution,
                fund = null,
                percentage = BigDecimal.TEN,
            ),
        )

        private fun expectedCreationObject(partnerId: Long) = ProjectPartnerReportCreate(
            partnerId = partnerId,
            reportNumber = 7 + 1,
            status = ReportStatus.Draft,
            version = "14.2.0",
            identification = PartnerReportIdentificationCreate(
                projectIdentifier = "XE.1_0001",
                projectAcronym = "project acronym",
                partnerNumber = 4,
                partnerAbbreviation = "abbr",
                partnerRole = ProjectPartnerRole.PARTNER,
                nameInOriginalLanguage = "name in orig",
                nameInEnglish = "name in eng",
                legalStatusId = 697854L,
                partnerType = ProjectTargetGroup.SectoralAgency,
                vatRecovery = ProjectPartnerVatRecovery.Yes,
                coFinancing = coFinancing,
            )
        )
    }

    @MockK
    lateinit var versionPersistence: ProjectVersionPersistence
    @MockK
    lateinit var projectPersistence: ProjectPersistence
    @MockK
    lateinit var projectPartnerPersistence: PartnerPersistence
    @MockK
    lateinit var partnerCoFinancingPersistence: ProjectPartnerCoFinancingPersistence
    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @InjectMockKs
    lateinit var createReport: CreateProjectPartnerReport

    @ParameterizedTest(name = "can create report when status {0}")
    @EnumSource(value = ApplicationStatus::class, names = ["CONTRACTED", "IN_MODIFICATION", "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED"])
    fun createReportFor(status: ApplicationStatus) {
        val partnerId = 66L
        every { projectPartnerPersistence.getProjectIdForPartnerId(partnerId) } returns PROJECT_ID
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary(status)
        every { versionPersistence.getLatestApprovedOrCurrent(PROJECT_ID) } returns "14.2.0"
        every { reportPersistence.getCurrentLatestReportNumberForPartner(partnerId) } returns 7
        every { partnerCoFinancingPersistence.getCoFinancingAndContributions(partnerId, "14.2.0").finances } returns coFinancing
        every { projectPartnerPersistence.getById(partnerId, "14.2.0") } returns partnerDetail(partnerId)

        val slotReport = slot<ProjectPartnerReportCreate>()
        every { reportPersistence.createPartnerReport(capture(slotReport)) } returns mockk()

        createReport.createReportFor(partnerId)

        assertThat(slotReport.captured).isEqualTo(expectedCreationObject(partnerId))
    }

    @ParameterizedTest(name = "cannot create report when status {0}")
    @EnumSource(
        value = ApplicationStatus::class,
        names = ["CONTRACTED", "IN_MODIFICATION", "MODIFICATION_SUBMITTED", "MODIFICATION_REJECTED"],
        mode = EnumSource.Mode.EXCLUDE,
    )
    fun cannotCreateReportFor(status: ApplicationStatus) {
        val partnerId = 67L
        every { projectPartnerPersistence.getProjectIdForPartnerId(partnerId) } returns PROJECT_ID
        every { projectPersistence.getProjectSummary(PROJECT_ID) } returns projectSummary(status)

        assertThrows<ReportCanBeCreatedOnlyWhenContractedException> { createReport.createReportFor(partnerId) }
    }

}
