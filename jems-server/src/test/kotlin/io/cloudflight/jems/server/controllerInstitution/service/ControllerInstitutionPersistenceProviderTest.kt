package io.cloudflight.jems.server.controllerInstitution.service


import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserEntity
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserId
import io.cloudflight.jems.server.controllerInstitution.repository.*
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.nuts.repository.NutsRegion3Repository
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.user.repository.user.toUserSummary
import io.cloudflight.jems.server.user.service.UserPersistence
import io.cloudflight.jems.server.utils.USER_EMAIL
import io.cloudflight.jems.server.utils.USER_ID
import io.cloudflight.jems.server.utils.userEntity
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime
import java.util.*


class ControllerInstitutionPersistenceProviderTest : UnitTest() {

    @RelaxedMockK
    lateinit var controllerRepo: ControllerInstitutionRepository

    @RelaxedMockK
    lateinit var nutsRegion3Repository: NutsRegion3Repository

    @RelaxedMockK
    lateinit var userPersistence: UserPersistence

    @RelaxedMockK
    lateinit var institutionUserRepository: ControllerInstitutionUserRepository

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @MockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @InjectMockKs
    lateinit var controllerInstitutionPersistenceProvider: ControllerInstitutionPersistenceProvider

    @BeforeEach
    fun resetMocks() {
        clearMocks(auditPublisher)
        clearMocks(userPersistence)
        clearMocks(institutionUserRepository)
    }

    private val INSTITUTION_ID = 1L
    private val createdAt = ZonedDateTime.now()
    private val institution = ControllerInstitution(
        id = INSTITUTION_ID,
        name = "INSTITUTION",
        description = "DESCRIPTION",
        institutionNuts = emptyList(),
        institutionUsers = emptyList(),
        createdAt = createdAt
    )
    private val userList =
        listOf(ControllerInstitutionUser(INSTITUTION_ID, USER_ID, USER_EMAIL, UserInstitutionAccessLevel.View))
    private val institutionWithUsers = ControllerInstitution(
        id = INSTITUTION_ID,
        name = "INSTITUTION",
        description = "DESCRIPTION",
        institutionNuts = emptyList(),
        institutionUsers = userList,
        createdAt = createdAt
    )

    private val updateInstitution = UpdateControllerInstitution(
        id = INSTITUTION_ID,
        name = "INSTITUTION",
        description = "DESCRIPTION",
        institutionNuts = emptyList(),
        institutionUsers = userList,
        createdAt = createdAt
    )

    @Test
    fun createInstitution() {
        val slotControllerUpdate = slot<ControllerInstitutionEntity>()

        every { controllerRepo.findById(any()) } returns Optional.of(institution.toEntity())
        every { userPersistence.findAllByEmails(any()) } returns listOf(userEntity.toUserSummary())
        every { controllerRepo.save(capture(slotControllerUpdate)) } returns institutionWithUsers.toEntity()

        controllerInstitutionPersistenceProvider.createControllerInstitution(updateInstitution)

        verify(exactly = 1) {
            institutionUserRepository.saveAll(any<List<ControllerInstitutionUserEntity>>())
        }
        assertThat(slotControllerUpdate.captured.id).isEqualTo(institutionWithUsers.id)
        assertThat(slotControllerUpdate.captured.name).isEqualTo(institutionWithUsers.name)

        verify(exactly = 1) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
    }

    @Test
    fun updateInstitution() {
        every { controllerRepo.findById(any()) } returns Optional.of(institutionWithUsers.toEntity())
        every { userPersistence.findAllByEmails(any()) } returns listOf(userEntity.toUserSummary())

        controllerInstitutionPersistenceProvider.updateControllerInstitution(updateInstitution)

        verify(exactly = 1) {
            institutionUserRepository.saveAll(any<List<ControllerInstitutionUserEntity>>())
        }
        verify(exactly = 1) { auditPublisher.publishEvent(any<AuditCandidateEvent>()) }
    }

    @Test
    fun getControllerInstitutions() {
        val institutionEntity = institution.toEntity()
        every { controllerRepo.findAll(Pageable.unpaged()) } returns PageImpl(listOf(institutionEntity))

        controllerInstitutionPersistenceProvider.getControllerInstitutions(Pageable.unpaged())
        assertThat(controllerRepo.findAll(Pageable.unpaged())).containsExactly(institutionEntity)
    }

    @Test
    fun getControllerInstitutionsByUserId() {
        val controllerInstitutionUser = ControllerInstitutionUserEntity(
            id = ControllerInstitutionUserId(
                controllerInstitutionId = INSTITUTION_ID,
                user = userEntity
            ),
            accessLevel = UserInstitutionAccessLevel.View
        )
        val institutionEntity = institution.toEntity()

        every { institutionUserRepository.findAllByUserId(any()) } returns listOf(controllerInstitutionUser)
        val allowedControllerInstitutionUserIds =
            listOf(controllerInstitutionUser).map { it.id.controllerInstitutionId }
        every {
            controllerRepo.findAllByIdIn(
                allowedControllerInstitutionUserIds,
                Pageable.unpaged()
            )
        } returns PageImpl(listOf(institutionEntity))

        controllerInstitutionPersistenceProvider.getControllerInstitutionsByUserId(userEntity.id, Pageable.unpaged())
        assertThat(controllerRepo.findAllByIdIn(allowedControllerInstitutionUserIds, Pageable.unpaged()))
            .containsExactly(institutionEntity)
    }

    @Test
    fun getControllerInstitutionById() {
        val controllerInstitutionUser = ControllerInstitutionUserEntity(
            id = ControllerInstitutionUserId(
                controllerInstitutionId = INSTITUTION_ID,
                user = userEntity
            ),
            accessLevel = UserInstitutionAccessLevel.View
        )
        val institutionEntity = institutionWithUsers.toEntity()

        every { institutionUserRepository.findAllByControllerInstitutionId(INSTITUTION_ID) } returns listOf(
            controllerInstitutionUser
        )
        every { controllerRepo.findById(any()) } returns Optional.of(institutionEntity)
        val allowedControllerInstitutionUserIds =
            listOf(controllerInstitutionUser).map { it.id.controllerInstitutionId }
        every {
            controllerRepo.findAllByIdIn(
                allowedControllerInstitutionUserIds,
                Pageable.unpaged()
            )
        } returns PageImpl(listOf(institutionEntity))


        val result = institutionEntity.toModel(listOf(controllerInstitutionUser))

        controllerInstitutionPersistenceProvider.getControllerInstitutionById(INSTITUTION_ID)
        assertThat(result.id).isEqualTo(institutionWithUsers.id)
        assertThat(result.name).isEqualTo(institutionWithUsers.name)
        assertThat(result.description).isEqualTo(institutionWithUsers.description)
        assertThat(result.institutionNuts).isEqualTo(institutionWithUsers.institutionNuts)
        assertThat(result.institutionUsers).isEqualTo(institutionWithUsers.institutionUsers)
    }
}
