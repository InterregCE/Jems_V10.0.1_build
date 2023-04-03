package io.cloudflight.jems.server.project.service.sharedFolderFile.delete

interface DeleteFileFromSharedFolderInteractor {

    fun delete(projectId: Long, fileId: Long)
}
