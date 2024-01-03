package io.cloudflight.jems.server.project.repository.checklist.contracting

import io.cloudflight.jems.server.project.repository.checklist.ChecklistInstanceRepository
import io.cloudflight.jems.server.project.repository.checklist.toModel
import io.cloudflight.jems.server.project.service.checklist.ContractingChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ContractingChecklistInstancePersistenceProvider(
    private val repository: ChecklistInstanceRepository
) : ContractingChecklistInstancePersistence {
    @Transactional(readOnly = true)
    override fun findChecklistInstances(searchRequest: ChecklistInstanceSearchRequest): List<ChecklistInstance> =
        repository.findAll(ChecklistInstanceRepository.buildSearchPredicate(searchRequest)!!).toList().toModel()
}
