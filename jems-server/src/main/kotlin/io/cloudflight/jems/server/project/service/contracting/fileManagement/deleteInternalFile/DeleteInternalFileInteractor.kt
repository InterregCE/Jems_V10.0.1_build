package io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteInternalFile

interface DeleteInternalFileInteractor {

    fun delete(projectId: Long, fileId: Long)

}
