package io.cloudflight.ems.factory

import io.cloudflight.ems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.ems.call.entity.Call
import io.cloudflight.ems.project.entity.Project
import io.cloudflight.ems.project.entity.ProjectStatus
import io.cloudflight.ems.project.repository.ProjectRepository
import io.cloudflight.ems.project.repository.ProjectStatusRepository
import io.cloudflight.ems.user.entity.User
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import javax.transaction.Transactional

@Component
class ProjectFactory(
    val projectRepository: ProjectRepository,
    val projectStatusRepository: ProjectStatusRepository
) {

    @Transactional
    fun saveProject(author: User, call: Call): Project {
        val projectStatus = projectStatusRepository.save(ProjectStatus(null, null, ProjectApplicationStatus.DRAFT, author, ZonedDateTime.now(), null))
        return projectRepository.save(Project(null, call, "test_project", author, projectStatus, projectStatus))
    }


}