package io.cloudflight.jems.server.project.service.partner.state_aid.get_project_partner_state_aid

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetProjectPartnerStateAidInteractorTest: UnitTest() {

    companion object {
        private const val PARTNER_ID = 747L

        private val stateAid = ProjectPartnerStateAid(
            answer1 = true,
            justification1 = setOf(InputTranslation(SystemLanguage.EN, "true")),
            answer2 = false,
            justification2 = setOf(InputTranslation(SystemLanguage.EN, "false")),
            answer3 = null,
            justification3 = setOf(InputTranslation(SystemLanguage.EN, "null")),
            answer4 = null,
            justification4 = emptySet(),
        )
    }

    @MockK
    lateinit var persistence: PartnerPersistence

    @InjectMockKs
    lateinit var getStateAidInteractor: GetProjectPartnerStateAid

    @Test
    fun getStateAidForPartnerId() {
        every { persistence.getPartnerStateAid(PARTNER_ID) } returns stateAid
        assertThat(getStateAidInteractor.getStateAidForPartnerId(PARTNER_ID)).isEqualTo(stateAid)
    }

}
