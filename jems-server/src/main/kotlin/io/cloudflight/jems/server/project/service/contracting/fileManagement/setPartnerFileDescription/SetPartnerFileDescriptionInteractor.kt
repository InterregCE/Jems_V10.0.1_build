package io.cloudflight.jems.server.project.service.contracting.fileManagement.setPartnerFileDescription

interface SetPartnerFileDescriptionInteractor {
    fun setPartnerFileDescription(partnerId: Long, fileId: Long, description: String)
}
