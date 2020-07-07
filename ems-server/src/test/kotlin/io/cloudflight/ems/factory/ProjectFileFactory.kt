package io.cloudflight.ems.factory

import io.cloudflight.ems.entity.User
import io.cloudflight.ems.entity.Project
import io.cloudflight.ems.entity.ProjectFile
import io.cloudflight.ems.repository.ProjectFileRepository
import io.cloudflight.ems.repository.ProjectRepository
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.transaction.Transactional

@Component
class ProjectFileFactory(
    val projectRepository: ProjectRepository,
    val projectFileRepository: ProjectFileRepository
) {

    @Transactional
    fun saveProject(author: User): Project {
        return projectRepository.save(Project(null, "test_project", author, LocalDate.now()))
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
