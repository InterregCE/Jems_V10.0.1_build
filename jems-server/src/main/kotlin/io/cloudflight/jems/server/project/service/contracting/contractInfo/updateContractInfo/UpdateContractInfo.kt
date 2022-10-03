package io.cloudflight.jems.server.project.service.contracting.contractInfo.updateContractInfo

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditContractInfo
import io.cloudflight.jems.server.project.repository.ProjectPersistenceProvider
import io.cloudflight.jems.server.project.service.contracting.ContractingValidator
import io.cloudflight.jems.server.project.service.contracting.contractInfo.ProjectContractInfoPersistence
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractInfo
import io.cloudflight.jems.server.project.service.projectContractInfoChanged
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateContractInfo(
    private val projectContractInfoPersistence: ProjectContractInfoPersistence,
    private val projectPersistence: ProjectPersistenceProvider,
    private val auditPublisher: ApplicationEventPublisher,
    private val validator: ContractingValidator
    ): UpdateContractInfoInteractor {

    @CanEditContractInfo
    @Transactional
    @ExceptionWrapper(UpdateContractInfoException::class)
    override fun updateContractInfo(projectId: Long, contractInfo: ProjectContractInfo): ProjectContractInfo {
         projectPersistence.getProjectSummary(projectId).let { projectSummary ->
            validator.validateProjectStatusForModification(projectSummary)
            val partnerShipAgreement = projectContractInfoPersistence.getContractInfo(projectId).partnershipAgreementDate
            val updatedContractInfo = projectContractInfoPersistence.updateContractInfo(projectId, contractInfo)

            if (projectSummary.status.isAlreadyContracted() &&
                updatedContractInfo.partnershipAgreementDate != partnerShipAgreement) {
                auditPublisher.publishEvent(
                    projectContractInfoChanged(
                        context = this,
                        project = projectSummary,
                        oldPartnershipAgreementDate = partnerShipAgreement,
                        newPartnershipAgreementDate = updatedContractInfo.partnershipAgreementDate
                    )
                )
            }
            return updatedContractInfo
        }
    }

}
