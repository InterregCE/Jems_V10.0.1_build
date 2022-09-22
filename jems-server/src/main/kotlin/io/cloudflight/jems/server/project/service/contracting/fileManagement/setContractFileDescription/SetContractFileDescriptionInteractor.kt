package io.cloudflight.jems.server.project.service.contracting.fileManagement.setContractFileDescription

interface SetContractFileDescriptionInteractor {

    fun setContractFileDescription(projectId: Long, fileId: Long, description: String)
}
