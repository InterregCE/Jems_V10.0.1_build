package io.cloudflight.jems.server.project.controller.auditAndControl.correction.measure

import io.cloudflight.jems.api.project.dto.auditAndControl.correction.measure.ProjectCorrectionProgrammeMeasureDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.measure.ProjectCorrectionProgrammeMeasureScenarioDTO
import io.cloudflight.jems.api.project.dto.auditAndControl.correction.measure.ProjectCorrectionProgrammeMeasureUpdateDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.AuditControlCorrectionMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.model.correction.measure.AuditControlCorrectionMeasureUpdate
import io.cloudflight.jems.server.project.service.auditAndControl.correction.measure.get.GetProgrammeMeasureInteractor
import io.cloudflight.jems.server.project.service.auditAndControl.correction.measure.update.UpdateProgrammeMeasureInteractor
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuditControlCorrectionMeasureControllerTest : UnitTest() {

    companion object {
        private const val CORRECTION_ID = 176L

        private val programmeMeasureModel = AuditControlCorrectionMeasure(
            correctionId = CORRECTION_ID,
            scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_5,
            comment = "comment",
            includedInAccountingYear = null,
        )

        private val programmeMeasureDTO = ProjectCorrectionProgrammeMeasureDTO(
            correctionId = CORRECTION_ID,
            scenario = ProjectCorrectionProgrammeMeasureScenarioDTO.SCENARIO_5,
            comment = "comment",
            includedInAccountingYear = null,
        )


        private val programmeMeasureUpdateModel = AuditControlCorrectionMeasureUpdate(
            ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3,
            "newComment"
        )

        private val programmeMeasureUpdateDTO = ProjectCorrectionProgrammeMeasureUpdateDTO(
            ProjectCorrectionProgrammeMeasureScenarioDTO.SCENARIO_3,
            "newComment"
        )
    }

    @MockK
    lateinit var getProgrammeMeasure: GetProgrammeMeasureInteractor

    @MockK
    lateinit var updateProgrammeMeasure: UpdateProgrammeMeasureInteractor

    @InjectMockKs
    lateinit var controller: AuditControlCorrectionMeasureController

    @BeforeEach
    fun setup() {
        clearMocks(getProgrammeMeasure, updateProgrammeMeasure)
    }

    @Test
    fun getProgrammeMeasure() {
        every { getProgrammeMeasure.get(CORRECTION_ID) } returns programmeMeasureModel

        assertThat(controller.getProgrammeMeasure(1L ,2L, CORRECTION_ID)).isEqualTo(programmeMeasureDTO)
    }

    @Test
    fun updateProgrammeMeasure() {
        every { updateProgrammeMeasure.update(CORRECTION_ID, programmeMeasureUpdateModel) } returns
                programmeMeasureModel.copy(scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3, comment = "newComment")

        assertThat(controller.updateProgrammeMeasure(1L, 2L, CORRECTION_ID, programmeMeasureUpdateDTO))
            .isEqualTo(programmeMeasureDTO.copy(scenario = ProjectCorrectionProgrammeMeasureScenarioDTO.SCENARIO_3, comment = "newComment"))
    }
}
