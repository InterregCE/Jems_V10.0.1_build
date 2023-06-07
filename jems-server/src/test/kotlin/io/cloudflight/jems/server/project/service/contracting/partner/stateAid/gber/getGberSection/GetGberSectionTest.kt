package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getGberSection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.PARTNER_ID
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.expectedGberSection
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getStateAidGberSection.GetContractingPartnerStateAidGber
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.gber.getStateAidGberSection.GetContractingPartnerStateAidGberService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class GetGberSectionTest : UnitTest() {

    @MockK
    lateinit var getContractingPartnerStateAidGberService: GetContractingPartnerStateAidGberService

    @InjectMockKs
    lateinit var getContractingPartnerStateAidGber: GetContractingPartnerStateAidGber

    @Test
    fun `get gber section`() {
        every { getContractingPartnerStateAidGberService.getGberSection(PARTNER_ID) } returns expectedGberSection
        Assertions.assertThat(getContractingPartnerStateAidGber.getGberSection(PARTNER_ID)).isEqualTo(expectedGberSection)
    }

}
