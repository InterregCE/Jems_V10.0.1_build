package io.cloudflight.jems.server.programme.repository.indicator

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.programme.entity.ProgrammePriorityEntity
import io.cloudflight.jems.server.programme.entity.ProgrammeSpecificObjectiveEntity
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorEntity
import io.cloudflight.jems.server.programme.entity.indicator.OutputIndicatorTranslEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorEntity
import io.cloudflight.jems.server.programme.entity.indicator.ResultIndicatorTranslEntity
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
    private val indicatorNameSet = setOf(InputTranslation(SystemLanguage.EN, "indicator title"))
    private val indicatorMeasurementUnitSet = setOf(InputTranslation(SystemLanguage.EN, "measurement unit"))
    private val indicatorSourceOfDataSet = setOf(InputTranslation(SystemLanguage.EN, "test source of data"))
    private val indicatorComment = setOf(InputTranslation(SystemLanguage.EN, "test comment"))
    protected val indicatorIdentifier = "ID01"
    private val indicatorReferenceYear = "2022/2023"
    private val indicatorResultIndicatorId = 2L

    protected val defaultResultIndicatorEntity = buildResultIndicatorEntityInstance()

    protected fun buildOutputIndicatorInstance(
        id: Long = indicatorId,
        identifier: String = indicatorIdentifier,
        code: String = indicatorCode,
        name: Set<InputTranslation> = indicatorNameSet,
        programmeObjectivePolicy: ProgrammeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
        measurementUnit: Set<InputTranslation> = indicatorMeasurementUnitSet,
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
        name: Set<InputTranslation> = indicatorNameSet,
        programmePriorityPolicy: ProgrammeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
        measurementUnit: Set<InputTranslation> = indicatorMeasurementUnitSet,
        baseline: BigDecimal = BigDecimal.TEN,
        referenceYear: String = indicatorReferenceYear,
        finalTarget: BigDecimal = BigDecimal.ONE,
        sourceOfData: Set<InputTranslation> = indicatorSourceOfDataSet,
        comment: Set<InputTranslation> = indicatorComment
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
        name: Set<InputTranslation> = indicatorNameSet,
        programmeSpecificObjectiveEntity: ProgrammeSpecificObjectiveEntity = indicatorProgrammeSpecificObjectiveEntity,
        measurementUnit: Set<InputTranslation> = indicatorMeasurementUnitSet,
        milestone: BigDecimal = BigDecimal.TEN,
        finalTarget: BigDecimal = BigDecimal.ONE,
        resultIndicatorEntity: ResultIndicatorEntity = defaultResultIndicatorEntity,
    ) =
        OutputIndicatorEntity(
            id,
            identifier,
            code,
            resultIndicatorEntity,
            programmeSpecificObjectiveEntity,
            milestone,
            finalTarget,
            translatedValues = mutableSetOf()
        ).apply {
            translatedValues.add(
                OutputIndicatorTranslEntity(
                    TranslationId(
                        this, SystemLanguage.EN
                    ),
                    name = name.first { it.language == SystemLanguage.EN }.translation!!,
                    measurementUnit = measurementUnit.first { it.language == SystemLanguage.EN }.translation
                )
            )
        }

    protected fun buildResultIndicatorEntityInstance(
        id: Long = indicatorId,
        identifier: String = indicatorIdentifier,
        code: String = indicatorCode,
        name: Set<InputTranslation> = indicatorNameSet,
        measurementUnit: Set<InputTranslation> = indicatorMeasurementUnitSet,
        baseline: BigDecimal = BigDecimal.TEN,
        referenceYear: String = indicatorReferenceYear,
        finalTarget: BigDecimal = BigDecimal.ONE,
        sourceOfData: Set<InputTranslation> = indicatorSourceOfDataSet,
        comment: Set<InputTranslation> = indicatorComment
    ) =
        ResultIndicatorEntity(
            id,
            identifier,
            code,
            indicatorProgrammeSpecificObjectiveEntity,
            baseline,
            referenceYear,
            finalTarget,
        ).apply {
            translatedValues.add(
                ResultIndicatorTranslEntity(
                    TranslationId(
                        this, SystemLanguage.EN
                    ),
                    name = name.first { it.language == SystemLanguage.EN }.translation!!,
                    measurementUnit = measurementUnit.first { it.language == SystemLanguage.EN }.translation,
                    sourceOfData = sourceOfData.first { it.language == SystemLanguage.EN }.translation,
                    comment = comment.first { it.language == SystemLanguage.EN }.translation
                )
            )
        }
}
