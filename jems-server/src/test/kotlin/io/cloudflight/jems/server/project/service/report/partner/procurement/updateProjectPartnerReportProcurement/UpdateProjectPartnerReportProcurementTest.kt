package io.cloudflight.jems.server.project.service.report.partner.procurement.updateProjectPartnerReportProcurement

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurement
import io.cloudflight.jems.server.project.service.report.model.procurement.ProjectPartnerReportProcurementUpdate
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

internal class UpdateProjectPartnerReportProcurementTest : UnitTest() {

    companion object {
        private const val PARTNER_ID = 5922L

        private const val EXISTING_CONTRACT_ID = "already existing"

        private fun procurement(id: Long, reportId: Long) = ProjectPartnerReportProcurement(
            id = id,
            reportId = reportId,
            reportNumber = 1,
            createdInThisReport = false,
            contractId = "contract",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN")),
            contractAmount = BigDecimal.TEN,
            supplierName = "supplierName",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN")),
        )

        private fun procurementUpdate(id: Long) = ProjectPartnerReportProcurementUpdate(
            id = id,
            contractId = "contract NEW",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN NEW")),
            contractAmount = BigDecimal.ONE,
            supplierName = "supplierName NEW",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN NEW")),
        )

        private fun procurementNew(id: Long, reportId: Long) = ProjectPartnerReportProcurement(
            id = id,
            reportId = reportId,
            reportNumber = 1,
            createdInThisReport = false,
            contractId = "contract NEW",
            contractType = setOf(InputTranslation(SystemLanguage.EN, "contractType EN NEW")),
            contractAmount = BigDecimal.ONE,
            supplierName = "supplierName NEW",
            comment = setOf(InputTranslation(SystemLanguage.EN, "comment EN NEW")),
        )

    }

    @MockK
    lateinit var reportPersistence: ProjectReportPersistence

    @MockK
    lateinit var reportProcurementPersistence: ProjectReportProcurementPersistence

    @MockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    lateinit var interactor: UpdateProjectPartnerReportProcurement

    @BeforeEach
    fun setup() {
        clearMocks(reportProcurementPersistence)
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { true }) } returns Unit
        every { generalValidator.maxLength(any<String>(), any(), any()) } returns emptyMap()
        every { generalValidator.maxLength(any<Set<InputTranslation>>(), any(), any()) } returns emptyMap()
        every { generalValidator.numberBetween(any<BigDecimal>(), any(), any(), any()) } returns emptyMap()
    }


    @Test
    fun `update successful - ignore not existing, create new one, update existing one`() {
        val existingProcurementId = 757L
        val currentReportId = 85L
        val previousReportId = 84L

        // validation is not validated, but we will verify it has been called
        val slotString = mutableListOf<String>()
        val slotStringName = mutableListOf<String>()
        val slotTranslatedString = mutableListOf<Set<InputTranslation>>()
        val slotTranslatedStringName = mutableListOf<String>()
        val slotBigDecimal = mutableListOf<BigDecimal>()
        val slotBigDecimalName = mutableListOf<String>()
        every { generalValidator.maxLength(capture(slotString), any(), capture(slotStringName)) } returns emptyMap()
        every { generalValidator.maxLength(capture(slotTranslatedString), any(), capture(slotTranslatedStringName)) } returns emptyMap()
        every { generalValidator.numberBetween(capture(slotBigDecimal), any(), any(), capture(slotBigDecimalName)) } returns emptyMap()
        // end of validation mock

        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = currentReportId) } returns setOf(previousReportId)
        every { reportProcurementPersistence.countProcurementsForReportIds(reportIds = setOf(previousReportId)) } returns 1
        every { reportProcurementPersistence.getProcurementContractIdsForReportIds(reportIds = setOf(previousReportId)) } returns setOf(EXISTING_CONTRACT_ID)
        every { reportProcurementPersistence.getProcurementIdsForReport(PARTNER_ID, reportId = currentReportId) } returns setOf(existingProcurementId)

        val dataSaved = slot<List<ProjectPartnerReportProcurementUpdate>>()
        every {
            reportProcurementPersistence.updatePartnerReportProcurement(
                PARTNER_ID,
                reportId = currentReportId,
                procurementNew = capture(dataSaved),
            )
        } answers { }

        // after update mock correct result
        every { reportProcurementPersistence.getProcurementsForReportIds(setOf(previousReportId, currentReportId)) } returns
            listOf(
                procurementNew(id = 0L, reportId = currentReportId) /* has just been created */,
                procurementNew(id = existingProcurementId, reportId = currentReportId) /* existing before from current report */,
                procurement(id = 52L, reportId = previousReportId) /* from older report */,
            )

        // test update here:
        val toBeChanged = listOf(
            procurementUpdate(id = -1L).copy(contractId = "something unique") /* not existing - should be ignored */,
            procurementUpdate(id = existingProcurementId) /* existing - should be updated */,
            procurementUpdate(id = 0L).copy(contractId = "created") /* not - existing - should be created */,
        )
        assertThat(interactor.update(PARTNER_ID, reportId = currentReportId, procurementNew = toBeChanged))
            .containsExactly(
                procurementNew(id = 0L, reportId = currentReportId).copy(createdInThisReport = true) /* has just been created */,
                procurementNew(id = existingProcurementId, reportId = currentReportId)
                    .copy(createdInThisReport = true) /* existing before from current report */,
                procurement(id = 52L, reportId = previousReportId) /* from older report */,
            )

        assertUpdatedData(dataSaved.captured, existingProcurementId)

        assertValidationsCalledString(slotString = slotString, slotStringName = slotStringName)
        assertValidationsCalledTranslatedStrings(slotTranslatedString = slotTranslatedString, slotTranslatedStringName = slotTranslatedStringName)
        assertValidationsCalledAmount(slotBigDecimal = slotBigDecimal, slotBigDecimalName = slotBigDecimalName)
    }

    private fun assertUpdatedData(
        dataSaved: List<ProjectPartnerReportProcurementUpdate>,
        existingProcurementId: Long,
    ) {
        assertThat(dataSaved).containsExactly(
            procurementUpdate(id = existingProcurementId),
            procurementUpdate(id = 0L).copy(contractId = "created"),
        )
        assertThat(dataSaved.map { it.id }).doesNotContain(-1L)
    }

    private fun assertValidationsCalledString(
        slotString: MutableList<String>,
        slotStringName: MutableList<String>,
    ) {
        assertThat(slotString).containsExactly(
            "something unique" /* -1L */,
            "contract NEW" /* existingProcurementId */,
            "created" /* 0L */,
            "supplierName NEW" /* -1L */,
            "supplierName NEW" /* existingProcurementId */,
            "supplierName NEW" /* 0L */,
        )
        assertThat(slotStringName).containsExactly(
            "contractId[0]",
            "contractId[1]",
            "contractId[2]",
            "supplierName[0]",
            "supplierName[1]",
            "supplierName[2]",
        )
    }

    private fun assertValidationsCalledTranslatedStrings(
        slotTranslatedString: MutableList<Set<InputTranslation>>,
        slotTranslatedStringName: MutableList<String>,
    ) {
        assertThat(slotTranslatedString).containsExactly(
            setOf(InputTranslation(SystemLanguage.EN, "contractType EN NEW")) /* -1L */,
            setOf(InputTranslation(SystemLanguage.EN, "contractType EN NEW")) /* existingProcurementId */,
            setOf(InputTranslation(SystemLanguage.EN, "contractType EN NEW")) /* 0L */,
            setOf(InputTranslation(SystemLanguage.EN, "comment EN NEW")) /* -1L */,
            setOf(InputTranslation(SystemLanguage.EN, "comment EN NEW")) /* existingProcurementId */,
            setOf(InputTranslation(SystemLanguage.EN, "comment EN NEW")) /* 0L */,
        )
        assertThat(slotTranslatedStringName).containsExactly(
            "contractType[0]",
            "contractType[1]",
            "contractType[2]",
            "comment[0]",
            "comment[1]",
            "comment[2]",
        )
    }

    private fun assertValidationsCalledAmount(
        slotBigDecimal: MutableList<BigDecimal>,
        slotBigDecimalName: MutableList<String>,
    ) {
        assertThat(slotBigDecimal).containsExactly(
            BigDecimal.ONE /* -1L */,
            BigDecimal.ONE /* existingProcurementId */,
            BigDecimal.ONE /* 0L */,
        )
        assertThat(slotBigDecimalName).containsExactly("contractAmount[0]", "contractAmount[1]", "contractAmount[2]")
    }

    @Test
    fun `update unsuccessful - max amount reached`() {
        val MAX_AMOUNT = 50L

        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = 71L) } returns setOf(70L)
        every { reportProcurementPersistence.countProcurementsForReportIds(reportIds = setOf(70L)) } returns MAX_AMOUNT

        val toBeChanged = listOf(
            procurementUpdate(id = 0L).copy(contractId = "created"), /* not - existing - should be created */
        )
        assertThrows<MaxAmountOfProcurementsReachedException> {
            interactor.update(PARTNER_ID, reportId = 71L, procurementNew = toBeChanged)
        }
    }

    @Test
    fun `update unsuccessful - provided contractIds are not unique`() {
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = 21L) } returns setOf(20L)
        every { reportProcurementPersistence.countProcurementsForReportIds(reportIds = setOf(20L)) } returns 0
        every { reportProcurementPersistence.getProcurementContractIdsForReportIds(reportIds = setOf(20L)) } returns emptySet()

        val toBeChanged = listOf(
            procurementUpdate(id = 1L).copy(contractId = "same"),
            procurementUpdate(id = 2L).copy(contractId = "same"),
        )
        val ex = assertThrows<ContractIdsAreNotUnique> {
            interactor.update(PARTNER_ID, reportId = 21L, procurementNew = toBeChanged)
        }
        assertThat(ex.message).isEqualTo("duplicates: [same]")
    }

    @Test
    fun `update unsuccessful - provided contractId is already in use`() {
        every { reportPersistence.getReportIdsBefore(PARTNER_ID, beforeReportId = 31L) } returns setOf(30L)
        every { reportProcurementPersistence.countProcurementsForReportIds(reportIds = setOf(30L)) } returns 1
        every { reportProcurementPersistence.getProcurementContractIdsForReportIds(reportIds = setOf(30L)) } returns
            setOf("used")

        val ex = assertThrows<ContractIdsAreNotUnique> {
            interactor.update(PARTNER_ID, reportId = 31L, procurementNew = listOf(
                procurementUpdate(id = 0L).copy(contractId = "used"),
            ))
        }
        assertThat(ex.message).isEqualTo("duplicates: [used]")
    }

}
