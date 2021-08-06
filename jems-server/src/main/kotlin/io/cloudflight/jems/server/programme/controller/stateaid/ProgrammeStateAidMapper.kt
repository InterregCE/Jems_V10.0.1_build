package io.cloudflight.jems.server.programme.controller.stateaid

import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidDTO
import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidMeasure
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid

fun Iterable<ProgrammeStateAid>.toDto() = map {
    ProgrammeStateAidDTO(
        id = it.id,
        measure = ProgrammeStateAidMeasure.valueOf(it.measure.name),
        name = it.name,
        abbreviatedName = it.abbreviatedName,
        schemeNumber = it.schemeNumber,
        maxIntensity = it.maxIntensity,
        threshold = it.threshold,
        comments = it.comments
    )
}

fun Iterable<ProgrammeStateAidDTO>.toModel() = map {
    ProgrammeStateAid(
        id = it.id,
        measure = ProgrammeStateAidMeasure.valueOf(it.measure.name),
        name = it.name,
        abbreviatedName = it.abbreviatedName,
        schemeNumber = it.schemeNumber,
        maxIntensity = it.maxIntensity,
        threshold = it.threshold,
        comments = it.comments
    )
}
