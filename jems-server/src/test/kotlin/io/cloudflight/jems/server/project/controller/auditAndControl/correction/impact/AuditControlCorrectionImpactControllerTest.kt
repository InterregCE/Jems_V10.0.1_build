package io.cloudflight.jems.server.project.controller.auditAndControl.correction.impact

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.impact.AuditControlCorrectionImpactDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.impact.CorrectionImpactActionDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.correction.impact.updateImpact.UpdateAuditControlCorrectionImpactInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.AuditControlCorrectionImpact
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.impact.CorrectionImpactAction
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditControlCorrectionImpactControllerTest : UnitTest() {

    companion object {
        private const val CORRECTION_ID = 176L
    }

    @MockK
    private lateinit var updateImpact: UpdateAuditControlCorrectionImpactInteractor

    @InjectMockKs
    private lateinit var controller: AuditControlCorrectionImpactController

    @BeforeEach
    fun setup() {
        clearMocks(updateImpact)
    }

    @Test
    fun updateImpact() {
        val impact = AuditControlCorrectionImpactDTO(
            action = CorrectionImpactActionDTO.BudgetReduction,
            comment = "new comment",
        )

        val slotImpact = slot<AuditControlCorrectionImpact>()
        every { updateImpact.update(CORRECTION_ID, capture(slotImpact)) } returnsArgument 1

        assertThat(controller.updateImpact(-1L, -2L, CORRECTION_ID, impact))
            .isEqualTo(impact)

        assertThat(slotImpact.captured).isEqualTo(
            AuditControlCorrectionImpact(
                action = CorrectionImpactAction.BudgetReduction,
                comment = "new comment",
            )
        )
    }

}
