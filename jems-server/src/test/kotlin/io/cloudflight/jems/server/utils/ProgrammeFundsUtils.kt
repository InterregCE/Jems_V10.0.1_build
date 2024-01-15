package io.cloudflight.jems.server.utils

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFundType

val ERDF_FUND = ProgrammeFund(
    id = 1L, type = ProgrammeFundType.ERDF, selected = true,
    abbreviation = setOf(
        InputTranslation(
            SystemLanguage.EN, "EN ERDF"
        ),
        InputTranslation(SystemLanguage.SK, "SK ERDF")
    ),
    description = setOf(
        InputTranslation(SystemLanguage.EN, "EN desc"),
        InputTranslation(SystemLanguage.SK, "SK desc")
    )
)

val NDCI_FUND = ProgrammeFund(
    id = 5L, type = ProgrammeFundType.NDICI, selected = true,
    abbreviation = setOf(
        InputTranslation(
            SystemLanguage.EN, "EN NDCI"
        ),
        InputTranslation(SystemLanguage.SK, "SK NDCI")
    ),
    description = setOf(
        InputTranslation(SystemLanguage.EN, "EN desc"),
        InputTranslation(SystemLanguage.SK, "SK desc")
    )
)
val IPA_III_FUND = ProgrammeFund(
    id = 4L, type = ProgrammeFundType.IPA_III, selected = true,
    abbreviation = setOf(
        InputTranslation(
            SystemLanguage.EN, "EN IPA_III"
        ),
        InputTranslation(SystemLanguage.SK, "SK IPA_III")
    ),
    description = setOf(
        InputTranslation(SystemLanguage.EN, "EN desc"),
        InputTranslation(SystemLanguage.SK, "SK desc")
    )
)
