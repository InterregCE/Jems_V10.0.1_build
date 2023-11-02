package io.cloudflight.jems.server.project.service.auditAndControl.correction.programmeMeasure.update

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.repository.auditAndControl.correction.programmeMeasure.CorrectionProgrammeMeasurePersistenceProvider
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureUpdate
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateProgrammeMeasureTest : UnitTest() {

    companion object {
        private const val CORRECTION_ID = 176L

        private val programmeMeasureModel = ProjectCorrectionProgrammeMeasure(
            correctionId = CORRECTION_ID,
            scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_5,
            comment = "comment",
            includedInAccountingYear = null,
        )
    }

    @MockK
    lateinit var programmeMeasurePersistenceProvider: CorrectionProgrammeMeasurePersistenceProvider

    @InjectMockKs
    lateinit var interactor: UpdateProgrammeMeasure

    @BeforeEach
    fun setup() {
        clearMocks(programmeMeasurePersistenceProvider)
    }

    @Test
    fun updateProgrammeMeasure() {
        val programmeMeasureUpdate = ProjectCorrectionProgrammeMeasureUpdate(
            ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3,
            "newComment"
        )
        every { programmeMeasurePersistenceProvider.updateProgrammeMeasure(CORRECTION_ID, programmeMeasureUpdate) } returns
                programmeMeasureModel.copy(scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3, comment = "newComment")

        assertThat(interactor.update(correctionId = CORRECTION_ID, programmeMeasure = programmeMeasureUpdate))
            .isEqualTo(programmeMeasureModel.copy(scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3, comment = "newComment"))
    }
}
