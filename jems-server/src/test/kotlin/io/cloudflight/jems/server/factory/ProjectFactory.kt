package io.cloudflight.jems.server.factory

import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectStatusHistoryRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.user.entity.UserEntity
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Component
class ProjectFactory(
    val projectRepository: ProjectRepository,
    val projectStatusHistoryRepository: ProjectStatusHistoryRepository
) {

    @Transactional
    fun saveProject(author: UserEntity, call: CallEntity): ProjectEntity {
        val projectStatus = projectStatusHistoryRepository.save(ProjectStatusHistoryEntity(0, null, ApplicationStatus.DRAFT, author, ZonedDateTime.now(), null))
        return projectRepository.save(
            ProjectEntity(
                id = 0,
                call = call,
                acronym = "test_project",
                applicant = author,
                currentStatus = projectStatus,
                firstSubmission = projectStatus,
                step2Active = false
            )
        )
    }


}
