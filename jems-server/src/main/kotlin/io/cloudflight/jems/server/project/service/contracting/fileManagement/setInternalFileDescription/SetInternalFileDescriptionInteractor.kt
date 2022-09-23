package io.cloudflight.jems.server.project.service.contracting.fileManagement.setInternalFileDescription

interface SetInternalFileDescriptionInteractor {

    fun setInternalFileDescription(projectId: Long, fileId: Long, description: String)
}
