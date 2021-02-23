package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicator
import java.math.BigDecimal


abstract class IndicatorsPersistenceBaseTest : UnitTest() {

    protected val priority = ProgrammePriorityEntity(
        id = 1,
        code = "prio_01",
        objective = ProgrammeObjective.PO2,
        specificObjectives = emptySet() // not used here
    )

    protected val indicatorProgrammeSpecificObjectiveEntity = ProgrammeSpecificObjectiveEntity(
        programmeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
        code = "RE",
        programmePriority = priority
    )
    protected val indicatorId = 1L
    private val indicatorCode = "ioCODE"
    private val indicatorName = "indicator title"
    private val indicatorMeasurementUnit = "measurement unit"
    private val indicatorSourceOfData = "test source of data"
    private val indicatorComment = "test comment"
    protected val indicatorIdentifier = "ID01"
    private val indicatorReferenceYear = "2022/2023"
    private val indicatorResultIndicatorId = 2L

    protected val defaultResultIndicatorEntity= buildResultIndicatorEntityInstance()

    protected fun buildOutputIndicatorInstance(
        id: Long = indicatorId,
        identifier: String = indicatorIdentifier,
        code: String = indicatorCode,
        name: String = indicatorName,
        programmeObjectivePolicy: ProgrammeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
        measurementUnit: String = indicatorMeasurementUnit,
        milestone: BigDecimal = BigDecimal.TEN,
        finalTarget: BigDecimal = BigDecimal.ONE,
        resultIndicatorId: Long? = indicatorResultIndicatorId
    ) =
        OutputIndicator(
            id,
            identifier,
            code,
            name,
            resultIndicatorId,
            programmeObjectivePolicy,
            measurementUnit,
            milestone,
            finalTarget
        )


    protected fun buildResultIndicatorInstance(
        id: Long = indicatorId,
        identifier: String = indicatorIdentifier,
        code: String = indicatorCode,
        name: String = indicatorName,
        programmePriorityPolicy: ProgrammeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
        measurementUnit: String = indicatorMeasurementUnit,
        baseline: BigDecimal = BigDecimal.TEN,
        referenceYear: String = indicatorReferenceYear,
        finalTarget: BigDecimal = BigDecimal.ONE,
        sourceOfData: String = indicatorSourceOfData,
        comment: String = indicatorComment
    ) =
        ResultIndicator(
            id,
            identifier,
            code,
            name,
            programmePriorityPolicy,
            measurementUnit,
            baseline,
            referenceYear,
            finalTarget,
            sourceOfData,
            comment
        )

    protected fun buildOutputIndicatorEntityInstance(
        id: Long = indicatorId,
        identifier: String = indicatorIdentifier,
        code: String = indicatorCode,
        name: String = indicatorName,
        programmeSpecificObjectiveEntity: ProgrammeSpecificObjectiveEntity = indicatorProgrammeSpecificObjectiveEntity,
        measurementUnit: String = indicatorMeasurementUnit,
        milestone: BigDecimal = BigDecimal.TEN,
        finalTarget: BigDecimal = BigDecimal.ONE,
        resultIndicatorEntity: ResultIndicatorEntity= defaultResultIndicatorEntity,
    ) =
        OutputIndicatorEntity(
            id,
            identifier,
            code,
            name,
            resultIndicatorEntity,
            programmeSpecificObjectiveEntity,
            measurementUnit,
            milestone,
            finalTarget
        )

    protected fun buildResultIndicatorEntityInstance(
        id: Long = indicatorId,
        identifier: String = indicatorIdentifier,
        code: String = indicatorCode,
        name: String = indicatorName,
        measurementUnit: String = indicatorMeasurementUnit,
        baseline: BigDecimal = BigDecimal.TEN,
        referenceYear: String = indicatorReferenceYear,
        finalTarget: BigDecimal = BigDecimal.ONE,
        sourceOfData: String = indicatorSourceOfData,
        comment: String = indicatorComment
    ) =
        ResultIndicatorEntity(
            id,
            identifier,
            code,
            name,
            indicatorProgrammeSpecificObjectiveEntity,
            measurementUnit,
            baseline,
            referenceYear,
            finalTarget,
            sourceOfData,
            comment
        )
}
