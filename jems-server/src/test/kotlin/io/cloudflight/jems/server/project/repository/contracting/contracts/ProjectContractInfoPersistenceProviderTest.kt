package io.cloudflight.jems.server.project.repository.contracting.contracts

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.contracting.ProjectContractInfoEntity
import io.cloudflight.jems.server.project.repository.contracting.contractInfo.ProjectContractInfoPersistenceProvider
import io.cloudflight.jems.server.project.repository.contracting.contractInfo.ProjectContractInfoRepository
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractInfo
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.Optional

class ProjectContractInfoPersistenceProviderTest: UnitTest() {

    companion object {
        private val contractInfoEntity = ProjectContractInfoEntity(
            projectId = 1L,
            website = "tgci.gov",
            partnershipAgreementDate =  LocalDate.of(2022, 9, 12)
        )

        private val contractInfoModel = ProjectContractInfo(
            projectStartDate = null,
            projectEndDate = null,
            website = "tgci.gov",
            subsidyContractDate = null,
            partnershipAgreementDate = LocalDate.of(2022, 9, 12)
        )
    }

    @RelaxedMockK
    lateinit var projectContractInfoRepository: ProjectContractInfoRepository

    @InjectMockKs
    lateinit var projectContractInfoPersistence: ProjectContractInfoPersistenceProvider


    @Test
    fun `fetch project contract info`() {
        every { projectContractInfoRepository.findById(1L) } returns Optional.of(contractInfoEntity)
        assertThat(projectContractInfoPersistence.getContractInfo(1L)).isEqualTo(contractInfoModel)
    }


    @Test
    fun `update contracting info`() {
        every { projectContractInfoRepository.save(any()) } returns contractInfoEntity

        assertThat(projectContractInfoPersistence.updateContractInfo(1L, contractInfoModel)).isEqualTo(
            ProjectContractInfo(
                projectStartDate = null,
                projectEndDate = null,
                website = "tgci.gov",
                subsidyContractDate = null,
                partnershipAgreementDate = LocalDate.of(2022, 9, 12)
            )
        )
    }

}
