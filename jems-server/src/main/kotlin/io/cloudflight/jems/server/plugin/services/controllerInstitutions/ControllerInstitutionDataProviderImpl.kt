package io.cloudflight.jems.server.plugin.services.controllerInstitutions

import io.cloudflight.jems.plugin.contract.models.controllerInstitutions.ControllerInstitutionData
import io.cloudflight.jems.plugin.contract.models.controllerInstitutions.InstitutionPartnerDetailsData
import io.cloudflight.jems.plugin.contract.services.controllerInstitutions.ControllerInstitutionDataProvider
import io.cloudflight.jems.server.controllerInstitution.service.ControllerInstitutionPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ControllerInstitutionDataProviderImpl(
    private val controllerInstitutionPersistence: ControllerInstitutionPersistence
) : ControllerInstitutionDataProvider {

    @Transactional(readOnly = true)
    override fun getInstitutionPartnerAssignments(): Sequence<InstitutionPartnerDetailsData> =
        controllerInstitutionPersistence.getAllInstitutionPartnerAssignments()

    @Transactional(readOnly = true)
    override fun getControllerInstitutionById(institutionId: Long): ControllerInstitutionData =
        controllerInstitutionPersistence.getControllerInstitutionById(institutionId).toDataModel()

    @Transactional(readOnly = true)
    override fun getControllerInstitutionByPartnerId(partnerId: Long): ControllerInstitutionData? {
        val controllerInstitution = controllerInstitutionPersistence.getControllerInstitutions(setOf(partnerId))
        return controllerInstitution.getOrDefault(partnerId, null)?.toDataModel()
    }

}
