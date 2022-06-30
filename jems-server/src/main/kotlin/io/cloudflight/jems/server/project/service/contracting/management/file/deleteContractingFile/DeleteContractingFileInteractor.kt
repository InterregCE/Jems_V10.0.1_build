package io.cloudflight.jems.server.project.service.contracting.management.file.deleteContractingFile

interface DeleteContractingFileInteractor {

    fun delete(projectId: Long, fileId: Long)

}
