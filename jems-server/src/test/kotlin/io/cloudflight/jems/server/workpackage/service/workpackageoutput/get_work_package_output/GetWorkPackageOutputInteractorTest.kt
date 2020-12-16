package io.cloudflight.jems.server.workpackage.service.workpackageoutput.get_work_package_output

import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.output.get_work_package_output.GetWorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.output.get_work_package_output.GetWorkPackageOutputInteractor
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput
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
            title = "TEST"
        )

        private val testWorkPackageOutput2 = WorkPackageOutput(
            outputNumber = 2,
            title = "TEST"
        )

        private val workPackageOutputs = mutableSetOf<WorkPackageOutput>(
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
        every { persistence.getWorkPackageOutputsForWorkPackage(any()) } returns emptySet<WorkPackageOutput>()
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
