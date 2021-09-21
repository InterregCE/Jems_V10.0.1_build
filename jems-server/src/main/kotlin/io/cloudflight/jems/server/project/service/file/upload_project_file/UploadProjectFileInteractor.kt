package io.cloudflight.jems.server.project.service.file.upload_project_file

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.model.ProjectFileCategory
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata

interface UploadProjectFileInteractor {

    fun upload(projectId: Long, projectFileCategory: ProjectFileCategory, projectFile: ProjectFile): ProjectFileMetadata

}
