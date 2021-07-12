package io.cloudflight.jems.server.project.repository.partner

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.SK
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.programme.repository.legalstatus.ProgrammeLegalStatusRepository
import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import io.cloudflight.jems.server.project.entity.partner.state_aid.ProjectPartnerStateAidEntity
import io.cloudflight.jems.server.project.entity.partner.state_aid.ProjectPartnerStateAidTranslEntity
import io.cloudflight.jems.server.project.repository.ProjectRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionRepository
import io.cloudflight.jems.server.project.repository.ProjectVersionUtils
import io.cloudflight.jems.server.project.service.associatedorganization.ProjectAssociatedOrganizationService
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.ZonedDateTime
import java.util.Optional

class PartnerPersistenceTest {

    companion object {
        private const val PARTNER_ID = 1L

        private val stateAidEntity = ProjectPartnerStateAidEntity(
            partnerId = PARTNER_ID,
            answer1 = true,
            answer2 = false,
            translatedValues = setOf(
                ProjectPartnerStateAidTranslEntity(
                    translationId = TranslationPartnerId(PARTNER_ID, EN),
                    justification1 = "Is true",
                ),
                ProjectPartnerStateAidTranslEntity(
                    translationId = TranslationPartnerId(PARTNER_ID, SK),
                    justification2 = "Is false",
                ),
            )
        )

        private val stateAid = ProjectPartnerStateAid(
            answer1 = true,
            justification1 = setOf(InputTranslation(EN, "Is true")),
            answer2 = false,
            justification2 = setOf(InputTranslation(SK, "Is false")),
        )

        private val stateAidEmpty = ProjectPartnerStateAid(
            answer1 = null,
            justification1 = emptySet(),
            answer2 = null,
            justification2 = emptySet(),
            answer3 = null,
            justification3 = emptySet(),
            answer4 = null,
            justification4 = emptySet(),
        )
    }

    private lateinit var projectVersionUtils: ProjectVersionUtils

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var legalStatusRepo: ProgrammeLegalStatusRepository

    @MockK
    lateinit var projectRepo: ProjectRepository

    @MockK
    lateinit var projectPartnerStateAidRepository: ProjectPartnerStateAidRepository

    @MockK
    lateinit var projectAssociatedOrganizationService: ProjectAssociatedOrganizationService

    @MockK
    lateinit var projectVersionRepo: ProjectVersionRepository

    lateinit var persistence: PartnerPersistenceProvider

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        projectVersionUtils = ProjectVersionUtils(projectVersionRepo)
        persistence = PartnerPersistenceProvider(
            projectVersionUtils,
            projectPartnerRepository,
            legalStatusRepo,
            projectRepo,
            projectPartnerStateAidRepository,
            projectAssociatedOrganizationService,
        )
    }

    @Test
    fun `get state aid`() {
        every { projectPartnerRepository.getProjectIdForPartner(PARTNER_ID) } returns 265L
        every { projectPartnerStateAidRepository.findById(PARTNER_ID) } returns Optional.of(stateAidEntity)

        assertThat(persistence.getPartnerStateAid(PARTNER_ID, null))
            .isEqualTo(stateAid)
    }

    @Test
    fun `get state aid - not existing`() {
        every { projectPartnerRepository.getProjectIdForPartner(PARTNER_ID) } returns 236L
        every { projectPartnerStateAidRepository.findById(PARTNER_ID) } returns Optional.empty()

        assertThat(persistence.getPartnerStateAid(PARTNER_ID, null))
            .isEqualTo(stateAidEmpty)
    }

    @Test
    fun `get state aid - historical`() {
        val version = "some historical version"
        val timestamp = Timestamp.valueOf(ZonedDateTime.now().toLocalDateTime())

        every { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(PARTNER_ID) } returns 909L
        every { projectVersionRepo.findTimestampByVersion(909L, version) } returns timestamp
        every { projectPartnerStateAidRepository.findPartnerStateAidByIdAsOfTimestamp(PARTNER_ID, timestamp) } returns listOf(
            PartnerStateAidRowTest(EN, PARTNER_ID, answer1 = true, answer2 = false, justification1 = "Is true"),
            PartnerStateAidRowTest(SK, PARTNER_ID, answer1 = true, answer2 = false, justification2 = "Is false"),
        )

        assertThat(persistence.getPartnerStateAid(PARTNER_ID, version))
            .isEqualTo(stateAid)
    }

    @Test
    fun `get state aid - historical but not existing`() {
        val version = "some historical version"
        val timestamp = Timestamp.valueOf(ZonedDateTime.now().toLocalDateTime())

        every { projectPartnerRepository.getProjectIdByPartnerIdInFullHistory(PARTNER_ID) } returns 1029L
        every { projectVersionRepo.findTimestampByVersion(1029L, version) } returns timestamp
        every { projectPartnerStateAidRepository.findPartnerStateAidByIdAsOfTimestamp(PARTNER_ID, timestamp) } returns emptyList()

        assertThat(persistence.getPartnerStateAid(PARTNER_ID, version))
            .isEqualTo(stateAidEmpty)
    }

    @Test
    fun `update state aid - historical but not existing`() {
        val stateAidEnitySlot = slot<ProjectPartnerStateAidEntity>()
        every { projectPartnerStateAidRepository.save(capture(stateAidEnitySlot)) } returnsArgument 0

        assertThat(persistence.updatePartnerStateAid(PARTNER_ID, stateAid)).isEqualTo(stateAid)

        assertThat(stateAidEnitySlot.captured.partnerId).isEqualTo(PARTNER_ID)
        assertThat(stateAidEnitySlot.captured.answer1).isTrue
        assertThat(stateAidEnitySlot.captured.answer2).isFalse
        assertThat(stateAidEnitySlot.captured.answer3).isNull()
        assertThat(stateAidEnitySlot.captured.answer4).isNull()
        assertThat(stateAidEnitySlot.captured.translatedValues).hasSize(2)
    }

}
