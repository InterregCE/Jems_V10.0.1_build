package io.cloudflight.jems.server.controllerInstitution.service

import io.cloudflight.jems.api.controllerInstitutions.dto.InstitutionPartnerSearchRequest
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.controllerInstitution.MONITOR_USER_1_EMAIL
import io.cloudflight.jems.server.controllerInstitution.MONITOR_USER_1_ID
import io.cloudflight.jems.server.controllerInstitution.MONITOR_USER_2_EMAIL
import io.cloudflight.jems.server.controllerInstitution.MONITOR_USER_2_ID
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionPartnerEntity
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserEntity
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserId
import io.cloudflight.jems.server.controllerInstitution.institutionUsers
import io.cloudflight.jems.server.controllerInstitution.nutsRegion3Entity
import io.cloudflight.jems.server.controllerInstitution.repository.ControllerInstitutionNutsRepository
import io.cloudflight.jems.server.controllerInstitution.repository.ControllerInstitutionPartnerRepository
import io.cloudflight.jems.server.controllerInstitution.repository.ControllerInstitutionPartnerRepository.Companion.buildSearchPredicate
import io.cloudflight.jems.server.controllerInstitution.repository.ControllerInstitutionPersistenceProvider
import io.cloudflight.jems.server.controllerInstitution.repository.ControllerInstitutionRepository
import io.cloudflight.jems.server.controllerInstitution.repository.ControllerInstitutionUserRepository
import io.cloudflight.jems.server.controllerInstitution.repository.toDto
import io.cloudflight.jems.server.controllerInstitution.repository.toEntity
import io.cloudflight.jems.server.controllerInstitution.repository.toModel
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionUser
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerDetails
import io.cloudflight.jems.server.controllerInstitution.service.model.UpdateControllerInstitution
import io.cloudflight.jems.server.controllerInstitution.service.model.UserInstitutionAccessLevel
import io.cloudflight.jems.server.nuts.repository.NutsRegion3Repository
import io.cloudflight.jems.server.project.entity.partner.ControllerInstitutionEntity
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.repository.user.UserRepository
import io.cloudflight.jems.server.user.service.model.UserStatus
import io.cloudflight.jems.server.utils.USER_NAME
import io.cloudflight.jems.server.utils.USER_SURNAME
import io.cloudflight.jems.server.utils.userEntity
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.ZonedDateTime
import java.util.Optional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class ControllerInstitutionPersistenceProviderTest : UnitTest() {

    @RelaxedMockK
    lateinit var controllerRepo: ControllerInstitutionRepository

    @MockK
    lateinit var nutsRegion3Repository: NutsRegion3Repository

    @MockK
    lateinit var institutionPartnerRepository: ControllerInstitutionPartnerRepository

    @RelaxedMockK
    lateinit var institutionUserRepository: ControllerInstitutionUserRepository

    @RelaxedMockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var controllerInstitutionPersistence: ControllerInstitutionPersistence

    @RelaxedMockK
    lateinit var partnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var institutionNutsRepository: ControllerInstitutionNutsRepository

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

        private fun dummyATInstitutionAssignmentEntity(): ControllerInstitutionPartnerEntity {
            val partnerMock = mockk<ProjectPartnerEntity> {
                every { project.id } returns 1
                every { project.call.id } returns 1
            }
            val institutionMock = mockk<ControllerInstitutionEntity> { every { id } returns INSTITUTION_ID }
            val entity = mockk<ControllerInstitutionPartnerEntity> {
                every { partnerId } returns 1L
                every { institution } returns institutionMock
                every { partner } returns partnerMock
                every { partnerAbbreviation } returns "A"
                every { partnerActive } returns true
                every { partnerRole } returns ProjectPartnerRole.LEAD_PARTNER
                every { partnerNumber } returns 1
                every { addressNuts3 } returns "Wien (AT130)"
                every { addressNuts3Code } returns "AT130"
                every { addressCountry } returns "Austria"
                every { addressCountryCode } returns "AT"
                every { addressCity } returns "Wien"
                every { addressPostalCode } returns "299281"
                every { projectIdentifier } returns "0001"
                every { projectAcronym } returns "Project Test"
            }

            return entity
        }

        private fun dummyROInstitutionAssignmentEntity(): ControllerInstitutionPartnerEntity {
            val partnerMock = mockk<ProjectPartnerEntity> {
                every { project.id } returns 2
                every { project.call.id } returns 1
            }
            val institutionMock = mockk<ControllerInstitutionEntity> { every { id } returns INSTITUTION_ID }
            val entity = mockk<ControllerInstitutionPartnerEntity> {
                every { partnerId } returns 2L
                every { institution } returns institutionMock
                every { partner } returns partnerMock
                every { partnerAbbreviation } returns "B"
                every { partnerActive } returns true
                every { partnerRole } returns ProjectPartnerRole.PARTNER
                every { partnerNumber } returns 2
                every { addressNuts3 } returns "Gorj (RO412)"
                every { addressNuts3Code } returns "RO412"
                every { addressCountry } returns "România"
                every { addressCountryCode } returns "RO"
                every { addressCity } returns "Gorj"
                every { addressPostalCode } returns "123456"
                every { projectIdentifier } returns "0002"
                every { projectAcronym } returns "Project Test #2"
            }

            return entity
        }

        private val institutionATPartnerDetail = InstitutionPartnerDetails(
            institutionId = INSTITUTION_ID,
            partnerId = 1L,
            partnerName = "A",
            partnerStatus = true,
            partnerRole = ProjectPartnerRole.LEAD_PARTNER,
            partnerSortNumber = 1,
            partnerNuts3 = "Wien (AT130)",
            partnerNuts3Code = "AT130",
            country = "Austria",
            countryCode = "AT",
            city = "Wien",
            postalCode = "299281",
            callId = 1L,
            projectId = 1L,
            projectCustomIdentifier = "0001",
            projectAcronym = "Project Test"
        )

        private val institutionROPartnerDetail = InstitutionPartnerDetails(
            institutionId = INSTITUTION_ID,
            partnerId = 2L,
            partnerName = "B",
            partnerStatus = true,
            partnerRole = ProjectPartnerRole.PARTNER,
            partnerSortNumber = 2,
            partnerNuts3 = "Gorj (RO412)",
            partnerNuts3Code = "RO412",
            country = "România",
            countryCode = "RO",
            city = "Gorj",
            postalCode = "123456",
            callId = 1L,
            projectId = 2L,
            projectCustomIdentifier = "0002",
            projectAcronym = "Project Test #2"
        )
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
    fun `get institution partner assignments - empty search request`() {
        val emptyRequest = InstitutionPartnerSearchRequest(
            callId  = null,
            projectId = null,
            acronym = "",
            partnerName = "",
            partnerNuts = emptySet(),
            globallyRestrictedNuts = null // = not restricted
        )
        every { institutionPartnerRepository.findAll(buildSearchPredicate(emptyRequest), Pageable.unpaged()) } returns
                PageImpl(listOf(
                    dummyATInstitutionAssignmentEntity(),
                    dummyROInstitutionAssignmentEntity()
                ))

        val result = controllerInstitutionPersistenceProvider.getInstitutionPartnerAssignments(Pageable.unpaged(), emptyRequest)

        assertThat(result).containsAll(listOf(institutionATPartnerDetail, institutionROPartnerDetail))
    }

    @Test
    fun `get institution partner assignments - active search request`() {
        val searchRequest = InstitutionPartnerSearchRequest(
            callId = 1,
            projectId = null,
            acronym = "",
            partnerName = "",
            partnerNuts = emptySet(),
            globallyRestrictedNuts = null // = not restricted
        )
        every { institutionPartnerRepository.findAll(buildSearchPredicate(searchRequest), Pageable.unpaged()) } returns
                PageImpl(listOf(
                    dummyATInstitutionAssignmentEntity(),
                    dummyROInstitutionAssignmentEntity()
                ))

        val result = controllerInstitutionPersistenceProvider.getInstitutionPartnerAssignments(Pageable.unpaged(), searchRequest)

        assertThat(result).containsAll(listOf(institutionATPartnerDetail, institutionROPartnerDetail))
    }

    @Test
    fun `get institution partner assignments - active search request including NUTS`() {
        val searchRequest = InstitutionPartnerSearchRequest(
            callId = 1,
            projectId = "1",
            acronym = "Project",
            partnerName = "A",
            partnerNuts = setOf("AT1"),
            globallyRestrictedNuts = setOf("AT130"),
        )
        every { institutionPartnerRepository.findAll(buildSearchPredicate(searchRequest), Pageable.unpaged()) } returns
                PageImpl(listOf(dummyATInstitutionAssignmentEntity()))

        val result = controllerInstitutionPersistenceProvider.getInstitutionPartnerAssignments(Pageable.unpaged(), searchRequest)

        assertThat(result).containsExactly(institutionATPartnerDetail)
    }

    @Test
    fun `getInstitutionUserByInstitutionIdAndUserId - empty`() {
        every { institutionUserRepository
            .findByInstitutionIdAndUserId(institutionId = -1L, userId = -1L)
        } returns Optional.empty()

        assertThat(controllerInstitutionPersistenceProvider.getInstitutionUserByInstitutionIdAndUserId(-1L, -1L))
            .isEmpty()
    }

    @Test
    fun getInstitutionUserByInstitutionIdAndUserId() {
        val user = mockk<UserEntity>()
        every { user.id } returns 25L
        every { user.email } returns "user25@mail.com"

        val institutionUser = ControllerInstitutionUserEntity(
            id = ControllerInstitutionUserId(controllerInstitutionId = 96L, user),
            accessLevel = UserInstitutionAccessLevel.View,
        )
        val expectedUser = ControllerInstitutionUser(
            institutionId = 96L,
            userId = 25L,
            userEmail = "user25@mail.com",
            accessLevel = UserInstitutionAccessLevel.View,
        )

        every { institutionUserRepository
            .findByInstitutionIdAndUserId(institutionId = 96L, userId = 25L)
        } returns Optional.of(institutionUser)

        val result = controllerInstitutionPersistenceProvider
            .getInstitutionUserByInstitutionIdAndUserId(institutionId = 96L, userId = 25L)
        assertThat(result).isPresent()
        assertThat(result.get()).isEqualTo(expectedUser)
    }

    @Test
    fun getInstitutionUsersByInstitutionId() {
        val user = mockk<UserEntity>()
        every { user.id } returns 702L
        every { user.email } returns "user702@mail.com"

        val institutionUser = ControllerInstitutionUserEntity(
            id = ControllerInstitutionUserId(controllerInstitutionId = 18L, user),
            accessLevel = UserInstitutionAccessLevel.View,
        )
        val expectedUser = ControllerInstitutionUser(
            institutionId = 18L,
            userId = 702L,
            userEmail = "user702@mail.com",
            accessLevel = UserInstitutionAccessLevel.View,
        )

        every { institutionUserRepository.findAllByControllerInstitutionId(18L) } returns listOf(institutionUser)
        assertThat(controllerInstitutionPersistenceProvider.getInstitutionUsersByInstitutionId(18L))
            .containsExactly(expectedUser)
    }

    @Test
    fun getControllerInstitutionUsersByInstitutionIds() {
        val user = mockk<UserEntity>()
        every { user.id } returns 702L
        every { user.email } returns "user702@mail.com"

        val institutionUser = ControllerInstitutionUserEntity(
            id = ControllerInstitutionUserId(controllerInstitutionId = 14L, user),
            accessLevel = UserInstitutionAccessLevel.View,
        )
        val expectedUser = ControllerInstitutionUser(
            institutionId = 14L,
            userId = 702L,
            userEmail = "user702@mail.com",
            accessLevel = UserInstitutionAccessLevel.View,
        )

        every { institutionUserRepository.findAllByControllerInstitutionIdIn(setOf(14L)) } returns listOf(institutionUser)
        assertThat(controllerInstitutionPersistenceProvider.getControllerInstitutionUsersByInstitutionIds(setOf(14L)))
            .containsExactly(expectedUser)
    }

    @Test
    fun getInstitutionPartnerAssignmentsByPartnerIdsIn() {
        val controllerInstitution = mockk<ControllerInstitutionPartnerEntity>()
        every { controllerInstitution.partnerId } returns 480L
        every { controllerInstitution.institution?.id } returns 400L

        val expectedAssignment = InstitutionPartnerAssignment(partnerId = 480L, institutionId = 400L)

        every { institutionPartnerRepository.findAllByInstitutionId(400L) } returns listOf(controllerInstitution)
        assertThat(controllerInstitutionPersistenceProvider.getInstitutionPartnerAssignmentsByInstitutionId(400L))
            .containsExactly(expectedAssignment)
    }

    @Test
    fun getControllerUserAccessLevelForPartner() {
        every { institutionPartnerRepository.getControllerUserAccessLevelForPartner(22L, partnerId = 17L) } returns
            UserInstitutionAccessLevel.View
        assertThat(controllerInstitutionPersistenceProvider.getControllerUserAccessLevelForPartner(22L, partnerId = 17L))
            .isEqualTo(UserInstitutionAccessLevel.View)
    }
}
