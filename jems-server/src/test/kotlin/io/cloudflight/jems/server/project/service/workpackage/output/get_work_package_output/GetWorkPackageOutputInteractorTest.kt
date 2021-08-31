package io.cloudflight.jems.server.project.service.workpackage.output.get_work_package_output

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.output.model.WorkPackageOutput
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetWorkPackageOutputInteractorTest : UnitTest() {

    private val testOutput1 = WorkPackageOutput(
        workPackageId = 1L,
        outputNumber = 1,
        title = setOf(InputTranslation(SystemLanguage.EN, "Test")),
        description = emptySet()

    )

    private val testOutput2 = WorkPackageOutput(
        workPackageId = 1L,
        outputNumber = 2,
        title = setOf(InputTranslation(SystemLanguage.EN, "Test")),
        description = emptySet()
    )


    @MockK
    lateinit var persistence: WorkPackagePersistence

    @InjectMockKs
    private lateinit var getWorkPackageOutput: GetWorkPackageOutput

    @Test
    fun `get work package outputs returns empty list`() {
        every { persistence.getWorkPackageOutputsForWorkPackage(10L, 1L) } returns emptyList()
        assertThat(getWorkPackageOutput.getOutputsForWorkPackage(1L, 10L)).isEmpty()
    }

    @Test
    fun `get work package outputs`() {
        every { persistence.getWorkPackageOutputsForWorkPackage(1L, 1L) } returns listOf(testOutput1, testOutput2)
        assertThat(getWorkPackageOutput.getOutputsForWorkPackage(1L, 1L))
            .containsExactly(testOutput1, testOutput2)
    }
}
