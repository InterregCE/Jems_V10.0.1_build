package io.cloudflight.jems.server.project.service.lumpsum.get_project_lump_sums

import io.cloudflight.jems.server.project.authorization.CanReadProject
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectLumpSums(
    private val persistence: ProjectLumpSumPersistence,
) : GetProjectLumpSumsInteractor {

    @Transactional(readOnly = true)
    @CanReadProject
    override fun getLumpSums(projectId: Long) = persistence.getLumpSums(projectId)

}
