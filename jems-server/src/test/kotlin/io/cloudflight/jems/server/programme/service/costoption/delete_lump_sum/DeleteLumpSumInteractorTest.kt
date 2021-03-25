package io.cloudflight.jems.server.programme.service.costoption.delete_lump_sum

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.costoption.DeleteLumpSumWhenProgrammeSetupRestricted
import io.cloudflight.jems.server.programme.service.costoption.ProgrammeLumpSumPersistence
import io.cloudflight.jems.server.programme.service.is_programme_setup_locked.IsProgrammeSetupLockedInteractor
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

    @MockK
    lateinit var isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor

    private lateinit var deleteLumpSumInteractor: DeleteLumpSumInteractor

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        deleteLumpSumInteractor = DeleteLumpSum(persistence, isProgrammeSetupLocked)
    }

    @Test
    fun `delete lump sum - OK`() {
        every { persistence.deleteLumpSum(1L) } answers {}
        every { isProgrammeSetupLocked.isLocked() } returns false
        deleteLumpSumInteractor.deleteLumpSum(1L)
        verify { persistence.deleteLumpSum(1L) }
    }

    @Test
    fun `delete lump sum - not existing`() {
        every { persistence.deleteLumpSum(-1L) } throws ResourceNotFoundException("programmeLumpSum")
        every { isProgrammeSetupLocked.isLocked() } returns false
        assertThrows<ResourceNotFoundException> { deleteLumpSumInteractor.deleteLumpSum(-1L) }
    }

    @Test
    fun `delete lump sum - call already published`() {
        every { persistence.deleteLumpSum(1L) } throws ResourceNotFoundException("programmeLumpSum")
        every { isProgrammeSetupLocked.isLocked() } returns true
        assertThrows<DeleteLumpSumWhenProgrammeSetupRestricted> { deleteLumpSumInteractor.deleteLumpSum(1L) }
    }

}
