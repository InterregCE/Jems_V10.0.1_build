package io.cloudflight.jems.server.programme.controller.legalstatus

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.CS
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusDTO
import io.cloudflight.jems.api.programme.dto.legalstatus.ProgrammeLegalStatusUpdateDTO
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.legalstatus.get_legal_statuses.GetLegalStatusInteractor
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusTranslatedValue
import io.cloudflight.jems.server.programme.service.legalstatus.update_legal_statuses.UpdateLegalStatusInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProgrammeLegalStatusControllerTest: UnitTest() {

    companion object {

        private const val ID = 1L
        private val legalStatus = ProgrammeLegalStatus(
            id = ID,
            translatedValues = setOf(
                ProgrammeLegalStatusTranslatedValue(language = CS, description = "CS desc"),
                ProgrammeLegalStatusTranslatedValue(language = EN, description = ""),
                ProgrammeLegalStatusTranslatedValue(language = SK, description = null),
            )
        )
        private val legalStatusDto = ProgrammeLegalStatusDTO(
            id = ID,
            description = setOf(InputTranslation(language = CS, translation = "CS desc"))
        )

    }

    @MockK
    lateinit var getLegalStatus: GetLegalStatusInteractor

    @MockK
    lateinit var updateLegalStatus: UpdateLegalStatusInteractor

    @InjectMockKs
    private lateinit var controller: ProgrammeLegalStatusController

    @Test
    fun `should get Programme Legal Statuses`() {
        every { getLegalStatus.getLegalStatuses() } returns listOf(legalStatus)
        assertThat(controller.getProgrammeLegalStatusList()).containsExactly(legalStatusDto)
    }

    @Test
    fun `should update Programme Legal Statuses`() {
        val deleteIdsSlot = slot<Set<Long>>()
        val toPersistSlot = slot<Collection<ProgrammeLegalStatus>>()
        every { updateLegalStatus.updateLegalStatuses(capture(deleteIdsSlot), capture(toPersistSlot)) } returnsArgument 1

        val toUpdate = ProgrammeLegalStatusUpdateDTO(
            toDeleteIds = setOf(56L),
            toPersist = listOf(legalStatusDto.copy(id = null)),
        )
        assertThat(controller.updateProgrammeLegalStatuses(toUpdate)).containsExactly(legalStatusDto.copy(id = 0))
        assertThat(deleteIdsSlot.captured).containsExactly(56L)
        assertThat(toPersistSlot.captured).containsExactly(
            ProgrammeLegalStatus(
                id = 0,
                translatedValues = setOf(ProgrammeLegalStatusTranslatedValue(language = CS, description = "CS desc")),
            )
        )
    }

}
