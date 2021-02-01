package io.cloudflight.jems.server.programme.service.priority.delete_priority

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.IndustrialTransition
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.RenewableEnergy
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
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

    @InjectMockKs
    private lateinit var deletePriority: DeletePriority

    @Test
    fun `delete priority - OK`() {
        every { persistence.delete(1L) } answers {}
        every { persistence.getPriorityById(1L) } returns testPriority
        every { persistence.getObjectivePoliciesAlreadyInUse() } returns setOf(IndustrialTransition)

        deletePriority.deletePriority(1L)
        verify { persistence.delete(1L) }
    }

    @Test
    fun `delete priority - specific objective in use`() {
        every { persistence.delete(1L) } answers {}
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
    fun `delete prioty - not existing`() {
        every { persistence.getPriorityById(-1L) } throws ResourceNotFoundException("programmePriority")
        assertThrows<ResourceNotFoundException> { deletePriority.deletePriority(-1L) }
    }

}
