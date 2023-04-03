package io.cloudflight.jems.server.project.service.sharedFolderFile.description

interface SetDescriptionToSharedFolderFileInteractor {

    fun set(projectId: Long, fileId: Long, description: String)
}
