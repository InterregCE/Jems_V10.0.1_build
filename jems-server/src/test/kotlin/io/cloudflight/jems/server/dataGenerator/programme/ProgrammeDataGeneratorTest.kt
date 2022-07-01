package io.cloudflight.jems.server.dataGenerator.programme

import io.cloudflight.jems.api.programme.OutputIndicatorApi
import io.cloudflight.jems.api.programme.ProgrammeStrategyApi
import io.cloudflight.jems.api.programme.ResultIndicatorApi
import io.cloudflight.jems.api.programme.costoption.ProgrammeLumpSumApi
import io.cloudflight.jems.api.programme.costoption.ProgrammeUnitCostApi
import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumDTO
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeLumpSumPhase
import io.cloudflight.jems.api.programme.dto.costoption.ProgrammeUnitCostDTO
import io.cloudflight.jems.api.programme.dto.indicator.OutputIndicatorCreateRequestDTO
import io.cloudflight.jems.api.programme.dto.indicator.ResultIndicatorCreateRequestDTO
import io.cloudflight.jems.api.programme.dto.language.ProgrammeLanguageDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.priority.ProgrammePriorityDTO
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeSpecificObjectiveDTO
import io.cloudflight.jems.api.programme.dto.strategy.InputProgrammeStrategy
import io.cloudflight.jems.api.programme.fund.ProgrammeFundApi
import io.cloudflight.jems.api.programme.language.ProgrammeLanguageApi
import io.cloudflight.jems.api.programme.legalstatus.ProgrammeLegalStatusApi
import io.cloudflight.jems.api.programme.priority.ProgrammePriorityApi
import io.cloudflight.jems.server.DataGeneratorTest
import io.cloudflight.jems.server.dataGenerator.*
import io.cloudflight.platform.test.openfeign.FeignTestClientFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.quickperf.sql.annotation.ExpectDelete
import org.quickperf.sql.annotation.ExpectInsert
import org.quickperf.sql.annotation.ExpectSelect
import org.quickperf.sql.annotation.ExpectUpdate
import org.springframework.boot.web.server.LocalServerPort
import java.math.BigDecimal


@Order(PROGRAMME_DATA_INITIALIZER_ORDER)
class ProgrammeDataGeneratorTest(@LocalServerPort private val port: Int) : DataGeneratorTest() {

    private val fundApi =
        FeignTestClientFactory.createClientApi(ProgrammeFundApi::class.java, port, config)

    private val priorityApi =
        FeignTestClientFactory.createClientApi(ProgrammePriorityApi::class.java, port, config)

    private val languageApi =
        FeignTestClientFactory.createClientApi(ProgrammeLanguageApi::class.java, port, config)

    private val outputIndicatorApi =
        FeignTestClientFactory.createClientApi(OutputIndicatorApi::class.java, port, config)

    private val resultIndicatorApi =
        FeignTestClientFactory.createClientApi(ResultIndicatorApi::class.java, port, config)

    private val strategyApi =
        FeignTestClientFactory.createClientApi(ProgrammeStrategyApi::class.java, port, config)

    private val legalStatusApi =
        FeignTestClientFactory.createClientApi(ProgrammeLegalStatusApi::class.java, port, config)

    private val programmeLumpSumApi =
        FeignTestClientFactory.createClientApi(ProgrammeLumpSumApi::class.java, port, config)

    private val programmeUnitCostApi =
        FeignTestClientFactory.createClientApi(ProgrammeUnitCostApi::class.java, port, config)

    @Test
    @Order(1)
    @ExpectSelect(11)
    @ExpectInsert(0)
    @ExpectUpdate(5)
    @ExpectDelete(1)
    fun `should set programme language for the programme`() {
        languageApi.update(
            setOf(
                ProgrammeLanguageDTO(SystemLanguage.EN, ui = true, fallback = true, input = true),
                ProgrammeLanguageDTO(SystemLanguage.FR, ui = true, fallback = false, input = false),
                ProgrammeLanguageDTO(SystemLanguage.BE, ui = true, fallback = false, input = false),
                ProgrammeLanguageDTO(SystemLanguage.DE, ui = false, fallback = false, input = true),
                ProgrammeLanguageDTO(SystemLanguage.HR, ui = false, fallback = false, input = true),
                ProgrammeLanguageDTO(SystemLanguage.CS, ui = false, fallback = false, input = true)
            )
        ).also {
            PROGRAMME_UI_LANGUAGES = it.filter { it.ui }.sortedBy { it.code.name }
            PROGRAMME_INPUT_LANGUAGES = it.filter { it.input }.sortedBy { it.code.name }
        }

        assertThat(PROGRAMME_UI_LANGUAGES.size).isEqualTo(3)
        assertThat(PROGRAMME_INPUT_LANGUAGES.size).isEqualTo(4)
    }

    @Test
    @ExpectSelect(11)
    @ExpectUpdate(7)
    @ExpectInsert(0)
    @ExpectDelete(2)
    fun `select all funds to be available for the calls`() {
        SELECTED_PROGRAMME_FUNDS =
            fundApi.updateProgrammeFundList(
                fundApi.getProgrammeFundList().map {
                    it.copy(selected = true)
                }.toSet()
            )

        assertThat(SELECTED_PROGRAMME_FUNDS.size).isEqualTo(7)
        assertThat(SELECTED_PROGRAMME_FUNDS.map { it.selected }).allMatch { true }
    }

    @Test
    @ExpectSelect(11)
    @ExpectInsert(0)
    @ExpectUpdate(13)
    @ExpectDelete(2)
    fun `should add strategy to the programme`() {
        PROGRAMME_STRATEGIES = strategyApi.updateProgrammeStrategies(
            strategyApi.getProgrammeStrategies().map { InputProgrammeStrategy(it.strategy, true) }
        )
        assertThat(PROGRAMME_STRATEGIES.size).isEqualTo(13)
    }

    @Test
    @ExpectSelect(9)
    @ExpectInsert(8)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should add lump sums to the programme`() {
        PROGRAMME_LUMP_SUMS = listOf(
            programmeLumpSumApi.createProgrammeLumpSum(
                ProgrammeLumpSumDTO(
                    id = null,
                    name = inputTranslation("name"),
                    description = inputTranslation("description"),
                    cost = BigDecimal.valueOf(23532, 2),
                    splittingAllowed = true,
                    phase = ProgrammeLumpSumPhase.Closure,
                    categories = setOf(
                        BudgetCategory.EquipmentCosts,
                        BudgetCategory.ExternalCosts,
                        BudgetCategory.StaffCosts
                    ),
                    fastTrack = false
                )
            )
        )
        assertThat(PROGRAMME_LUMP_SUMS).isNotNull
    }

    @Test
    @ExpectSelect(9)
    @ExpectInsert(7)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should add unit costs to the programme`() {
        PROGRAMME_UNIT_COSTS = listOf(
            programmeUnitCostApi.createProgrammeUnitCost(
                ProgrammeUnitCostDTO(
                    id = null,
                    name = inputTranslation("name"),
                    description = inputTranslation("description"),
                    type = inputTranslation("type"),
                    costPerUnit = BigDecimal.valueOf(123, 2),
                    costPerUnitForeignCurrency = null,
                    foreignCurrencyCode = null,
                    oneCostCategory = false,
                    categories = setOf(BudgetCategory.StaffCosts, BudgetCategory.ExternalCosts)
                )
            )
        )
        assertThat(PROGRAMME_UNIT_COSTS).isNotNull
    }

    @Test
    @ExpectSelect(5)
    @ExpectInsert(0)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should fetch programme legal statuses`() {
        PROGRAMME_LEGAL_STATUSES = legalStatusApi.getProgrammeLegalStatusList()
        assertThat(PROGRAMME_LEGAL_STATUSES.size).isEqualTo(2)
    }

    @Test
    @Order(2)
    @ExpectSelect(11)
    @ExpectInsert(6)
    @ExpectUpdate(1)
    @ExpectDelete(1)
    fun `should add programme priority to the programme`() {

        PROGRAMME_PRIORITY = priorityApi.create(
            ProgrammePriorityDTO(
                code = "001",
                title = inputTranslation("P001"),
                objective = ProgrammeObjective.PO2,
                specificObjectives = listOf(
                    ProgrammeSpecificObjectiveDTO(
                        ProgrammeObjectivePolicy.EnergyEfficiency, "1.1", "RSO2.1"
                    )
                )
            )
        )
        assertThat(PROGRAMME_PRIORITY).isNotNull
    }

    @Test
    @Order(3)
    @ExpectSelect(8)
    @ExpectInsert(5)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should add result indicator to the programme`() {

        PROGRAMME_RESULT_INDICATOR = resultIndicatorApi.createResultIndicator(
            ResultIndicatorCreateRequestDTO(
                identifier = "RI-1",
                code = "RCO02",
                name = inputTranslation("Enterprises supported (of which: micro, small, medium, large)"),
                programmeObjectivePolicy = ProgrammeObjectivePolicy.EnergyEfficiency,
                measurementUnit = inputTranslation("Euro"),
                baseline = BigDecimal.valueOf(484542, 2),
                finalTarget = BigDecimal.valueOf(54365, 2),
                referenceYear = "2022",
                sourceOfData = emptySet(),
                comment = "result indicator comment"
            )
        )

        assertThat(PROGRAMME_RESULT_INDICATOR).isNotNull

    }

    @Test
    @Order(4)
    @ExpectSelect(8)
    @ExpectInsert(5)
    @ExpectUpdate(0)
    @ExpectDelete(1)
    fun `should add output indicator to the programme`() {

        PROGRAMME_OUTPUT_INDICATOR = outputIndicatorApi.createOutputIndicator(
            OutputIndicatorCreateRequestDTO(
                identifier = "OI-1",
                code = "RCO01",
                name = inputTranslation("Enterprises supported (of which: micro, small, medium, large)"),
                programmeObjectivePolicy = ProgrammeObjectivePolicy.EnergyEfficiency,
                measurementUnit = inputTranslation("Euro"),
                milestone = BigDecimal.valueOf(21123, 2),
                finalTarget = BigDecimal.valueOf(54365, 2),
                resultIndicatorId = PROGRAMME_RESULT_INDICATOR.id
            )
        )
        assertThat(PROGRAMME_OUTPUT_INDICATOR).isNotNull

    }

}
