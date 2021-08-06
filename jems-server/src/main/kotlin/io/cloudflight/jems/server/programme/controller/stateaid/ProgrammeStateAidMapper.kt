package io.cloudflight.jems.server.programme.controller.stateaid

import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidDTO
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid

//fun ProgrammeStateAid.toDto() = ProgrammeStateAidDTO(
//    id = id,
//    measure = measure,
//    name = name,
//    abbreviatedName = abbreviatedName,
//    schemeNumber = schemeNumber,
//    maxIntensity = maxIntensity,
//    threshold = threshold,
//    comments = comments
//)

fun Iterable<ProgrammeStateAid>.toDto() = map {
    ProgrammeStateAidDTO(
        id = it.id,
        measure = it.measure,
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
        measure = it.measure,
        name = it.name,
        abbreviatedName = it.abbreviatedName,
        schemeNumber = it.schemeNumber,
        maxIntensity = it.maxIntensity,
        threshold = it.threshold,
        comments = it.comments
    )
}
