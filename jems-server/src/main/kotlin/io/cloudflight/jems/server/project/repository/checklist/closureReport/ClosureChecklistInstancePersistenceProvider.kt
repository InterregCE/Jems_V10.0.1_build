package io.cloudflight.jems.server.project.repository.checklist.closureReport

import io.cloudflight.jems.server.project.repository.checklist.ChecklistInstanceRepository
import io.cloudflight.jems.server.project.repository.checklist.toModel
import io.cloudflight.jems.server.project.service.checklist.ClosureChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstance
import io.cloudflight.jems.server.project.service.checklist.model.ChecklistInstanceSearchRequest
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ClosureChecklistInstancePersistenceProvider(
    private val repository: ChecklistInstanceRepository
): ClosureChecklistInstancePersistence {

    @Transactional(readOnly = true)
    override fun findChecklistInstances(searchRequest: ChecklistInstanceSearchRequest): List<ChecklistInstance> =
        repository.findAll(ChecklistInstanceRepository.buildSearchPredicate(searchRequest)).toList().toModel()

}
