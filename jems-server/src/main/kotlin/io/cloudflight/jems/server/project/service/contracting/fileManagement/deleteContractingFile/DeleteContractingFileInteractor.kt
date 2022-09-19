package io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteContractingFile

interface DeleteContractingFileInteractor {

    fun delete(projectId: Long, fileId: Long)

}
