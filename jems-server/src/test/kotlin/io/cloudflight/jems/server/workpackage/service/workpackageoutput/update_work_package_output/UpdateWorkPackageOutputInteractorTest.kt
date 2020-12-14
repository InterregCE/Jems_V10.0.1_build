package io.cloudflight.jems.server.workpackage.service.workpackageoutput.update_work_package_output

import io.cloudflight.jems.server.project.service.workpackage.WorkPackagePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.model.WorkPackageOutputUpdate
import io.cloudflight.jems.server.project.service.workpackage.update_work_package_output.UpdateWorkPackageOutput
import io.cloudflight.jems.server.project.service.workpackage.update_work_package_output.UpdateWorkPackageOutputInteractor
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateWorkPackageOutputInteractorTest {

    companion object {


        private val testWorkPackageOutput1 = WorkPackageOutput(
            outputNumber = 1,
            title = "TEST"
        )


        private val testWorkPackageOutput2 = WorkPackageOutput(
            outputNumber = 2,
            title = "TEST"
        )


        private val updateTestWorkPackageOutput1 = WorkPackageOutputUpdate(
            outputNumber = 1,
            title = "TEST updated"
        )


        private val updateTestWorkPackageOutput2 = WorkPackageOutputUpdate(
            outputNumber = 2,
            title = "TEST updated"
        )

        private val workPackageOutputsUpdated =
            mutableSetOf<WorkPackageOutput>(testWorkPackageOutput1, testWorkPackageOutput2)
        private val workPackageOutputsToUpdate =
            mutableSetOf<WorkPackageOutputUpdate>(updateTestWorkPackageOutput1, updateTestWorkPackageOutput2)

    }

    @MockK
    lateinit var persistence: WorkPackagePersistence

    private lateinit var updateWorkPackageOutputInteractor: UpdateWorkPackageOutputInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        updateWorkPackageOutputInteractor = UpdateWorkPackageOutput(persistence)
    }

    @Test
    fun `delete work package outputs from a work package`() {
        every { persistence.updateWorkPackageOutputs(any(), any(), any()) } returns emptySet<WorkPackageOutput>()
        Assertions.assertThat(updateWorkPackageOutputInteractor.updateWorkPackageOutputs(1L, emptySet(), 1L)).isEmpty()
    }

    @Test
    fun `update `() {
        every { persistence.updateWorkPackageOutputs(1L, any(), 1L) } returns workPackageOutputsUpdated
        Assertions.assertThat(
            updateWorkPackageOutputInteractor.updateWorkPackageOutputs(
                1L,
                workPackageOutputsToUpdate,
                1L
            )
        ).isEqualTo(workPackageOutputsUpdated)
    }


}
