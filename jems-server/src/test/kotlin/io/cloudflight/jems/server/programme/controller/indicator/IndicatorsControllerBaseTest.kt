package io.cloudflight.jems.server.programme.controller.indicator

import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorCreateRequestDTO
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorUpdateRequestDTO
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorCreateRequestDTO
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorUpdateRequestDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorDetail
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorSummary
import java.math.BigDecimal


abstract class IndicatorsControllerBaseTest : UnitTest() {

    protected val indicatorId = 1L
    private val indicatorCode = "ioCODE"
    private val indicatorNameSet = setOf(InputTranslation(SystemLanguage.EN, "indicator title"))
    private val indicatorMeasurementUnitSet = setOf(InputTranslation(SystemLanguage.EN, "measurement unit"))
    private val indicatorSourceOfDataSet = setOf(InputTranslation(SystemLanguage.EN, "test source of data"))
    private val indicatorComment = setOf(InputTranslation(SystemLanguage.EN, "test comment"))
    private val indicatorIdentifier = "ID01"
    private val indicatorReferenceYear = "2022/2023"
    private val indicatorProgrammePriorityCode = "prio_01"
    private val indicatorProgrammeSpecificObjectiveCode = "RE"

    protected val outputIndicatorCreateRequestDTO =
        OutputIndicatorCreateRequestDTO(
            indicatorIdentifier,
            indicatorCode,
            indicatorNameSet,
            ProgrammeObjectivePolicy.RenewableEnergy,
            indicatorMeasurementUnitSet,
            BigDecimal.TEN,
            BigDecimal.ONE,
            null
        )
    protected val outputIndicatorUpdateRequestDTO =
        OutputIndicatorUpdateRequestDTO(
            indicatorId,
            indicatorIdentifier,
            indicatorCode,
            indicatorNameSet,
            ProgrammeObjectivePolicy.RenewableEnergy,
            indicatorMeasurementUnitSet,
            BigDecimal.TEN,
            BigDecimal.ONE,
            null
        )

    protected val resultIndicatorCreateRequestDTO =
        ResultIndicatorCreateRequestDTO(
            indicatorIdentifier,
            indicatorCode,
            indicatorNameSet,
            ProgrammeObjectivePolicy.RenewableEnergy,
            indicatorMeasurementUnitSet,
            BigDecimal.TEN,
            indicatorReferenceYear,
            BigDecimal.ONE,
            indicatorSourceOfDataSet,
            indicatorComment
        )
    protected val resultIndicatorUpdateRequestDTO =
        ResultIndicatorUpdateRequestDTO(
            indicatorId,
            indicatorIdentifier,
            indicatorCode,
            indicatorNameSet,
            ProgrammeObjectivePolicy.RenewableEnergy,
            indicatorMeasurementUnitSet,
            BigDecimal.TEN,
            indicatorReferenceYear,
            BigDecimal.ONE,
            indicatorSourceOfDataSet,
            indicatorComment
        )

    protected fun buildOutputIndicatorDetailInstance(
        id: Long = indicatorId,
        identifier: String = indicatorIdentifier,
        code: String = indicatorCode,
        name: Set<InputTranslation> = indicatorNameSet,
        programmeObjectivePolicy: ProgrammeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
        programmePriorityPolicyCode: String = indicatorProgrammeSpecificObjectiveCode,
        programmePriorityCode: String = indicatorProgrammePriorityCode,
        measurementUnit: Set<InputTranslation> = indicatorMeasurementUnitSet,
        milestone: BigDecimal = BigDecimal.TEN,
        finalTarget: BigDecimal = BigDecimal.ONE
    ) =
        OutputIndicatorDetail(
            id,
            identifier,
            code,
            name,
            programmeObjectivePolicy,
            programmePriorityPolicyCode,
            programmePriorityCode,
            measurementUnit,
            milestone,
            finalTarget,
            null
        )

    protected fun buildOutputIndicatorSummaryInstance(
        id: Long = indicatorId,
        identifier: String = indicatorIdentifier,
        code: String = indicatorCode,
        name: Set<InputTranslation> = indicatorNameSet,
        programmePriorityPolicyCode: String = indicatorProgrammeSpecificObjectiveCode,
        measurementUnit: Set<InputTranslation> = indicatorMeasurementUnitSet,
    ) =
        OutputIndicatorSummary(
            id,
            identifier,
            code,
            name,
            programmePriorityPolicyCode,
            measurementUnit
        )

    protected fun buildResultIndicatorSummaryInstance(
        id: Long = indicatorId,
        identifier: String = indicatorIdentifier,
        code: String = indicatorCode,
        name: Set<InputTranslation> = indicatorNameSet,
        programmePriorityCode: String = indicatorProgrammePriorityCode,
        measurementUnit: Set<InputTranslation> = indicatorMeasurementUnitSet,
        baseline: BigDecimal = BigDecimal.TEN
    ) =
        ResultIndicatorSummary(
            id,
            identifier,
            code,
            name,
            programmePriorityCode,
            measurementUnit,
            baseline
        )

    protected fun buildResultIndicatorDetailInstance(
        id: Long = indicatorId,
        identifier: String = indicatorIdentifier,
        code: String = indicatorCode,
        name: Set<InputTranslation> = indicatorNameSet,
        programmeObjectivePolicy: ProgrammeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
        programmePriorityPolicyCode: String = indicatorProgrammeSpecificObjectiveCode,
        programmePriorityCode: String = indicatorProgrammePriorityCode,
        measurementUnit: Set<InputTranslation> = indicatorMeasurementUnitSet,
        baseline: BigDecimal = BigDecimal.TEN,
        referenceYear: String = indicatorReferenceYear,
        finalTarget: BigDecimal = BigDecimal.ONE,
        sourceOfData: Set<InputTranslation> = indicatorSourceOfDataSet,
        comment: Set<InputTranslation> = indicatorComment
    ) =
        ResultIndicatorDetail(
            id,
            identifier,
            code,
            name,
            programmeObjectivePolicy,
            programmePriorityPolicyCode,
            programmePriorityCode,
            measurementUnit,
            baseline,
            referenceYear,
            finalTarget,
            sourceOfData,
            comment
        )
}
