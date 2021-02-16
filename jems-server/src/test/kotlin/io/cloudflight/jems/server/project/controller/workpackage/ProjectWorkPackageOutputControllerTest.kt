package io.cloudflight.jems.server.project.controller.workpackage

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.CS
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.workpackage.output.WorkPackageOutputDTO
import io.cloudflight.jems.server.project.service.workpackage.output.get_work_package_output.GetWorkPackageOutputInteractor
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutputTranslatedValue
import io.cloudflight.jems.server.project.service.workpackage.output.update_work_package_output.UpdateWorkPackageOutputInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ProjectWorkPackageOutputControllerTest {

    companion object {
        val output1 = WorkPackageOutput(
            outputNumber = 1,
            translatedValues = setOf(
                WorkPackageOutputTranslatedValue(language = EN, title = null, description = "en_desc"),
                WorkPackageOutputTranslatedValue(language = CS, title = "", description = null),
                WorkPackageOutputTranslatedValue(language = SK, title = "sk_title", description = "sk_desc"),
            ),
            periodNumber = 1,
            programmeOutputIndicatorId = 50L,
            targetValue = "target_value",
        )
        val output2 = WorkPackageOutput(
            outputNumber = 2,
            translatedValues = emptySet(),
            periodNumber = 3,
        )

    }

    @MockK
    lateinit var getOutputInteractor: GetWorkPackageOutputInteractor

    @MockK
    lateinit var updateOutputInteractor: UpdateWorkPackageOutputInteractor

    @InjectMockKs
    private lateinit var controller: ProjectWorkPackageOutputController

    @Test
    fun getOutputs() {
        every { getOutputInteractor.getOutputsForWorkPackage(1L) } returns listOf(output1, output2)

        assertThat(controller.getOutputs(1L)).containsExactly(
            WorkPackageOutputDTO(
                outputNumber = 1,
                title = setOf(InputTranslation(SK, "sk_title")),
                periodNumber = 1,
                programmeOutputIndicatorId = 50L,
                description = setOf(InputTranslation(EN, "en_desc"), InputTranslation(SK, "sk_desc")),
                targetValue = "target_value",
            ),
            WorkPackageOutputDTO(
                outputNumber = 2,
                periodNumber = 3,
            ),
        )
    }

    @Test
    fun updateOutputs() {
        val outputsSlot = slot<List<WorkPackageOutput>>()
        // we test retrieval in getOutputs test
        every { updateOutputInteractor.updateOutputsForWorkPackage(1L, capture(outputsSlot)) } returnsArgument 1

        val outputDto1 = WorkPackageOutputDTO(
            title = setOf(InputTranslation(EN, null), InputTranslation(CS, ""), InputTranslation(SK, "sk_title")),
            periodNumber = 1,
            programmeOutputIndicatorId = 15,
            description = setOf(InputTranslation(EN, "en_desc"), InputTranslation(CS, ""), InputTranslation(SK, "sk_desc")),
            targetValue = "target value",
        )
        val outputDto2 = WorkPackageOutputDTO(
            periodNumber = 3,
        )

        controller.updateOutputs(1L, listOf(outputDto1, outputDto2))

        assertThat(outputsSlot.captured).containsExactly(
            WorkPackageOutput(
                outputNumber = 0,
                translatedValues = setOf(
                    WorkPackageOutputTranslatedValue(language = EN, title = null, description = "en_desc"),
                    WorkPackageOutputTranslatedValue(language = SK, title = "sk_title", description = "sk_desc"),
                ),
                periodNumber = 1,
                programmeOutputIndicatorId = 15,
                targetValue = "target value",
            ),
            WorkPackageOutput(
                outputNumber = 0,
                periodNumber = 3,
            )
        )
    }

}
