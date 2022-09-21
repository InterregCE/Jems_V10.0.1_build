package io.cloudflight.jems.server.programme.service.indicator.model

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.service.indicator.IndicatorsBaseTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class ResultIndicatorDetailTest : IndicatorsBaseTest() {

    @Test
    fun `should return differences between a ResultIndicatorDetail and it's updated version correctly`() {
        val resultIndicatorDetail = buildResultIndicatorDetailInstance()
        val updatedResultIndicatorDetail = buildResultIndicatorDetailInstance(
            identifier = "ID11",
            code = "ioCODE-update",
            name = setOf(InputTranslation(SystemLanguage.EN, "indicator title2")),
            measurementUnit = setOf(InputTranslation(SystemLanguage.EN, "new measurement unit")),
            baseline = BigDecimal.ONE,
            referenceYear = "2022/2024",
            finalTarget = BigDecimal.TEN,
            sourceOfData = setOf(InputTranslation(SystemLanguage.EN, "test source of data update")),
            comment = setOf(InputTranslation(SystemLanguage.EN, "test comment update")),
            programmeObjectivePolicy = ProgrammeObjectivePolicy.AdvancedTechnologies,
            programmePriorityCode = indicatorProgrammePriorityCode,
            programmePriorityPolicyCode = indicatorProgrammeSpecificObjectiveCode
        )
        val diff = resultIndicatorDetail.getDiff(updatedResultIndicatorDetail)
        assertThat(diff).contains(
            entry("identifier", Pair(resultIndicatorDetail.identifier, updatedResultIndicatorDetail.identifier)),
            entry("code", Pair(resultIndicatorDetail.code, updatedResultIndicatorDetail.code)),
            entry("name", Pair(resultIndicatorDetail.name, updatedResultIndicatorDetail.name)),
            entry(
                "programmeObjectivePolicy",
                Pair(
                    resultIndicatorDetail.programmeObjectivePolicy,
                    updatedResultIndicatorDetail.programmeObjectivePolicy
                )
            ),
            entry(
                "measurementUnit",
                Pair(resultIndicatorDetail.measurementUnit, updatedResultIndicatorDetail.measurementUnit)
            ),
            entry("baseline", Pair(resultIndicatorDetail.baseline, updatedResultIndicatorDetail.baseline)),
            entry(
                "referenceYear",
                Pair(resultIndicatorDetail.referenceYear, updatedResultIndicatorDetail.referenceYear)
            ),
            entry("finalTarget", Pair(resultIndicatorDetail.finalTarget, updatedResultIndicatorDetail.finalTarget)),
            entry("sourceOfData", Pair(resultIndicatorDetail.sourceOfData, updatedResultIndicatorDetail.sourceOfData)),
            entry("comment", Pair(resultIndicatorDetail.comment, updatedResultIndicatorDetail.comment)),
        )

    }
}
