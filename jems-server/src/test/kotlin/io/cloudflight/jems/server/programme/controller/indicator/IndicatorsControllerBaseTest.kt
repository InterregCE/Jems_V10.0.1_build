package io.cloudflight.jems.server.programme.controller.indicator

import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorCreateRequestDTO
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorUpdateRequestDTO
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorCreateRequestDTO
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorUpdateRequestDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorSummary
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorDetail
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorSummary
import java.math.BigDecimal


abstract class IndicatorsControllerBaseTest : UnitTest() {

    protected val indicatorId = 1L
    private val indicatorCode = "ioCODE"
    private val indicatorName = "indicator title"
    private val indicatorMeasurementUnit = "measurement unit"
    private val indicatorSourceOfData = "test source of data"
    private val indicatorComment = "test comment"
    private val indicatorIdentifier = "ID01"
    private val indicatorReferenceYear = "2022/2023"
    private val indicatorProgrammePriorityCode = "prio_01"
    private val indicatorProgrammeSpecificObjectiveCode = "RE"

    protected val outputIndicatorCreateRequestDTO =
        OutputIndicatorCreateRequestDTO(
            indicatorIdentifier,
            indicatorCode,
            indicatorName,
            ProgrammeObjectivePolicy.RenewableEnergy,
            indicatorMeasurementUnit,
            BigDecimal.TEN,
            BigDecimal.ONE,
            null
        )
    protected val outputIndicatorUpdateRequestDTO =
        OutputIndicatorUpdateRequestDTO(
            indicatorId,
            indicatorIdentifier,
            indicatorCode,
            indicatorName,
            ProgrammeObjectivePolicy.RenewableEnergy,
            indicatorMeasurementUnit,
            BigDecimal.TEN,
            BigDecimal.ONE,
            null
        )

    protected val resultIndicatorCreateRequestDTO =
        ResultIndicatorCreateRequestDTO(
            indicatorIdentifier,
            indicatorCode,
            indicatorName,
            ProgrammeObjectivePolicy.RenewableEnergy,
            indicatorMeasurementUnit,
            BigDecimal.TEN,
            indicatorReferenceYear,
            BigDecimal.ONE,
            indicatorSourceOfData,
            indicatorComment
        )
    protected val resultIndicatorUpdateRequestDTO =
        ResultIndicatorUpdateRequestDTO(
            indicatorId,
            indicatorIdentifier,
            indicatorCode,
            indicatorName,
            ProgrammeObjectivePolicy.RenewableEnergy,
            indicatorMeasurementUnit,
            BigDecimal.TEN,
            indicatorReferenceYear,
            BigDecimal.ONE,
            indicatorSourceOfData,
            indicatorComment
        )

    protected fun buildOutputIndicatorDetailInstance(
        id: Long = indicatorId,
        identifier: String = indicatorIdentifier,
        code: String = indicatorCode,
        name: String = indicatorName,
        programmeObjectivePolicy: ProgrammeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
        programmePriorityPolicyCode: String = indicatorProgrammeSpecificObjectiveCode,
        programmePriorityCode: String = indicatorProgrammePriorityCode,
        measurementUnit: String = indicatorMeasurementUnit,
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
        name: String = indicatorName,
        programmePriorityPolicyCode: String = indicatorProgrammeSpecificObjectiveCode,
        measurementUnit: String = indicatorMeasurementUnit,
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
        name: String = indicatorName,
        programmePriorityCode: String = indicatorProgrammePriorityCode,
        measurementUnit: String = indicatorMeasurementUnit,
    ) =
        ResultIndicatorSummary(
            id,
            identifier,
            code,
            name,
            programmePriorityCode,
            measurementUnit,
        )

    protected fun buildResultIndicatorDetailInstance(
        id: Long = indicatorId,
        identifier: String = indicatorIdentifier,
        code: String = indicatorCode,
        name: String = indicatorName,
        programmeObjectivePolicy: ProgrammeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
        programmePriorityPolicyCode: String = indicatorProgrammeSpecificObjectiveCode,
        programmePriorityCode: String = indicatorProgrammePriorityCode,
        measurementUnit: String = indicatorMeasurementUnit,
        baseline: BigDecimal = BigDecimal.TEN,
        referenceYear: String = indicatorReferenceYear,
        finalTarget: BigDecimal = BigDecimal.ONE,
        sourceOfData: String = indicatorSourceOfData,
        comment: String = indicatorComment
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
