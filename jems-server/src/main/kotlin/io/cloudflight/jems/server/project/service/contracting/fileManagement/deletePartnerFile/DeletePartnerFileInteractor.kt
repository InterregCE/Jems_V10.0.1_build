package io.cloudflight.jems.server.project.service.contracting.fileManagement.deletePartnerFile

interface DeletePartnerFileInteractor {

    fun delete(partnerId: Long, fileId: Long)
}
