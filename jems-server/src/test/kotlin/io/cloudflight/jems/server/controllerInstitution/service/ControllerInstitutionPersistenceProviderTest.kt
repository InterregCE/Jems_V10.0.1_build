package io.cloudflight.jems.server.controllerInstitution.service


import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.controllerInstitution.ControllerInstitutionPersistence
import io.cloudflight.jems.server.controllerInstitution.MONITOR_USER_1_EMAIL
import io.cloudflight.jems.server.controllerInstitution.MONITOR_USER_1_ID
import io.cloudflight.jems.server.controllerInstitution.MONITOR_USER_2_EMAIL
import io.cloudflight.jems.server.controllerInstitution.MONITOR_USER_2_ID
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserEntity
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserId
import io.cloudflight.jems.server.controllerInstitution.institutionUsers
import io.cloudflight.jems.server.controllerInstitution.nutsRegion3Entity
import io.cloudflight.jems.server.controllerInstitution.repository.ControllerInstitutionPartnerRepository
import io.cloudflight.jems.server.controllerInstitution.repository.ControllerInstitutionPersistenceProvider
import io.cloudflight.jems.server.controllerInstitution.repository.ControllerInstitutionRepository
import io.cloudflight.jems.server.controllerInstitution.repository.ControllerInstitutionUserRepository
import io.cloudflight.jems.server.controllerInstitution.repository.toDto
import io.cloudflight.jems.server.controllerInstitution.repository.toEntity
import io.cloudflight.jems.server.controllerInstitution.repository.toModel
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetailsRow
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.nuts.repository.NutsRegion3Repository
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.project.service.partner.getPartnerAddressOrEmptyString
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.utils.USER_NAME
import io.cloudflight.jems.server.utils.USER_SURNAME
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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime
import java.util.Optional


class ControllerInstitutionPersistenceProviderTest : UnitTest() {

    @RelaxedMockK
    lateinit var controllerRepo: ControllerInstitutionRepository

    @MockK
    lateinit var nutsRegion3Repository: NutsRegion3Repository

    @RelaxedMockK
    lateinit var institutionPartnerRepository: ControllerInstitutionPartnerRepository

    @RelaxedMockK
    lateinit var institutionUserRepository: ControllerInstitutionUserRepository

    @MockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @InjectMockKs
    lateinit var controllerInstitutionPersistenceProvider: ControllerInstitutionPersistenceProvider

    @BeforeEach
    fun resetMocks() {
        clearMocks(institutionUserRepository)
        clearMocks(nutsRegion3Repository)
    }

    companion object {
        private const val INSTITUTION_ID = 1L
        private val createdAt = ZonedDateTime.now()

        private val institution = ControllerInstitution(
            id = INSTITUTION_ID,
            name = "INSTITUTION",
            description = "DESCRIPTION",
            institutionNuts = emptyList(),
            institutionUsers = mutableSetOf(),
            createdAt = ZonedDateTime.now()
        )


        private val institutionWithUsers = ControllerInstitution(
            id = INSTITUTION_ID,
            name = "INSTITUTION",
            description = "DESCRIPTION - INSTITUTION WITH USERS",
            institutionNuts = emptyList(),
            institutionUsers = institutionUsers,
            createdAt = createdAt
        )

        private val updateInstitution = UpdateControllerInstitution(
            id = INSTITUTION_ID,
            name = "Institution Test",
            description = "DESCRIPTION : institution with nuts",
            institutionNuts = listOf("RO113"),
            institutionUsers = emptyList(),
            createdAt = createdAt
        )


        private val institutionEntity = ControllerInstitutionEntity(
            id = 1L,
            name = "INSTITUTION",
            description = "DESCRIPTION",
            institutionNuts = mutableSetOf(nutsRegion3Entity),
            createdAt = ZonedDateTime.now()
        )
        private val InstitutionPartnerDetail =  InstitutionPartnerDetails(
            institutionId = INSTITUTION_ID,
            partnerId = 1L,
            partnerName = "A",
            partnerStatus = true,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerSortNumber = 1,
            partnerNuts3 = "RO113",
            partnerAddress = "Vienna, Main street, 123A, Austria",
            callId = 1L,
            projectId = 1L,
            projectCustomIdentifier = "0001",
            projectAcronym = "Project Test"
        )

        private class InstitutionPartnerDetailsRowImpl(
            override val institutionId: Long?,
            override val partnerId: Long,
            override val partnerName: String,
            override val partnerStatus: Boolean,
            override val partnerSortNumber: Int,
            override val partnerRole: String,
            override val partnerNuts3: String,
            override val country: String,
            override val city: String,
            override val street: String,
            override val houseNumber: String,
            override val callId: Long,
            override val projectId: Long,
            override val projectCustomIdentifier: String,
            override val projectAcronym: String
        ): InstitutionPartnerDetailsRow
    }

    @Test
    fun `create controller institution with nuts`() {
        val slotControllerUpdate = slot<ControllerInstitutionEntity>()
        every { controllerRepo.save(capture(slotControllerUpdate)) } returns institutionEntity
        every { nutsRegion3Repository.findAllById(updateInstitution.institutionNuts) } returns mutableListOf(nutsRegion3Entity)

        controllerInstitutionPersistenceProvider.createControllerInstitution(updateInstitution).let {
            assertThat(it.institutionNuts).isEqualTo(setOf(nutsRegion3Entity).toDto())
        }
        assertThat(slotControllerUpdate.captured.id).isEqualTo(updateInstitution.id)
        assertThat(slotControllerUpdate.captured.name).isEqualTo(updateInstitution.name)
        assertThat(slotControllerUpdate.captured.description).isEqualTo(updateInstitution.description)

    }

    @Test
    fun `update institution nuts and description`() {
        every { controllerRepo.findById(any()) } returns Optional.of(institutionWithUsers.toEntity())
        every { nutsRegion3Repository.findAllById(updateInstitution.institutionNuts) } returns mutableListOf(nutsRegion3Entity)

        controllerInstitutionPersistenceProvider.updateControllerInstitution(updateInstitution).let {
            assertThat(it.name).isEqualTo(updateInstitution.name)
            assertThat(it.description).isEqualTo(updateInstitution.description)
            assertThat(it.institutionNuts).isEqualTo(setOf(nutsRegion3Entity).toDto())
        }

        verify(exactly = 1) {
            controllerRepo.findById(any())
            nutsRegion3Repository.findAllById(updateInstitution.institutionNuts)
        }

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
        val controllerInstitutionUserEntity = ControllerInstitutionUserEntity(
            id = ControllerInstitutionUserId(
                controllerInstitutionId = INSTITUTION_ID,
                user = userEntity
            ),
            accessLevel = UserInstitutionAccessLevel.View
        )
        val institutionEntity = institution.toEntity()
        val allowedControllerInstitutionUserIds =
            listOf(controllerInstitutionUserEntity).map { it.id.controllerInstitutionId }

        every { institutionUserRepository.findAllByUserId(any()) } returns listOf(controllerInstitutionUserEntity)
        every {
            controllerRepo.findAllByIdIn(allowedControllerInstitutionUserIds, Pageable.unpaged())
        } returns PageImpl(listOf(institutionEntity))

        controllerInstitutionPersistenceProvider.getControllerInstitutionsByUserId(userEntity.id, Pageable.unpaged())
        assertThat(controllerRepo.findAllByIdIn(allowedControllerInstitutionUserIds, Pageable.unpaged()))
            .containsExactly(institutionEntity)
    }

    @Test
    fun getControllerInstitutionById() {

        val userEntities = listOf(
            ControllerInstitutionUserEntity(
                id = ControllerInstitutionUserId(
                    controllerInstitutionId = INSTITUTION_ID,
                    user = UserEntity(
                        id = MONITOR_USER_1_ID,
                        name = USER_NAME,
                        password = "hash",
                        email = MONITOR_USER_1_EMAIL,
                        surname = USER_SURNAME,
                        userRole = UserRoleEntity(3L, "Controller"),
                        userStatus = UserStatus.ACTIVE
                    )
                ),
                accessLevel = UserInstitutionAccessLevel.View
            ),
            ControllerInstitutionUserEntity(
                id = ControllerInstitutionUserId(
                    controllerInstitutionId = INSTITUTION_ID,
                    user = UserEntity(
                        id = MONITOR_USER_2_ID,
                        name = USER_NAME,
                        password = "hash",
                        email = MONITOR_USER_2_EMAIL,
                        surname = USER_SURNAME,
                        userRole = UserRoleEntity(3L, "Controller"),
                        userStatus = UserStatus.ACTIVE
                    )
                ),
                accessLevel = UserInstitutionAccessLevel.Edit
            )
        )

        val institutionEntity = institutionWithUsers.toEntity()
        val allowedControllerInstitutionUserIds = userEntities.map { it.id.controllerInstitutionId }

        every { institutionUserRepository.findAllByControllerInstitutionId(INSTITUTION_ID) } returns userEntities
        every { controllerRepo.findById(any()) } returns Optional.of(institutionEntity)

        every {
            controllerRepo.findAllByIdIn(
                allowedControllerInstitutionUserIds,
                Pageable.unpaged()
            )
        } returns PageImpl(listOf(institutionEntity))


        val result = institutionEntity.toModel(userEntities)

        controllerInstitutionPersistenceProvider.getControllerInstitutionById(INSTITUTION_ID)
        assertThat(result.id).isEqualTo(institutionWithUsers.id)
        assertThat(result.name).isEqualTo(institutionWithUsers.name)
        assertThat(result.description).isEqualTo(institutionWithUsers.description)
        assertThat(result.institutionNuts).isEqualTo(institutionWithUsers.institutionNuts)
        assertThat(result.institutionUsers).isEqualTo(institutionWithUsers.institutionUsers)
    }

    @Test
    fun `get institution partner assignments`() {
        val listInstitutionPartnerDetailsRowImpl = listOf(InstitutionPartnerDetailsRowImpl(
            institutionId = INSTITUTION_ID,
            partnerId = 1L,
            partnerName = "A",
            partnerStatus = true,
            partnerRole = "LEAD_PARTNER",
            partnerSortNumber = 1,
            country = "Austria",
            city = "Vienna",
            street = "Main street",
            houseNumber = "123A",
            partnerNuts3 = "RO113",
            callId = 1L,
            projectId = 1L,
            projectCustomIdentifier = "0001",
            projectAcronym = "Project Test"
        ))
        every { institutionPartnerRepository.getInstitutionPartnerAssignments(Pageable.unpaged()) } returns PageImpl(listInstitutionPartnerDetailsRowImpl)
        assertThat(institutionPartnerRepository.getInstitutionPartnerAssignments(Pageable.unpaged()).content[0].toModel()).isEqualTo(InstitutionPartnerDetail)
        assertThat(getPartnerAddressOrEmptyString("Austria","Vienna", "Main street","123A")).isNotEmpty
        assertThat(institutionPartnerRepository.getInstitutionPartnerAssignments(Pageable.unpaged()).toModel()).contains(InstitutionPartnerDetail)
        assertThat(controllerInstitutionPersistenceProvider.getInstitutionPartnerAssignments(Pageable.unpaged()).content).containsExactly(
            InstitutionPartnerDetail
        )
    }
}
