package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.programme.dto.InputProgrammeLanguage
import io.cloudflight.jems.api.programme.dto.OutputProgrammeLanguage
import io.cloudflight.jems.server.programme.entity.ProgrammeLanguage

fun InputProgrammeLanguage.toEntity() = ProgrammeLanguage(
    code = code,
    ui = ui,
    fallback = fallback,
    input = input
)

fun OutputProgrammeLanguage.toEntity() = ProgrammeLanguage(
    code = code,
    ui = ui,
    fallback = fallback,
    input = input
)

fun ProgrammeLanguage.toOutputProgrammeLanguage(): OutputProgrammeLanguage {
    return OutputProgrammeLanguage(
        code = code,
        ui = ui,
        fallback = fallback,
        input = input
    )
}
