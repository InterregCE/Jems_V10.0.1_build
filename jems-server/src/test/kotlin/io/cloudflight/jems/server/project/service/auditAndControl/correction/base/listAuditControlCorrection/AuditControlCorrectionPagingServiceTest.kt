package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.listAuditControlCorrection

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.tmpModel.AuditControlCorrectionLineTmp
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.AuditControlPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.correction.AuditControlCorrectionPersistence
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionLine
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpactAction
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerDetail
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class AuditControlCorrectionPagingServiceTest: UnitTest() {
    private val correctionLine = AuditControlCorrectionLineTmp(
        correction = AuditControlCorrection(
            id = 42L,
            orderNr = 2,
            status = AuditControlStatus.Ongoing,
            type = AuditControlCorrectionType.LinkedToCostOption,
            auditControlId = 18L,
            auditControlNr = 8,
        ),
        partnerId = 300L,
        lumpSumPartnerId = null,
        partnerNumber = 5,
        partnerAbbreviation = "partner abbr",
        partnerRole = ProjectPartnerRole.LEAD_PARTNER,
        reportNr = 9,
        lumpSumOrderNr = null,
        followUpAuditNr = 11,
        followUpCorrectionNr = 13,
        fund = ProgrammeFund(
            id = 622L,
            selected = true,
            type = ProgrammeFundType.NEIGHBOURHOOD_CBC,
            abbreviation = setOf(InputTranslation(SystemLanguage.GA, "abbr-GA")),
            description = setOf(InputTranslation(SystemLanguage.FI, "desc-FI")),
        ),
        fundAmount = BigDecimal.valueOf(61L),
        publicContribution = BigDecimal.valueOf(62L),
        autoPublicContribution = BigDecimal.valueOf(63L),
        privateContribution = BigDecimal.valueOf(64L),
        impactProjectLevel = AuditControlCorrectionImpactAction.AdjustmentInNextPayment,
        scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_2,
    )

    @MockK
    private lateinit var auditControlPersistence: AuditControlPersistence
    @MockK
    private lateinit var versionPersistence: ProjectVersionPersistence
    @MockK
    private lateinit var partnerPersistence: PartnerPersistence
    @MockK
    private lateinit var auditControlCorrectionPersistence: AuditControlCorrectionPersistence

    @InjectMockKs
    private lateinit var service: AuditControlCorrectionPagingService

    @Test
    fun listCorrections() {
        val projectId = 96L
        every { auditControlPersistence.getProjectIdForAuditControl(auditControlId = 18L) } returns projectId
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns "V4P45"

        val partner = mockk<ProjectPartnerDetail>()
        every { partner.active } returns false
        every { partner.id } returns 300L
        every { partnerPersistence.findTop50ByProjectId(projectId, "V4P45") } returns listOf(partner)

        every {
            auditControlCorrectionPersistence.getAllCorrectionsByAuditControlId(18L, Pageable.unpaged())
        } returns PageImpl(listOf(correctionLine))

        Assertions.assertThat(service.listCorrections(18L, Pageable.unpaged())).containsExactly(
            AuditControlCorrectionLine(
                id = 42L,
                orderNr = 2,
                status = AuditControlStatus.Ongoing,
                type = AuditControlCorrectionType.LinkedToCostOption,
                auditControlId = 18L,
                auditControlNr = 8,
                canBeDeleted = true,
                partnerReport = 9,
                partnerRole = ProjectPartnerRole.LEAD_PARTNER,
                partnerId = 300,
                partnerNumber = 5,
                partnerDisabled = true,
                lumpSumOrderNr = null,
                followUpAuditNr = 11,
                followUpCorrectionNr = 13,
                fund = ProgrammeFund(
                    id = 622L,
                    selected = true,
                    type = ProgrammeFundType.NEIGHBOURHOOD_CBC,
                    abbreviation = setOf(InputTranslation(SystemLanguage.GA, "abbr-GA")),
                    description = setOf(InputTranslation(SystemLanguage.FI, "desc-FI")),
                ),
                fundAmount = BigDecimal.valueOf(61L),
                publicContribution = BigDecimal.valueOf(62L),
                autoPublicContribution = BigDecimal.valueOf(63L),
                privateContribution = BigDecimal.valueOf(64L),
                total = BigDecimal.valueOf(250L),
                impactProjectLevel = AuditControlCorrectionImpactAction.AdjustmentInNextPayment,
                scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_2,
            )
        )
    }
}
