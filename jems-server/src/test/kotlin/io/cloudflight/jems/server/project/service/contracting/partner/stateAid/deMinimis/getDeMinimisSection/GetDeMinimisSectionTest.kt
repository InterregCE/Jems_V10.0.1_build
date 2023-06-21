package io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getDeMinimisSection

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.PARTNER_ID
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.expectedDeMinimisSection
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getStateAidDeMinimisSection.GetContractingPartnerStateAidDeMinimis
import io.cloudflight.jems.server.project.service.contracting.partner.stateAid.deMinimis.getStateAidDeMinimisSection.GetContractingPartnerStateAidDeMinimisService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GetDeMinimisSectionTest : UnitTest() {

    @MockK
    lateinit var getContractingPartnerStateAidDeMinimisService: GetContractingPartnerStateAidDeMinimisService

    @InjectMockKs
    lateinit var getContractingPartnerStateAidDeMinimis: GetContractingPartnerStateAidDeMinimis

    @Test
    fun `get minimis section`() {
        every { getContractingPartnerStateAidDeMinimisService.getDeMinimisSection(PARTNER_ID) } returns expectedDeMinimisSection
        assertThat(getContractingPartnerStateAidDeMinimis.getDeMinimisSection(PARTNER_ID)).isEqualTo(expectedDeMinimisSection)
    }

}
