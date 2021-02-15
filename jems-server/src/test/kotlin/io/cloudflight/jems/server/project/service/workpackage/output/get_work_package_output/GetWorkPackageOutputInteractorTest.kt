package io.cloudflight.jems.server.project.service.workpackage.output.get_work_package_output

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutputTranslatedValue
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetWorkPackageOutputInteractorTest {

    companion object {


        private val testWorkPackageOutput1 = WorkPackageOutput(
            outputNumber = 1,
            translatedValues = setOf(WorkPackageOutputTranslatedValue(SystemLanguage.EN, "Test"))
        )

        private val testWorkPackageOutput2 = WorkPackageOutput(
            outputNumber = 2,
            translatedValues = setOf(WorkPackageOutputTranslatedValue(SystemLanguage.EN, "Test"))
        )

        private val workPackageOutputs = mutableListOf<WorkPackageOutput>(
            testWorkPackageOutput1,
            testWorkPackageOutput2
        )
    }


    @MockK
    lateinit var persistence: WorkPackagePersistence

    private lateinit var getWorkPackageOutputInteractor: GetWorkPackageOutputInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        getWorkPackageOutputInteractor = GetWorkPackageOutput(persistence)
    }

    @Test
    fun `get work package outputs returns empty list`() {
        every { persistence.getWorkPackageOutputsForWorkPackage(any()) } returns emptyList<WorkPackageOutput>()
        assertThat(getWorkPackageOutputInteractor.getWorkPackageOutputsForWorkPackage(1L)).isEmpty()
    }

    @Test
    fun `get work package outputs`() {
        every { persistence.getWorkPackageOutputsForWorkPackage(any()) } returns workPackageOutputs
        assertThat(getWorkPackageOutputInteractor.getWorkPackageOutputsForWorkPackage(1L)).isEqualTo(
            workPackageOutputs
        )
    }
}
