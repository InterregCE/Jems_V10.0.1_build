package io.cloudflight.jems.server.project.service.contracting.fileManagement.deleteContractFile

interface DeleteContractFileInteractor {

    fun delete(projectId: Long, fileId: Long)

}
