package io.cloudflight.jems.server.programme.service.priority.delete_priority

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.IndustrialTransition
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.RenewableEnergy
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.priority.testPriority
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus

@ExtendWith(MockKExtension::class)
class DeletePriorityInteractorTest {

    @MockK
    lateinit var persistence: ProgrammePriorityPersistence

    @MockK
    lateinit var isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor

    @InjectMockKs
    private lateinit var deletePriority: DeletePriority

    @Test
    fun `delete priority - OK`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getPriorityById(1L) } returns testPriority
        every { persistence.getObjectivePoliciesAlreadyInUse() } returns setOf(IndustrialTransition)
        every { persistence.delete(1L) } answers {}

        deletePriority.deletePriority(1L)
        verify { persistence.delete(1L) }
    }

    @Test
    fun `delete priority - specific objective in use`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getPriorityById(1L) } returns testPriority
        every { persistence.getObjectivePoliciesAlreadyInUse() } returns setOf(RenewableEnergy)

        val ex = assertThrows<I18nValidationException> { deletePriority.deletePriority(1L) }
        assertThat(ex.httpStatus).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        assertThat(ex.i18nFieldErrors!!["specificObjectives"]).isEqualTo(I18nFieldError(
            i18nKey = "programme.priority.specificObjective.already.used.in.call",
            i18nArguments = listOf(RenewableEnergy.name)
        ))
    }

    @Test
    fun `delete priority - not existing`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.getPriorityById(-1L) } throws ResourceNotFoundException("programmePriority")
        assertThrows<ResourceNotFoundException> { deletePriority.deletePriority(-1L) }
    }

    @Test
    fun `delete priority - programme setup already locked`() {
        every { isProgrammeSetupLocked.isLocked() } returns true
        assertThrows<DeletionWhenProgrammeSetupRestricted> { deletePriority.deletePriority(-2L) }
    }

}
