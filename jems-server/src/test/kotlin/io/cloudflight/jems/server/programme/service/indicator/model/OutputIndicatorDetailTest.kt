package io.cloudflight.jems.server.programme.service.indicator.model

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.service.indicator.IndicatorsBaseTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class OutputIndicatorDetailTest : IndicatorsBaseTest() {

    @Test
    fun `should return differences between a ResultIndicatorDetail and it's updated version correctly`() {
        val outputIndicatorDetail = buildOutputIndicatorDetailInstance()
        val updatedOutputIndicatorDetail = buildOutputIndicatorDetailInstance(
            identifier = "ID11",
            code = "ioCODE-update",
            name = setOf(InputTranslation(SystemLanguage.EN, "new indicator title")),
            measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "new measurement unit")),
            finalTarget = BigDecimal.TEN,
            programmeObjectivePolicy = ProgrammeObjectivePolicy.AdvancedTechnologies,
            programmePriorityCode = indicatorProgrammePriorityCode,
            programmePriorityPolicyCode = indicatorProgrammeSpecificObjectiveCode,
            milestone = BigDecimal.ONE
        )
        val diff = outputIndicatorDetail.getDiff(updatedOutputIndicatorDetail)
        Assertions.assertThat(diff).contains(
            Assertions.entry(
                "identifier",
                Pair(outputIndicatorDetail.identifier, updatedOutputIndicatorDetail.identifier)
            ),
            Assertions.entry("code", Pair(outputIndicatorDetail.code, updatedOutputIndicatorDetail.code)),
            Assertions.entry("name", Pair(outputIndicatorDetail.name, updatedOutputIndicatorDetail.name)),
            Assertions.entry(
                "programmeObjectivePolicy",
                Pair(
                    outputIndicatorDetail.programmeObjectivePolicy,
                    updatedOutputIndicatorDetail.programmeObjectivePolicy
                )
            ),
            Assertions.entry(
                "measurementUnit",
                Pair(outputIndicatorDetail.measurementUnit, updatedOutputIndicatorDetail.measurementUnit)
            ),
            Assertions.entry(
                "milestone",
                Pair(outputIndicatorDetail.milestone, updatedOutputIndicatorDetail.milestone)
            ),

            Assertions.entry(
                "finalTarget",
                Pair(outputIndicatorDetail.finalTarget, updatedOutputIndicatorDetail.finalTarget)
            )
        )

    }
}
