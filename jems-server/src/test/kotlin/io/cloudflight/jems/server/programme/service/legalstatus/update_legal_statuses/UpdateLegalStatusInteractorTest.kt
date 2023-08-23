package io.cloudflight.jems.server.programme.service.legalstatus.update_legal_statuses

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.common.validator.AppInputValidationException
import io.cloudflight.jems.server.common.validator.GeneralValidatorService
import io.cloudflight.jems.server.programme.service.info.isSetupLocked.IsProgrammeSetupLockedInteractor
import io.cloudflight.jems.server.programme.service.legalstatus.ProgrammeLegalStatusPersistence
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatus
import io.cloudflight.jems.server.programme.service.legalstatus.model.ProgrammeLegalStatusType
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.context.ApplicationEventPublisher

internal class UpdateLegalStatusInteractorTest : UnitTest() {

    companion object {
        private const val MAX_DESCRIPTION_LENGTH = 50
        private val alreadyExistingLegalStatus = ProgrammeLegalStatus(
            id = 14,
            description = setOf(
                InputTranslation(language = EN, translation = "already existing EN desc"),
                InputTranslation(language = SK, translation = "already existing SK desc"),
            ),
            type = ProgrammeLegalStatusType.OTHER
        )

        private val defaultLegalStatuses = listOf(
            ProgrammeLegalStatus(
                id = 1,
                description = setOf(
                    InputTranslation(language = EN, translation = "defautl public EN desc"),
                    InputTranslation(language = SK, translation = "default public SK desc"),
                ),
                type = ProgrammeLegalStatusType.PUBLIC
            ),
            ProgrammeLegalStatus(
                id = 2,
                description = setOf(
                    InputTranslation(language = EN, translation = "default private EN desc"),
                    InputTranslation(language = SK, translation = "default private SK desc"),
                ),
                type = ProgrammeLegalStatusType.PRIVATE
            )
        )
    }

    @MockK
    lateinit var persistence: ProgrammeLegalStatusPersistence

    @RelaxedMockK
    lateinit var generalValidatorService: GeneralValidatorService

    @MockK
    lateinit var isProgrammeSetupLocked: IsProgrammeSetupLockedInteractor

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @InjectMockKs
    lateinit var updateLegalStatus: UpdateLegalStatus

    @MockK
    lateinit var mockedList: List<ProgrammeLegalStatus>

    @BeforeAll
    fun setup() {
        every {
            persistence.getByType(
                listOf(ProgrammeLegalStatusType.PRIVATE, ProgrammeLegalStatusType.PUBLIC)
            )
        } returns defaultLegalStatuses
    }

    @BeforeEach
    fun reset() {
        clearMocks(generalValidatorService)
    }

    @Test
    fun `update legal statuses - everything should be fine`() {
        val legalStatus = ProgrammeLegalStatus(
            description = setOf(InputTranslation(language = EN, translation = "LS EN desc")),
            type = ProgrammeLegalStatusType.OTHER
        )

        val slotToDeleteIds = slot<Set<Long>>()
        val slotLegalStatuses = slot<Collection<ProgrammeLegalStatus>>()
        every { isProgrammeSetupLocked.isLocked() } returns false
        every { persistence.updateLegalStatuses(capture(slotToDeleteIds), capture(slotLegalStatuses)) } returns
            listOf(alreadyExistingLegalStatus, legalStatus.copy(id = 30))

        assertThat(
            updateLegalStatus.updateLegalStatuses(
                toDeleteIds = setOf(22, 25),
                toPersist = listOf(legalStatus)
            )
        ).containsExactly(
            alreadyExistingLegalStatus, legalStatus.copy(id = 30)
        )

        assertThat(slotToDeleteIds.captured).containsExactly(22, 25)
        assertThat(slotLegalStatuses.captured).containsExactly(legalStatus)

        val event = slot<AuditCandidateEvent>()
        verify { auditPublisher.publishEvent(capture(event)) }
        with(event.captured) {
            assertThat(this.auditCandidate.action).isEqualTo(AuditAction.LEGAL_STATUS_EDITED)
            assertThat(this.auditCandidate.description).isEqualTo(
                "Values for partner legal status set to:\n" +
                    "[EN=already existing EN desc, SK=already existing SK desc],\n" +
                    "[EN=LS EN desc]"
            )
        }
    }

    @Test
    fun `update legal statuses - when programme setup is restricted we cannot delete`() {
        every { isProgrammeSetupLocked.isLocked() } returns true
        assertThrows<DeletionIsNotAllowedException> {
            updateLegalStatus.updateLegalStatuses(
                setOf(-1),
                emptySet()
            )
        }
    }

    @Test
    fun `update legal statuses - when programme setup description is longer then allowed exception should be thrown`() {
        val descriptionTranslations = setOf(InputTranslation(language = EN, translation = getStringOfLength(128),))
        every {
            generalValidatorService.throwIfAnyIsInvalid(
                generalValidatorService.maxLength(descriptionTranslations, MAX_DESCRIPTION_LENGTH, "description")
            )
        } throws AppInputValidationException(hashMapOf())
        assertThrows<AppInputValidationException> {
            updateLegalStatus.updateLegalStatuses(
                emptySet(), listOf(
                    ProgrammeLegalStatus(
                        description = descriptionTranslations,
                        type = ProgrammeLegalStatusType.OTHER

                    )
                )
            )
        }
    }

    @Test
    fun `update legal statuses - exception should be thrown when more legal statuses would be created then allowed`() {
        every { mockedList.size } returns 51
        every { isProgrammeSetupLocked.isLocked() } returns true
        every { persistence.updateLegalStatuses(any(), any()) } returns mockedList

        val ex = assertThrows<MaxAllowedLegalStatusesReachedException> {
            updateLegalStatus.updateLegalStatuses(
                emptySet(),
                emptyList()
            )
        }
        assertThat(ex.message).isEqualTo("max allowed: 50")

        verify(exactly = 0) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
    }

    @Test
    fun `should throw exception when public-private legal statuses are being deleted`() {
        every { isProgrammeSetupLocked.isLocked() } returns false

        assertThrows<PredefinedLegalStatusesCannotBeDeletedException> {
            updateLegalStatus.updateLegalStatuses(
                setOf(1L),
                emptyList()
            )
        }
    }

    @ParameterizedTest
    @MethodSource("providePreDefinedLegalStatusTypes")
    fun `should throw exception when new legal status under predefined types is being created`(type: ProgrammeLegalStatusType) {
        assertThrows<CreationOfLegalStatusUnderPredefinedTypesIsNotAllowedException> {
            updateLegalStatus.updateLegalStatuses(
                emptySet(),
                listOf(ProgrammeLegalStatus(id = 0, type = type))
            )
        }
    }

    private fun providePreDefinedLegalStatusTypes() =
        ProgrammeLegalStatusType.values().filter { it != ProgrammeLegalStatusType.OTHER }
}
