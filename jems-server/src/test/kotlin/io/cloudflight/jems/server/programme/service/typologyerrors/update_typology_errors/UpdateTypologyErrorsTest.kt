package io.cloudflight.jems.server.programme.service.typologyerrors.update_typology_errors

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.typologyerrors.DeletionIsNotAllowedException
import io.cloudflight.jems.server.programme.service.typologyerrors.MaxAllowedTypologyErrorsReachedException
import io.cloudflight.jems.server.programme.service.typologyerrors.ProgrammeTypologyErrorsPersistence
import io.cloudflight.jems.server.programme.service.typologyerrors.UpdateTypologyErrorsService
import io.cloudflight.jems.server.programme.service.typologyerrors.model.TypologyErrors
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

internal class UpdateTypologyErrorsTest : UnitTest()  {

    companion object {
        private val typologyErrors = TypologyErrors(
            id = 1,
            description = "Sample description"
        )
    }

    @MockK
    lateinit var persistence: ProgrammeTypologyErrorsPersistence

    @InjectMockKs
    lateinit var updateTypologyErrors: UpdateTypologyErrorsService

    @RelaxedMockK
    lateinit var generalValidatorService: GeneralValidatorService

    @MockK
    lateinit var isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @BeforeEach
    fun reset() {
        clearMocks(generalValidatorService)
    }

    @Test
    fun `should throw exception during update programme typology errors`() {
        val longText = getStringOfLength(501)
        every { isProgrammeSetupLocked.isLocked() } returns false
        every {
            generalValidatorService.throwIfAnyIsInvalid(
                generalValidatorService.maxLength(longText, 500, "description")
            )
        } throws AppInputValidationException(hashMapOf())

        assertThrows<AppInputValidationException> {
            updateTypologyErrors.updateTypologyErrors(listOf(), listOf(typologyErrors.copy(description = longText)))
        }
    }

    @Test
    fun `should throw exception when typology errors are being deleted`() {
        every { isProgrammeSetupLocked.isLocked() } returns true

        assertThrows<DeletionIsNotAllowedException> {
            updateTypologyErrors.updateTypologyErrors(
                listOf(1L),
                emptyList()
            )
        }
    }

    @Test
    fun `should throw exception the typology errors are above the request limit`() {
        every { isProgrammeSetupLocked.isLocked() } returns false
        val list: MutableList<TypologyErrors> = mutableListOf()

        for (i in 1..51) {
            list.add(TypologyErrors(id = i.toLong(), description = "Sample text"))
        }

        assertThrows<MaxAllowedTypologyErrorsReachedException> {
            updateTypologyErrors.updateTypologyErrors(
                listOf(),
                list
            )
        }
    }
}
