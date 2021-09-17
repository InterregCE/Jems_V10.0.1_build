package io.cloudflight.jems.server.project.service.partner.state_aid.update_project_partner_state_aid

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class UpdateProjectPartnerStateAidInteractorTest: UnitTest() {

    companion object {
        private const val PARTNER_ID = 566L

        private val stateAid = ProjectPartnerStateAid(
            answer1 = true,
            justification1 = setOf(InputTranslation(SystemLanguage.EN, "true")),
            answer2 = false,
            justification2 = setOf(InputTranslation(SystemLanguage.EN, "false")),
            answer3 = null,
            justification3 = setOf(InputTranslation(SystemLanguage.EN, "null")),
            answer4 = null,
            justification4 = emptySet(),
            stateAidScheme = null
        )
    }

    @MockK
    lateinit var persistence: PartnerPersistence

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var updateStateAidInteractor: UpdateProjectPartnerStateAid

    @Test
    fun updatePartnerStateAid() {
        every { persistence.updatePartnerStateAid(PARTNER_ID, any()) } returnsArgument 1
        assertThat(updateStateAidInteractor.updatePartnerStateAid(PARTNER_ID, stateAid)).isEqualTo(stateAid)
    }

}
