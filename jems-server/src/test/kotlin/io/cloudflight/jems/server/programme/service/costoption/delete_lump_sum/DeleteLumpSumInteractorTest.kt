package io.cloudflight.jems.server.programme.service.costoption.delete_lump_sum

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.costoption.DeleteLumpSumWhenProgrammeSetupRestricted
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteLumpSumInteractorTest {

    @MockK
    lateinit var persistence: ProgrammeLumpSumPersistence

    private lateinit var deleteLumpSumInteractor: DeleteLumpSumInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        deleteLumpSumInteractor = DeleteLumpSum(persistence)
    }

    @Test
    fun `delete lump sum - OK`() {
        every { persistence.deleteLumpSum(1L) } answers {}
        every { persistence.isProgrammeSetupRestricted() } returns false
        deleteLumpSumInteractor.deleteLumpSum(1L)
        verify { persistence.deleteLumpSum(1L) }
    }

    @Test
    fun `delete lump sum - not existing`() {
        every { persistence.deleteLumpSum(-1L) } throws ResourceNotFoundException("programmeLumpSum")
        every { persistence.isProgrammeSetupRestricted() } returns false
        assertThrows<ResourceNotFoundException> { deleteLumpSumInteractor.deleteLumpSum(-1L) }
    }

    @Test
    fun `delete lump sum - call already published`() {
        every { persistence.deleteLumpSum(1L) } throws ResourceNotFoundException("programmeLumpSum")
        every { persistence.isProgrammeSetupRestricted() } returns true
        assertThrows<DeleteLumpSumWhenProgrammeSetupRestricted> { deleteLumpSumInteractor.deleteLumpSum(1L) }
    }

}
