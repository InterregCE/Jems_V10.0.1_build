package io.cloudflight.jems.server.programme.service.priority.update_priority

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjective
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.CircularEconomy
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.EnergyEfficiency
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.GreenInfrastructure
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.RenewableEnergy
import io.cloudflight.jems.api.programme.dto.priority.ProgrammeObjectivePolicy.WaterManagement
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.common.exception.I18nFieldError
import io.cloudflight.jems.server.common.exception.I18nValidationException
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.priority.ProgrammePriorityPersistence
import io.cloudflight.jems.server.programme.service.priority.getStringOfLength
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeObjectiveDimension
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammePriority
import io.cloudflight.jems.server.programme.service.priority.model.ProgrammeSpecificObjective
import io.cloudflight.jems.server.programme.service.priority.testPriority
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus

@ExtendWith(MockKExtension::class)
class UpdatePriorityInteractorTest {

    private val inputErrorMap = mapOf("error" to I18nMessage("error.key"))

    companion object {
        private const val ID = 3L
        private val toUpdatePriority = ProgrammePriority(
            code = "PO-02",
            title = setOf(InputTranslation(SystemLanguage.EN, "PO-02 title")),
            objective = ProgrammeObjective.PO2,
            specificObjectives = listOf(
                ProgrammeSpecificObjective(programmeObjectivePolicy = GreenInfrastructure, code = "GU"),
                ProgrammeSpecificObjective(programmeObjectivePolicy = CircularEconomy, code = "CE"),
                ProgrammeSpecificObjective(programmeObjectivePolicy = WaterManagement, code = "WM"),
            ),
        )

    }

    @RelaxedMockK
    lateinit var persistence: ProgrammePriorityPersistence

    @RelaxedMockK
    lateinit var isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor

    @RelaxedMockK
    lateinit var auditService: AuditService

    @RelaxedMockK
    lateinit var generalValidator: GeneralValidatorService

    @InjectMockKs
    private lateinit var updatePriority: UpdatePriority

    @BeforeEach
    fun reset() {
        clearMocks(generalValidator)
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isEmpty() }) } returns Unit
        every { generalValidator.throwIfAnyIsInvalid(*varargAny { it.isNotEmpty() }) } throws AppInputValidationException(
            inputErrorMap
        )
    }

    @Test
    fun `updatePriority - valid`() {
        val priority = testPriority.copy(
            id = ID,
            code = "_old_",
            title = setOf(InputTranslation(SystemLanguage.EN, "_oldTitle_"))
        )
        every { persistence.getPriorityById(ID) } returns priority
        // code and title are not used
        every { persistence.getPriorityIdByCode(toUpdatePriority.code) } returns null
        // we can find existing one
        every { persistence.getPriorityIdForPolicyIfExists(RenewableEnergy) } returns ID
        every { persistence.getPriorityIdForPolicyIfExists(GreenInfrastructure) } returns ID
        // new "to be set" are not used
        every { persistence.getPriorityIdForPolicyIfExists(CircularEconomy) } returns null
        every { persistence.getPriorityIdForPolicyIfExists(WaterManagement) } returns null

        every { persistence.getPrioritiesBySpecificObjectiveCodes(setOf("GU", "CE", "WM")) } returns listOf(priority)
        every { isProgrammeSetupLocked.isLocked() } returns false
        // nothing is yet used by a Call
        every { persistence.getObjectivePoliciesAlreadyInUse() } returns emptySet()
        every { persistence.update(any()) } returnsArgument 0
        val auditSlot = slot<AuditCandidate>()
        every { auditService.logEvent(capture(auditSlot)) } answers {}

        assertThat(updatePriority.updatePriority(ID, toUpdatePriority)).isEqualTo(toUpdatePriority.copy(id = ID))
        assertThat(auditSlot.captured).isEqualTo(AuditCandidate(
            action = AuditAction.PROGRAMME_PRIORITY_UPDATED,
            description = "Programme priority data changed for '_old_' '[EN=_oldTitle_]':\n" +
                "code changed from _old_ to PO-02,\n" +
                "title changed from [EN=_oldTitle_] to [EN=PO-02 title],\n" +
                "specificObjectives changed from [RenewableEnergy, GreenInfrastructure] to [WaterManagement, CircularEconomy, GreenInfrastructure]",
        ))
    }

    @Test
    fun `updatePriority - not existing priority`() {
        every { persistence.getPriorityById(-1) } throws ResourceNotFoundException("programmePriority")
        assertThrows<ResourceNotFoundException> { updatePriority.updatePriority(-1, toUpdatePriority) }
    }

    @Test
    fun `updatePriority - wrong code (long)`() {
        val code = getStringOfLength(51)
        val priority = toUpdatePriority.copy(code = code)
        every { generalValidator.maxLength(code, 50, "code") } returns inputErrorMap
        assertThrows<AppInputValidationException> { updatePriority.updatePriority(ID, priority) }
        verify(exactly = 1) { generalValidator.maxLength(code, 50, "code") }
    }

    @Test
    fun `updatePriority - wrong code (empty)`() {
        every { persistence.getPriorityById(ID) } returns testPriority.copy(id = ID)
        testWrongCode(" ")
        testWrongCode("")
        testWrongCode("\t")
    }

    private fun testWrongCode(code: String) {
        val priority = toUpdatePriority.copy(code = code)
        every { generalValidator.maxLength(code, 50, "code") } returns inputErrorMap
        assertThrows<AppInputValidationException> { updatePriority.updatePriority(ID, priority) }
        verify(exactly = 1) { generalValidator.maxLength(code, 50, "code") }
    }

    @Test
    fun `updatePriority - wrong title (long or empty)`() {
        every { persistence.getPriorityById(ID) } returns testPriority.copy(id = ID)
        testWrongTitle(getStringOfLength(301))
    }

    @Test
    fun `updatePriority - invalid dimension codes`() {
        every { persistence.getPriorityIdByCode(any()) } returns null
        every { persistence.getPriorityIdForPolicyIfExists(RenewableEnergy) } returns ID
        every { persistence.getPriorityIdForPolicyIfExists(GreenInfrastructure) } returns ID
        every { persistence.getPriorityIdForPolicyIfExists(CircularEconomy) } returns null
        every { persistence.getPriorityIdForPolicyIfExists(WaterManagement) } returns null

        val dimensionCodes = mutableMapOf<ProgrammeObjectiveDimension, List<String>>(
            ProgrammeObjectiveDimension.EconomicActivity to listOf()
        )
        val priority = toUpdatePriority.copy(
            code = "random",
            specificObjectives = listOf(
                ProgrammeSpecificObjective(
                    programmeObjectivePolicy = RenewableEnergy,
                    code = "code01",
                    dimensionCodes = dimensionCodes
                )
            )
        )
        every { persistence.getPriorityById(ID) } returns priority
        var ex = assertThrows<I18nValidationException> { updatePriority.updatePriority(ID, priority) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.dimension.codes.size.invalid")

        dimensionCodes[ProgrammeObjectiveDimension.EconomicActivity] = listOf(
            "001", "002", "003", "004", "005", "006", "007", "008", "009", "010", "011", "012", "013",
            "014", "015", "016", "017", "018", "019", "020", "021"
        )
        ex = assertThrows { updatePriority.updatePriority(ID, priority) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.dimension.codes.size.invalid")

        dimensionCodes[ProgrammeObjectiveDimension.EconomicActivity] = listOf("1d")
        ex = assertThrows { updatePriority.updatePriority(ID, priority) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.dimension.codes.value.invalid")
    }

    private fun testWrongTitle(title: String) {
        val titleSet = setOf(InputTranslation(SystemLanguage.EN, title))
        val priority = toUpdatePriority.copy(title = setOf(InputTranslation(SystemLanguage.EN, title)))
        every { generalValidator.maxLength(titleSet, 300, "title") } returns inputErrorMap
        assertThrows<AppInputValidationException> { updatePriority.updatePriority(ID, priority) }
        verify(exactly = 1) { generalValidator.maxLength(titleSet, 300, "title") }

    }

    @Test
    fun `updatePriority - no specific objective`() {
        every { generalValidator.minSize(emptyList(), 1, "specificObjectives") } returns inputErrorMap
        assertThrows<AppInputValidationException> { updatePriority.updatePriority(ID, toUpdatePriority.copy(specificObjectives = emptyList())) }
        verify(exactly = 1) { generalValidator.minSize(emptyList(), 1, "specificObjectives") }
    }

    @Test
    fun `updatePriority - wrong specific objective code (long)`() {
        val code = getStringOfLength(51)
        val priority = toUpdatePriority.copy(
            specificObjectives = listOf(
                ProgrammeSpecificObjective(
                    programmeObjectivePolicy = RenewableEnergy,
                    code = code
                )
            )
        )
        every { generalValidator.maxLength(code, 50, "specificObjectives") } returns inputErrorMap

        assertThrows<AppInputValidationException> { updatePriority.updatePriority(ID, priority) }
        verify(exactly = 1) { generalValidator.maxLength(code, 50, "specificObjectives") }

    }

    @Test
    fun `updatePriority - wrong specific objective code (empty)`() {
        every { persistence.getPriorityById(ID) } returns testPriority.copy(id = ID)
        testBlankSpecificObjectiveCode(" ")
        testBlankSpecificObjectiveCode("")
        testBlankSpecificObjectiveCode("\n")
    }

    private fun testBlankSpecificObjectiveCode(code: String, so: ProgrammeObjectivePolicy = RenewableEnergy) {
        val priority = toUpdatePriority.copy(
            specificObjectives = listOf(
                ProgrammeSpecificObjective(
                    programmeObjectivePolicy = so,
                    code = code
                )
            )
        )
        every { generalValidator.notBlank(code, "specificObjectives") } returns inputErrorMap

        assertThrows<AppInputValidationException> { updatePriority.updatePriority(ID, priority) }
        verify(exactly = 1) { generalValidator.notBlank(code, "specificObjectives") }

    }

    @Test
    fun `updatePriority - specific objective does not belong to objective`() {
        every { persistence.getPriorityById(ID) } returns testPriority.copy(id = ID)
        assertThat(toUpdatePriority.objective).isNotEqualTo(ProgrammeObjective.ISO12)
        val priority = toUpdatePriority.copy(objective = ProgrammeObjective.ISO12)
        val ex = assertThrows<I18nValidationException> { updatePriority.updatePriority(ID, priority) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.specificObjectives.should.not.be.of.different.objectives")
    }

    @Test
    fun `updatePriority - specific objectives have duplicate codes`() {
        every { persistence.getPriorityById(ID) } returns testPriority.copy(id = ID)
        val NOT_UNIQUE_CODE = "not-unique-code"

        val priority = toUpdatePriority.copy(
            specificObjectives = listOf(
                ProgrammeSpecificObjective(
                    programmeObjectivePolicy = RenewableEnergy,
                    code = NOT_UNIQUE_CODE
                ),
                ProgrammeSpecificObjective(
                    programmeObjectivePolicy = GreenInfrastructure,
                    code = NOT_UNIQUE_CODE
                ),
            )
        )

        val ex = assertThrows<I18nValidationException> { updatePriority.updatePriority(ID, priority) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.specificObjective.code.should.be.unique")
    }

    @Test
    fun `updatePriority - specific objectives have duplicate policies`() {
        every { persistence.getPriorityById(ID) } returns testPriority.copy(id = ID)
        val NOT_UNIQUE_POLICY = RenewableEnergy

        val priority = toUpdatePriority.copy(
            specificObjectives = listOf(
                ProgrammeSpecificObjective(
                    programmeObjectivePolicy = NOT_UNIQUE_POLICY,
                    code = "code01"
                ),
                ProgrammeSpecificObjective(
                    programmeObjectivePolicy = NOT_UNIQUE_POLICY,
                    code = "code02"
                ),
            )
        )

        val ex = assertThrows<I18nValidationException> { updatePriority.updatePriority(ID, priority) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.specificObjective.objectivePolicy.should.be.unique")
    }

    @Test
    fun `updatePriority - priority code is already in use`() {
        every { persistence.getPriorityById(ID) } returns testPriority.copy(id = ID)
        // title is not used and code is used by different priority
        every { persistence.getPriorityIdByCode(toUpdatePriority.code) } returns 826
        val ex = assertThrows<I18nValidationException> { updatePriority.updatePriority(ID, testPriority) }
        assertThat(ex.i18nKey).isEqualTo("programme.priority.code.already.in.use")
    }

    @Test
    fun `updatePriority - specific objective policy is already used by other existing priority`() {
        every { persistence.getPriorityById(ID) } returns testPriority.copy(id = ID)
        // code and title are both used, but they are used by this priority we are updating so it is OK
        every { persistence.getPriorityIdByCode(testPriority.code) } returns ID
        // this one is used by priority we are updating now
        every { persistence.getPriorityIdForPolicyIfExists(GreenInfrastructure) } returns ID
        // this one is not used
        every { persistence.getPriorityIdForPolicyIfExists(CircularEconomy) } returns null
        // this one IS ALREADY USED
        every { persistence.getPriorityIdForPolicyIfExists(WaterManagement) } returns 25

        val ex = assertThrows<I18nValidationException> { updatePriority.updatePriority(ID, toUpdatePriority) }
        assertThat(ex.i18nFieldErrors!!["specificObjectives"]).isEqualTo(
            I18nFieldError(
                i18nKey = "programme.priority.specificObjective.objectivePolicy.already.in.use",
                i18nArguments = listOf(WaterManagement.name)
            )
        )
    }

    @Test
    fun `updatePriority - specific objective code is already used by other existing specific objective`() {
        every { persistence.getPriorityById(ID) } returns testPriority.copy(id = ID)
        // code and title are both not used yet
        every { persistence.getPriorityIdByCode(testPriority.code) } returns null
        // this one is used by priority we are updating now
        every { persistence.getPriorityIdForPolicyIfExists(GreenInfrastructure) } returns ID
        // these are not used
        every { persistence.getPriorityIdForPolicyIfExists(CircularEconomy) } returns null
        every { persistence.getPriorityIdForPolicyIfExists(WaterManagement) } returns null
        // here code "CE" is already assigned to existing specific objective of different priority "CODE_15":
        every { persistence.getPrioritiesBySpecificObjectiveCodes(setOf("GU", "CE", "WM")) } returns listOf(
            ProgrammePriority(
                id = 15,
                code = "CODE_15",
                title = setOf(InputTranslation(SystemLanguage.EN, "TITLE_15")),
                objective = EnergyEfficiency.objective,
                specificObjectives = listOf(ProgrammeSpecificObjective(EnergyEfficiency, "CE"))
            )
        )

        val ex = assertThrows<I18nValidationException> { updatePriority.updatePriority(ID, toUpdatePriority) }
        assertThat(ex.i18nFieldErrors!!["specificObjectives"]).isEqualTo(
            I18nFieldError(
                i18nKey = "programme.priority.specificObjective.code.already.in.use.by.other.priority",
                i18nArguments = listOf("CODE_15")
            )
        )
    }

    @Test
    fun `updatePriority - programme setup is locked so objectives cannot be removed`() {
        val priority = testPriority.copy(id = ID)
        every { persistence.getPriorityById(ID) } returns priority
        // code and title are both not used yet
        every { persistence.getPriorityIdByCode(testPriority.code) } returns null
        // this one is used by priority we are updating now
        every { persistence.getPriorityIdForPolicyIfExists(GreenInfrastructure) } returns ID
        every { persistence.getPrioritiesBySpecificObjectiveCodes(setOf("GU")) } returns listOf(priority)
        // programme setup is already locked
        every { isProgrammeSetupLocked.isLocked() } returns true

        val toUpdateWithoutRenewableEnergy = toUpdatePriority.copy(
            specificObjectives = listOf(
                ProgrammeSpecificObjective(programmeObjectivePolicy = GreenInfrastructure, code = "GU"),
            )
        )
        assertThrows<UpdateWhenProgrammeSetupRestricted> {
            updatePriority.updatePriority(
                ID,
                toUpdateWithoutRenewableEnergy
            )
        }
    }

    @Test
    fun `updatePriority - specific objectives that are in use cannot be removed`() {
        val priority = testPriority.copy(id = ID)
        every { persistence.getPriorityById(ID) } returns priority
        // code and title are both not used yet
        every { persistence.getPriorityIdByCode(testPriority.code) } returns null
        // this one is used by priority we are updating now
        every { persistence.getPriorityIdForPolicyIfExists(GreenInfrastructure) } returns ID
        every { persistence.getPrioritiesBySpecificObjectiveCodes(setOf("GU")) } returns listOf(priority)
        // programme setup still open for changes
        every { isProgrammeSetupLocked.isLocked() } returns false
        // objective to be removed is used by a call already
        every { persistence.getObjectivePoliciesAlreadyInUse() } returns setOf(RenewableEnergy)

        val toUpdateWithoutRenewableEnergy = toUpdatePriority.copy(
            specificObjectives = listOf(
                ProgrammeSpecificObjective(programmeObjectivePolicy = GreenInfrastructure, code = "GU"),
            )
        )

        val ex = assertThrows<ToUpdatePriorityAlreadyUsedInCall> {
            updatePriority.updatePriority(ID, toUpdateWithoutRenewableEnergy)
        }
        assertThat(ex.httpStatus).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(ex.i18nMessage.i18nKey).isEqualTo("use.case.update.programme.priority.already.used.in.call")
    }

}
