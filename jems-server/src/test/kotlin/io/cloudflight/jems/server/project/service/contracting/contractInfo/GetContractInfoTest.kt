package io.cloudflight.jems.server.project.service.contracting.contractInfo

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.contractInfo.getContractInfo.GetContractInfo
import io.cloudflight.jems.server.project.service.contracting.contractInfo.getContractInfo.GetContractInfoService
import io.cloudflight.jems.server.project.service.contracting.model.*
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class GetContractInfoTest: UnitTest(){

    companion object {
        private val projectContractingInfo = ProjectContractInfo(
            projectStartDate = LocalDate.of(2022, 8, 1),
            projectEndDate = LocalDate.of(2023, 8, 1),
            website = "tgci.gov",
            subsidyContractDate = LocalDate.of(2022, 8, 22),
            partnershipAgreementDate = LocalDate.of(2022, 9, 12)
        )
    }


    @RelaxedMockK
    lateinit var getContractInfoService: GetContractInfoService

    @InjectMockKs
    lateinit var getContractInfo: GetContractInfo


    @Test
    fun `get project contract info`() {
        every { getContractInfoService.getContractInfo(1L) } returns projectContractingInfo

        assertThat(getContractInfo.getContractInfo(1L)).isEqualTo(
            ProjectContractInfo(
                projectStartDate = LocalDate.of(2022, 8, 1),
                projectEndDate = LocalDate.of(2023, 8, 1),
                website = "tgci.gov",
                subsidyContractDate = LocalDate.of(2022, 8, 22),
                partnershipAgreementDate = LocalDate.of(2022, 9, 12)
            )
        )
    }

}
