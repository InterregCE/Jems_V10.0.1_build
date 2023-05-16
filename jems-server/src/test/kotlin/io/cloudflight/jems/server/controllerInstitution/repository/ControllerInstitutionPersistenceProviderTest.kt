package io.cloudflight.jems.server.controllerInstitution.repository

import com.querydsl.core.types.Predicate
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionPartnerEntity
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserEntity
import io.cloudflight.jems.server.controllerInstitution.entity.ControllerInstitutionUserId
import io.cloudflight.jems.server.controllerInstitution.nutsAustria
import io.cloudflight.jems.server.controllerInstitution.service.model.ControllerInstitutionList
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerAssignment
import io.cloudflight.jems.server.controllerInstitution.service.model.InstitutionPartnerSearchRequest
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
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.slot
import java.time.ZonedDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class ControllerInstitutionPersistenceProviderTest : UnitTest() {

    companion object {
        private val createdAt = ZonedDateTime.now().minusDays(1)
        val dummyControllerEntity = ControllerInstitutionEntity(
            id = 1L,
            name = "dummy controller",
            description = "dummy controller description",
            institutionNuts = mutableSetOf(nutsAustria),
            createdAt = createdAt
        )

        val expectedControllerInstitution = ControllerInstitutionList(
            id = 1L,
            name = "dummy controller",
            description = "dummy controller description",
            institutionNuts = setOf(nutsAustria).toDto(),
            createdAt = createdAt
        )

        private fun dummyATInstitutionAssignmentEntity(): ControllerInstitutionPartnerEntity {
            val partnerMock = mockk<ProjectPartnerEntity> {
                every { project.id } returns 1
                every { project.call.id } returns 1
            }
            val institutionMock = mockk<ControllerInstitutionEntity> { every { id } returns 2L }
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
                every { projectIdentifier } returns "00010"
                every { projectAcronym } returns "Project Test"
            }

            return entity
        }
        private fun dummyROInstitutionAssignmentEntity(): ControllerInstitutionPartnerEntity {
            val partnerMock = mockk<ProjectPartnerEntity> {
                every { project.id } returns 2
                every { project.call.id } returns 1
            }
            val institutionMock = mockk<ControllerInstitutionEntity> { every { id } returns 2L }
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

        private val userEntities = listOf(
            ControllerInstitutionUserEntity(
                id = ControllerInstitutionUserId(
                    controllerInstitutionId = 1L,
                    user = UserEntity(
                        id = 1L,
                        email = "some email",
                        sendNotificationsToEmail = false,
                        name = "some name",
                        surname = "some surname",
                        userRole = UserRoleEntity(
                            id = 1L,
                            name = "some role"
                        ),
                        password = "some password",
                        userStatus = UserStatus.ACTIVE
                    )
                ),
                accessLevel = UserInstitutionAccessLevel.Edit
            )
        )

        private val expectedUser =
            UserSimple(
                id = 1L,
                name = "some name",
                surname = "some surname",
                email = "some email"
            )
    }

    @MockK
    lateinit var nutsRegion3Repository: NutsRegion3Repository

    @MockK
    lateinit var institutionRepository: ControllerInstitutionRepository

    @MockK
    lateinit var institutionUserRepository: ControllerInstitutionUserRepository

    @MockK
    lateinit var institutionPartnerRepository: ControllerInstitutionPartnerRepository

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @MockK
    lateinit var institutionNutsRepository: ControllerInstitutionNutsRepository

    @InjectMockKs
    lateinit var persistence: ControllerInstitutionPersistenceProvider

    @BeforeEach
    fun reset() {
        clearAllMocks()
    }

    @Test
    fun getControllerInstitutions() {
        every { institutionRepository.findAll(Pageable.unpaged()) } returns PageImpl(listOf(dummyControllerEntity))
        assertThat(persistence.getControllerInstitutions(Pageable.unpaged()).content).contains(expectedControllerInstitution)
    }

    @Test
    fun getInstitutionPartnerAssignmentsWithActiveSearchRequest() {
        val predicate = slot<Predicate>()
        every { institutionPartnerRepository.findAll(capture(predicate), Pageable.unpaged()) } returns
                PageImpl(listOf(
                    dummyATInstitutionAssignmentEntity(),
                    dummyROInstitutionAssignmentEntity()
                ))

        persistence.getInstitutionPartnerAssignments(Pageable.unpaged(), InstitutionPartnerSearchRequest(
            callId  = 1,
            projectId = null,
            acronym = "",
            partnerName = "",
            partnerNuts = emptySet(),
            globallyRestrictedNuts = null // = not restricted
        )
        )

        assertThat(predicate.captured.toString()).isEqualTo("controllerInstitutionPartnerEntity.partner.project.call.id = 1")
    }

    @Test
    fun getInstitutionPartnerAssignmentsWithActiveSearchRequestIncludingNuts() {
        val predicate = slot<Predicate>()
        every { institutionPartnerRepository.findAll(capture(predicate), Pageable.unpaged()) } returns PageImpl(listOf(dummyATInstitutionAssignmentEntity()))

        persistence.getInstitutionPartnerAssignments(Pageable.unpaged(), InstitutionPartnerSearchRequest(
            callId  = 1,
            projectId = "1",
            acronym = "Project",
            partnerName = "A",
            partnerNuts = setOf("AT1"),
            globallyRestrictedNuts = setOf("AT130")
        ))

        assertThat(predicate.captured.toString()).isEqualTo("controllerInstitutionPartnerEntity.partner.project.call.id = 1 && " +
                "(controllerInstitutionPartnerEntity.projectIdentifier like %1% || controllerInstitutionPartnerEntity.partner.project.id = 1) && " +
                "lower(controllerInstitutionPartnerEntity.projectAcronym) like %project% && " +
                "lower(controllerInstitutionPartnerEntity.partnerAbbreviation) like %a% && " +
                "controllerInstitutionPartnerEntity.addressNuts3Code = AT130 && " +
                "lower(controllerInstitutionPartnerEntity.addressNuts3Code) like at1%")
    }

    @Test
    fun getRelatedUserIdsForProject() {
        every { institutionPartnerRepository.getRelatedUserIdsForProject(projectId = 188L) } returns setOf(1L, 2L, 3L)
        assertThat(persistence.getRelatedUserIdsForProject(188L))
            .containsExactly(1L, 2L, 3L)
    }

    @Test
    fun getRelatedUserIdsForPartner() {
        every { institutionPartnerRepository.getRelatedUserIdsForPartner(partnerId = 199L) } returns setOf(1L, 2L, 3L)
        assertThat(persistence.getRelatedUserIdsForPartner(199L))
            .containsExactly(1L, 2L, 3L)
    }

    @Test
    fun getRelatedProjectAndPartnerIdsForUser() {
        every { institutionPartnerRepository.getRelatedProjectIdsForUser(userId = 444L) } returns listOf(
            Pair(1L, 10L), Pair(1L, 11L),
            Pair(2L, 20L), Pair(2L, 21L),
        )
        assertThat(persistence.getRelatedProjectAndPartnerIdsForUser(444L))
            .containsExactlyEntriesOf(mapOf(1L to setOf(10L, 11L), 2L to setOf(20L, 21L)))
    }

    @Test
    fun getInstitutionPartnerAssignmentsToDeleteByProjectId() {
        every { institutionPartnerRepository.getInstitutionPartnerAssignmentsToDeleteByProjectId(1L) } returns
                listOf(dummyATInstitutionAssignmentEntity())
        assertThat(persistence.getInstitutionPartnerAssignmentsToDeleteByProjectId(1L))
            .containsExactly(InstitutionPartnerAssignment(institutionId = 2L, partnerId = 1L, partnerProjectId = 10L))
    }

    @Test
    fun getControllerUsersForReportByInstitutionId() {
        every { institutionUserRepository.findAllByControllerInstitutionId(2L) } returns userEntities
        assertThat(persistence.getControllerUsersForReportByInstitutionId(2L))
            .containsExactly(expectedUser)
    }
}
