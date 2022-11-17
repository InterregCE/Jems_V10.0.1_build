package io.cloudflight.jems.server.programme.service.indicator

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorDetail
import io.mockk.clearMocks
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.jupiter.api.BeforeEach
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal


abstract class IndicatorsBaseTest : UnitTest() {

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @BeforeEach
    fun resetAuditService() {
        clearMocks(auditPublisher)
    }

    private val indicatorId = 1L
    private val indicatorCode = "ioCODE"
    protected val indicatorNameSet = setOf(InputTranslation(SystemLanguage.EN, "indicator title"))
    protected val indicatorMeasurementUnitSet = setOf(InputTranslation(SystemLanguage.EN, "measurement unit"))
    protected val indicatorSourceOfDataSet = setOf(InputTranslation(SystemLanguage.EN, "test source of data"))
    private val indicatorComment = setOf(InputTranslation(SystemLanguage.EN, "test comment"))
    private val indicatorIdentifier = "ID01"
    private val indicatorReferenceYear = "2022/2023"
    protected val indicatorProgrammePriorityCode = "prio_01"
    protected val indicatorProgrammeSpecificObjectiveCode = "RE"


    protected fun buildOutputIndicatorInstance(
        id: Long = indicatorId,
        identifier: String = indicatorIdentifier,
        code: String = indicatorCode,
        name: Set<InputTranslation> = indicatorNameSet,
        programmeObjectivePolicy: ProgrammeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
        measurementUnit: Set<InputTranslation> = indicatorMeasurementUnitSet,
        milestone: BigDecimal = BigDecimal.TEN,
        finalTarget: BigDecimal = BigDecimal.ONE,
        resultIndicatorId: Long? = null
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
        finalTarget: BigDecimal = BigDecimal.ONE,
        resultIndicatorDetail: ResultIndicatorDetail? = null
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
            resultIndicatorDetail
        )

    protected fun buildResultIndicatorInstance(
        id: Long = indicatorId,
        identifier: String = indicatorIdentifier,
        code: String = indicatorCode,
        name: Set<InputTranslation> = indicatorNameSet,
        programmeObjectivePolicy: ProgrammeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
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
            programmeObjectivePolicy,
            measurementUnit,
            baseline,
            referenceYear,
            finalTarget,
            sourceOfData,
            comment
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
