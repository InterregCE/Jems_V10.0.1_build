package io.cloudflight.jems.server.factory

import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.api.dto.ProjectFileType
import io.cloudflight.jems.server.call.entity.Call
import io.cloudflight.jems.server.user.entity.User
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.entity.ProjectFile
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.repository.ProjectFileRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectStatusRepository
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import javax.transaction.Transactional

@Component
class ProjectFileFactory(
    val projectRepository: ProjectRepository,
    val projectStatusRepository: ProjectStatusRepository,
    val projectFileRepository: ProjectFileRepository
) {

    val callStart = ZonedDateTime.now().plusDays(1)
    val callEnd = ZonedDateTime.now().plusDays(20)

    @Transactional
    fun saveProject(author: User, call: Call): Project {
        val projectStatus = projectStatusRepository.save(ProjectStatus(null, null, ProjectApplicationStatus.DRAFT, author, ZonedDateTime.now(), null))
        return projectRepository.save(Project(null, call, "test_project", author, projectStatus, projectStatus))
    }

    @Transactional
    fun saveProjectFile(project: Project, applicant: User): ProjectFile {
        return projectFileRepository.save(
            ProjectFile(
                null,
                "project-files",
                "project-1/cat.jpg",
                "cat.jpg",
                project,
                applicant,
                ProjectFileType.APPLICANT_FILE,
                null,
                4,
                ZonedDateTime.now()))
    }

}
