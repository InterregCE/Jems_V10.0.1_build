package io.cloudflight.jems.server.programme.service.stateaid.update_state_aids

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.stateaid.ProgrammeStateAidMeasure
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.stateaid.ProgrammeStateAidPersistence
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import io.cloudflight.jems.server.programme.service.stateaid.update_stateaid.DeletionIsNotAllowedException
import io.cloudflight.jems.server.programme.service.stateaid.update_stateaid.UpdateStateAid
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher
import java.math.BigDecimal

internal class UpdateStateAidInteractorTest : UnitTest() {

    companion object {
        private val alreadyExistingStateAid = ProgrammeStateAid(
            id = 14,
            measure = ProgrammeStateAidMeasure.OTHER_1,
            name = setOf(InputTranslation(language = EN, translation = "already existing EN name")),
            abbreviatedName = setOf(InputTranslation(language = EN, translation = "already existing EN abbName")),
            schemeNumber = "already existing Sch",
            maxIntensity = BigDecimal(50),
            threshold = BigDecimal(30),
            comments = setOf(InputTranslation(language = EN, translation = "already existing EN comm"))
        )

        private val defaultStateAids = listOf(
            ProgrammeStateAid(
                id = 1,
                measure = ProgrammeStateAidMeasure.GBER_ARTICLE_15,
                name = setOf(InputTranslation(language = EN, translation = "default EN name")),
                abbreviatedName = setOf(InputTranslation(language = EN, translation = "default EN abbName")),
                schemeNumber = "default Sch",
                maxIntensity = BigDecimal(50),
                threshold = BigDecimal(30),
                comments = setOf(
                    InputTranslation(language = EN, translation = "default EN comm")
                )
            ),
            ProgrammeStateAid(
                id = 2,
                measure = ProgrammeStateAidMeasure.GBER_ARTICLE_25C,
                name = setOf(InputTranslation(language = EN, translation = "default EN name")),
                abbreviatedName = setOf(InputTranslation(language = EN, translation = "default EN abbName")),
                schemeNumber = "default Sch",
                maxIntensity = BigDecimal(50),
                threshold = BigDecimal(30),
                comments = setOf(
                    InputTranslation(language = EN, translation = "default EN comm")
                )
            )
        )
    }

    @RelaxedMockK
    lateinit var persistence: ProgrammeStateAidPersistence

    @RelaxedMockK
    lateinit var generalValidatorService: GeneralValidatorService

    @RelaxedMockK
    lateinit var isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var callPersistence: CallPersistence

    @InjectMockKs
    lateinit var updateStateAid: UpdateStateAid

    @MockK
    lateinit var mockedList: List<ProgrammeStateAid>

    @BeforeAll
    fun setup() {
        every { persistence.getStateAidList() } returns defaultStateAids
    }

    @BeforeEach
    fun reset() {
        clearMocks(generalValidatorService)
    }

    @Test
    fun `update state aids - everything should be fine`() {
        val stateAid = ProgrammeStateAid(
            measure = ProgrammeStateAidMeasure.OTHER_2,
            name = setOf(InputTranslation(language = EN, translation = "new EN name")),
            abbreviatedName = setOf(InputTranslation(language = EN, translation = "new EN abbName")),
            schemeNumber = "new Sch",
            maxIntensity = BigDecimal(50),
            threshold = BigDecimal(30),
            comments = setOf(InputTranslation(language = EN, translation = "new EN comm"))
        )

        val slotToDeleteIds = slot<Set<Long>>()
        val slotStateAids = slot<Collection<ProgrammeStateAid>>()
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.updateStateAids(capture(slotToDeleteIds), capture(slotStateAids)) } returns
            listOf(alreadyExistingStateAid, stateAid.copy(id = 30))

        assertThat(
            updateStateAid.updateStateAids(
                toDeleteIds = setOf(22, 25),
                toPersist = listOf(stateAid)
            )
        ).containsExactly(
            alreadyExistingStateAid, stateAid.copy(id = 30)
        )

        assertThat(slotToDeleteIds.captured).containsExactly(22, 25)
        assertThat(slotStateAids.captured).containsExactly(stateAid)

        val event = slot<AuditCandidateEvent>()
        verify { auditPublisher.publishEvent(capture(event)) }
        with(event.captured) {
            assertThat(this.auditCandidate.action).isEqualTo(AuditAction.PROGRAMME_STATE_AID_CHANGED)
            assertThat(this.auditCandidate.description).isEqualTo(
                "Programme State aid was set to:\n" +
                    "[EN=already existing EN name],\n" +
                    "[EN=new EN name]"
            )
        }
    }

    @Test
    fun `update state aids - when programme setup is restricted we cannot delete`() {
        every { isProgrammeSetupLocked.isLocked() } returns true
        assertThrows<DeletionIsNotAllowedException> {
            updateStateAid.updateStateAids(
                setOf(-1),
                emptySet()
            )
        }
    }
}
