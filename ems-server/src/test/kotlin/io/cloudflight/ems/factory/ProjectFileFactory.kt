package io.cloudflight.ems.factory

import io.cloudflight.ems.api.dto.ProjectApplicationStatus
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.entity.Project
import io.cloudflight.ems.entity.ProjectFile
import io.cloudflight.ems.entity.ProjectStatus
import io.cloudflight.ems.repository.ProjectFileRepository
import io.cloudflight.ems.repository.ProjectRepository
import io.cloudflight.ems.repository.ProjectStatusRepository
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.transaction.Transactional

@Component
class ProjectFileFactory(
    val projectRepository: ProjectRepository,
    val projectStatusRepository: ProjectStatusRepository,
    val projectFileRepository: ProjectFileRepository
) {

    @Transactional
    fun saveProject(author: User): Project {
        val projectStatus = projectStatusRepository.save(ProjectStatus(null, null, ProjectApplicationStatus.DRAFT, author, ZonedDateTime.now(), null))
        return projectRepository.save(Project(null, "test_project", author, projectStatus, projectStatus))
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
                null,
                4,
                ZonedDateTime.now()))
    }

}
