package io.cloudflight.jems.server.project.service.checklist.create.contracting

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanSetProjectToContracted
import io.cloudflight.jems.server.project.service.checklist.ContractingChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceDetail
import io.cloudflight.jems.server.project.service.checklist.model.CreateChecklistInstanceModel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateContractingChecklistInstance(
    private val persistence: ContractingChecklistInstancePersistence,
    private val securityService: SecurityService
) : CreateContractingChecklistInstanceInteractor {

    @CanSetProjectToContracted
    @Transactional
    @ExceptionWrapper(CreateContractingChecklistInstanceException::class)
    override fun create(projectId: Long, createCheckList: CreateChecklistInstanceModel): ChecklistInstanceDetail {
        return persistence.create(
            createChecklist = createCheckList,
            creatorId = securityService.currentUser?.user?.id!!,
            projectId = projectId
        )
    }
}
