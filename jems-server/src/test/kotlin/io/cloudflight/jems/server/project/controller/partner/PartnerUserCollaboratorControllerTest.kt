package io.cloudflight.jems.server.project.controller.partner

import io.cloudflight.jems.api.project.dto.assignment.PartnerCollaboratorLevelDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.partneruser.PartnerCollaboratorLevel.VIEW
import io.cloudflight.jems.server.project.service.partner.assign_user_collaborator_to_partner.AssignUserCollaboratorToPartnerInteractor
import io.cloudflight.jems.server.project.service.partner.get_partner_user_collaborator.GetPartnerUserCollaboratorsInteractor
import io.cloudflight.jems.server.project.service.partnerUser.getMyPartnerCollaboratorLevel.GetMyPartnerCollaboratorLevelInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PartnerUserCollaboratorControllerTest: UnitTest() {

    companion object {
        private const val PARTNER_ID = 655L
    }

    @MockK
    lateinit var  assignUserCollaboratorToPartner: AssignUserCollaboratorToPartnerInteractor

    @MockK
    lateinit var  getPartnerUserCollaborators: GetPartnerUserCollaboratorsInteractor

    @MockK
    lateinit var  checkMyPartnerCollaboratorLevel: GetMyPartnerCollaboratorLevelInteractor

    @InjectMockKs
    private lateinit var controller: PartnerUserCollaboratorController

    @Test
    fun `checkMyPartnerLevel - empty`() {
        every { checkMyPartnerCollaboratorLevel.getMyPartnerCollaboratorLevel(-1) } returns null
        assertThat(controller.checkMyPartnerLevel(-1)).isNull()
    }

    @Test
    fun checkMyPartnerLevel() {
        every { checkMyPartnerCollaboratorLevel.getMyPartnerCollaboratorLevel(PARTNER_ID) } returns VIEW
        assertThat(controller.checkMyPartnerLevel(PARTNER_ID)).isEqualTo(PartnerCollaboratorLevelDTO.VIEW)
    }
}
