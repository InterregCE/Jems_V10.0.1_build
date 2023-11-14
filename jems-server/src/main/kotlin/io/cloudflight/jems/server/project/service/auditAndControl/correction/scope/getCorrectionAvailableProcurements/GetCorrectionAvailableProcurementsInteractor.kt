package io.cloudflight.jems.server.project.service.auditAndControl.correction.identification.scope.getCorrectionAvailableProcurements

import io.cloudflight.jems.server.call.service.model.IdNamePair

interface GetCorrectionAvailableProcurementsInteractor {

    fun getAvailableProcurements(correctionId: Long): List<IdNamePair>
}