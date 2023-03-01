package io.cloudflight.jems.server.project.controller.contracting.sectionLock

import io.cloudflight.jems.api.project.contracting.ContractingSectionLockApi
import io.cloudflight.jems.api.project.dto.contracting.ContractingSectionDTO
import io.cloudflight.jems.server.project.service.contracting.model.ProjectContractingSection
import io.cloudflight.jems.server.project.service.contracting.sectionLock.getLockedSections.GetLockedSectionsInteractor
import io.cloudflight.jems.server.project.service.contracting.sectionLock.lockSection.ProjectContractingSectionLockInteractor
import io.cloudflight.jems.server.project.service.contracting.sectionLock.unlockSection.ProjectContractingSectionUnlockInteractor
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectContractingSectionLockController(
    private val contractingLockedSections: GetLockedSectionsInteractor,
    private val contractingSectionLock: ProjectContractingSectionLockInteractor,
    private val contractingSectionUnlock: ProjectContractingSectionUnlockInteractor,
): ContractingSectionLockApi {
    override fun getLockedSections(projectId: Long): List<ContractingSectionDTO> =
        contractingLockedSections.getLockedSections(projectId).map { ContractingSectionDTO.valueOf(it.name) }

    override fun lock(projectId: Long, sectionName: ContractingSectionDTO) =
        contractingSectionLock.lock(ProjectContractingSection.valueOf(sectionName.name), projectId)

    override fun unlock(projectId: Long, sectionName: ContractingSectionDTO) =
        contractingSectionUnlock.unlock(ProjectContractingSection.valueOf(sectionName.name), projectId)
}