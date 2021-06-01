package io.cloudflight.jems.server.factory

import io.cloudflight.jems.api.project.dto.file.ProjectFileType
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.file.ProjectFile
import io.cloudflight.jems.server.project.entity.ProjectStatusHistoryEntity
import io.cloudflight.jems.server.project.repository.ProjectFileRepository
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectStatusHistoryRepository
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Component
class ProjectFileFactory(
    val projectRepository: ProjectRepository,
    val projectStatusHistoryRepository: ProjectStatusHistoryRepository,
    val projectFileRepository: ProjectFileRepository
) {

    val callStart = ZonedDateTime.now().plusDays(1)
    val callEnd = ZonedDateTime.now().plusDays(20)

    @Transactional
    fun saveProject(author: UserEntity, call: CallEntity): ProjectEntity {
        val projectStatus = projectStatusHistoryRepository.save(
            ProjectStatusHistoryEntity(
                0,
                null,
                ApplicationStatus.DRAFT,
                author,
                ZonedDateTime.now(),
                null
            )
        )
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

    @Transactional
    fun saveProjectFile(project: ProjectEntity, applicant: UserEntity): ProjectFile {
        return projectFileRepository.save(
            ProjectFile(
                0,
                "project-files",
                "project-1/cat.jpg",
                "cat.jpg",
                project,
                applicant,
                ProjectFileType.APPLICANT_FILE,
                null,
                4,
                ZonedDateTime.now()
            )
        )
    }

}
