package io.cloudflight.jems.server.project.service.auditAndControl.correction.base.listAuditControlCorrection

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionLine
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpactAction
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class ListAuditControlCorrectionTest : UnitTest() {

    companion object {
        val correctionsLine = AuditControlCorrectionLine(
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
    }

    @MockK
    lateinit var auditControlCorrectionPagingService: AuditControlCorrectionPagingService

    @InjectMockKs
    private lateinit var interactor: ListAuditControlCorrection

    @Test
    fun listCorrections() {

        every { auditControlCorrectionPagingService.listCorrections(auditControlId = 18L, Pageable.unpaged()) } returns PageImpl(listOf( correctionsLine))
        assertThat(interactor.listCorrections(18L, Pageable.unpaged())).containsExactly(
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
