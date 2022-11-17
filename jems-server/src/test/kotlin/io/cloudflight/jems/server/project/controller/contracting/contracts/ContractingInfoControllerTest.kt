package io.cloudflight.jems.server.project.controller.contracting.contracts

import io.cloudflight.jems.api.project.dto.contracting.ContractInfoUpdateDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.contractInfo.getContractInfo.GetContractInfoInteractor
import io.cloudflight.jems.server.project.service.contracting.contractInfo.updateContractInfo.UpdateContractInfoInteractor
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractInfo
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ContractingInfoControllerTest: UnitTest() {

    companion object {
        private val contractingInfo = ProjectContractInfo(
            projectStartDate = LocalDate.of(2022, 8, 1),
            projectEndDate = LocalDate.of(2023, 8, 1),
            website = "tgci.gov",
            subsidyContractDate = LocalDate.of(2022, 8, 22),
            partnershipAgreementDate = LocalDate.of(2022, 9, 12)
        )
    }

    @MockK
    lateinit var getContractInfoInteractor: GetContractInfoInteractor
    @MockK
    lateinit var updateContractInfoInteractor: UpdateContractInfoInteractor

    @InjectMockKs
    lateinit var contractInfoController: ContractInfoController

    @Test
    fun getContractingInfo(){
        every { getContractInfoInteractor.getContractInfo(1L) } returns contractingInfo
        assertThat(contractInfoController.getProjectContractInfo(1L)).isEqualTo(contractingInfo.toDTO())

    }

    @Test
    fun updateContractingInfo() {
        every { updateContractInfoInteractor.updateContractInfo(1L,  any()) } returns ProjectContractInfo(
            projectStartDate = null,
            projectEndDate = null,
            website = "tgci.gov",
            subsidyContractDate = null,
            partnershipAgreementDate = LocalDate.of(2022, 9, 12)
        )
        assertThat(contractInfoController.updateProjectContractInfo(1L, ContractInfoUpdateDTO(
            website = "tgci.gov",
            partnershipAgreementDate = LocalDate.of(2022, 9, 12)
        ))).isEqualTo(ContractInfoUpdateDTO(
            website = "tgci.gov",
            partnershipAgreementDate = LocalDate.of(2022, 9, 12)
        ))

    }
}
