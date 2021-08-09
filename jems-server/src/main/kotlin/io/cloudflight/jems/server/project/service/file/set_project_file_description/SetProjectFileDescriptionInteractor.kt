package io.cloudflight.jems.server.project.service.file.set_project_file_description

import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata

interface SetProjectFileDescriptionInteractor {

    fun setDescription(projectId: Long, fileId: Long, description: String?): ProjectFileMetadata

}
