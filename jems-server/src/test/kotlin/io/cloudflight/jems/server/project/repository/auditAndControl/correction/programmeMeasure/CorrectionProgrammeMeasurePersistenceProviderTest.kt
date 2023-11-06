package io.cloudflight.jems.server.project.repository.auditAndControl.correction.programmeMeasure

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.auditAndControl.ProjectCorrectionProgrammeMeasureEntity
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasure
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureScenario
import io.cloudflight.jems.server.project.service.auditAndControl.correction.model.ProjectCorrectionProgrammeMeasureUpdate
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CorrectionProgrammeMeasurePersistenceProviderTest : UnitTest() {

    companion object {
        private const val CORRECTION_ID = 176L

        private val programmeMeasureEntity = ProjectCorrectionProgrammeMeasureEntity(
            correctionId = CORRECTION_ID,
            correctionEntity = mockk(),
            scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_5,
            comment = "comment"
        )

        private val programmeMeasureModel = ProjectCorrectionProgrammeMeasure(
            correctionId = CORRECTION_ID,
            scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_5,
            comment = "comment",
            includedInAccountingYear = null,
        )


    }

    @MockK
    lateinit var programmeMeasureRepository: CorrectionProgrammeMeasureRepository

    @InjectMockKs
    lateinit var persistenceProvider: CorrectionProgrammeMeasurePersistenceProvider

    @BeforeEach
    fun setup() {
        clearMocks(programmeMeasureRepository)
    }

    @Test
    fun getProgrammeMeasure() {
        every { programmeMeasureRepository.getByCorrectionId(CORRECTION_ID) } returns programmeMeasureEntity

        assertThat(persistenceProvider.getProgrammeMeasure(CORRECTION_ID)).isEqualTo(programmeMeasureModel)
    }

    @Test
    fun updateProgrammeMeasure() {
        val programmeMeasureUpdate = ProjectCorrectionProgrammeMeasureUpdate(
            ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3,
            "newComment"
        )

        every { programmeMeasureRepository.getByCorrectionId(CORRECTION_ID) } returns programmeMeasureEntity

        assertThat(persistenceProvider.updateProgrammeMeasure(CORRECTION_ID, programmeMeasureUpdate)).isEqualTo(
            programmeMeasureModel.copy(scenario = ProjectCorrectionProgrammeMeasureScenario.SCENARIO_3, comment = "newComment")
        )
    }
}
