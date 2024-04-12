package io.cloudflight.jems.server.payments.service.account.finance.correction.getAvailableClosedCorrections

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.payments.model.account.PaymentAccount
import io.cloudflight.jems.server.payments.model.account.finance.correction.PaymentAccountCorrectionLinking
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.AuditControlStatus
import io.cloudflight.jems.server.project.service.auditAndControl.model.ControllingBody
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrection
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.AuditControlCorrectionType
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal

class GetAvailableClosedCorrectionsForPaymentAccountTest : UnitTest() {

    companion object {
        val paymentAccount = mockk<PaymentAccount>()

        val paymentCorrectionLink = PaymentAccountCorrectionLinking(
            correction = AuditControlCorrection(
                id = 1716,
                orderNr = 1654,
                status = AuditControlStatus.Closed,
                type = AuditControlCorrectionType.LinkedToCostOption,
                auditControlId = 8780,
                auditControlNr = 6587
            ),
            projectId = 8144,
            projectAcronym = "porta",
            projectCustomIdentifier = "oporteat",
            priorityAxis = "invenire",
            controllingBody = ControllingBody.AA,
            scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3,
            paymentAccountId = 20L,
            fundAmount = BigDecimal.ZERO,
            partnerContribution = BigDecimal.ZERO,
            publicContribution = BigDecimal.ZERO,
            correctedPublicContribution = BigDecimal.ZERO,
            autoPublicContribution = BigDecimal.ZERO,
            correctedAutoPublicContribution = BigDecimal.ZERO,
            privateContribution = BigDecimal.ZERO,
            correctedPrivateContribution = BigDecimal.ZERO,
            comment = null

        )
    }

    @MockK
    lateinit var paymentAccountCorrectionsService: PaymentAccountCorrectionsService

    @InjectMockKs
    lateinit var interactor: GetAvailableClosedCorrectionsForPaymentAccount

    @Test
    fun getCorrectionList() {
        every {
            paymentAccountCorrectionsService.getClosedCorrections(
                Pageable.unpaged(),
                20L
            )
        } returns PageImpl(listOf(paymentCorrectionLink))

        assertThat(interactor.getClosedCorrections(pageable = Pageable.unpaged(), paymentAccountId = 20L))
            .isEqualTo(
                PageImpl(
                    listOf(
                        PaymentAccountCorrectionLinking(
                            correction = AuditControlCorrection(
                                id = 1716,
                                orderNr = 1654,
                                status = AuditControlStatus.Closed,
                                type = AuditControlCorrectionType.LinkedToCostOption,
                                auditControlId = 8780,
                                auditControlNr = 6587
                            ),
                            projectId = 8144,
                            projectAcronym = "porta",
                            projectCustomIdentifier = "oporteat",
                            priorityAxis = "invenire",
                            controllingBody = ControllingBody.AA,
                            scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3,
                            paymentAccountId = 20L,
                            fundAmount = BigDecimal.ZERO,
                            partnerContribution = BigDecimal.ZERO,
                            publicContribution = BigDecimal.ZERO,
                            correctedPublicContribution = BigDecimal.ZERO,
                            autoPublicContribution = BigDecimal.ZERO,
                            correctedAutoPublicContribution = BigDecimal.ZERO,
                            privateContribution = BigDecimal.ZERO,
                            correctedPrivateContribution = BigDecimal.ZERO,
                            comment = null
                        )
                    )
                )
            )
    }

}
