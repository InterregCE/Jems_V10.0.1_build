package io.cloudflight.jems.server.plugin.services.controllerInstitutions

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.plugin.contract.models.controllerInstitutions.ControllerInstitutionData
import io.cloudflight.jems.plugin.contract.models.controllerInstitutions.OutputNutsData
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList

fun ControllerInstitution.toDataModel() = ControllerInstitutionData(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt,
    institutionNuts = institutionNuts.toOutputNutsDataModel(),
)

fun List<OutputNuts>.toOutputNutsDataModel(): List<OutputNutsData> =
    this.map {
        OutputNutsData(
            code = it.code,
            title = it.title,
            areas = it.areas.toOutputNutsDataModel()
        )
    }

fun ControllerInstitutionList.toDataModel() = ControllerInstitutionData(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt,
    institutionNuts = institutionNuts.toOutputNutsDataModel(),
)
