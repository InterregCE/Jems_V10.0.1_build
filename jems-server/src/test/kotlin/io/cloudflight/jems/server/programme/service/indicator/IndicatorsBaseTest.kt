package io.cloudflight.jems.server.programme.service.indicator

import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.OutputIndicatorDetail
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicator
import io.cloudflight.jems.server.programme.service.indicator.model.ResultIndicatorDetail
import io.mockk.clearMocks
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.jupiter.api.BeforeEach
import java.math.BigDecimal


abstract class IndicatorsBaseTest : UnitTest() {

    @RelaxedMockK
    lateinit var auditService: AuditService

    @BeforeEach
    fun resetAuditService() {
        clearMocks(auditService)
    }

    private val indicatorId = 1L
    private val indicatorCode = "ioCODE"
    private val indicatorName = "indicator title"
    private val indicatorMeasurementUnit = "measurement unit"
    private val indicatorSourceOfData = "test source of data"
    private val indicatorComment = "test comment"
    private val indicatorIdentifier = "ID01"
    private val indicatorReferenceYear = "2022/2023"
    protected val indicatorProgrammePriorityCode = "prio_01"
    protected val indicatorProgrammeSpecificObjectiveCode = "RE"


    protected fun buildOutputIndicatorInstance(
        id: Long = indicatorId,
        identifier: String = indicatorIdentifier,
        code: String = indicatorCode,
        name: String = indicatorName,
        programmeObjectivePolicy: ProgrammeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
        measurementUnit: String = indicatorMeasurementUnit,
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
        name: String = indicatorName,
        programmeObjectivePolicy: ProgrammeObjectivePolicy = ProgrammeObjectivePolicy.RenewableEnergy,
        programmePriorityPolicyCode: String = indicatorProgrammeSpecificObjectiveCode,
        programmePriorityCode: String = indicatorProgrammePriorityCode,
        measurementUnit: String = indicatorMeasurementUnit,
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
