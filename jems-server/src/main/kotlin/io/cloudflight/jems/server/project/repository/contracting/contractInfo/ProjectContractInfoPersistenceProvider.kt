package io.cloudflight.jems.server.project.repository.contracting.contractInfo

import io.cloudflight.jems.server.project.service.contracting.contractInfo.ProjectContractInfoPersistence
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractInfo
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectContractInfoPersistenceProvider(
    private val projectContractInfoRepository: ProjectContractInfoRepository
): ProjectContractInfoPersistence {

    @Transactional(readOnly = true)
    override fun getContractInfo(projectId: Long): ProjectContractInfo =
        projectContractInfoRepository.findById(projectId).let {
            when {
                it.isPresent -> it.get().toModel()
                else -> ProjectContractInfo(website = null, partnershipAgreementDate = null)
            }
        }


    @Transactional
    override fun updateContractInfo(projectId: Long, contractInfo: ProjectContractInfo): ProjectContractInfo =
        projectContractInfoRepository.save(contractInfo.toEntity(projectId)).toModel()

}
