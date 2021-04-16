package io.cloudflight.jems.server.user.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.user.entity.UserRoleEntity
import io.cloudflight.jems.server.user.entity.UserRolePermissionEntity
import io.cloudflight.jems.server.user.entity.UserRolePermissionId
import io.cloudflight.jems.server.user.repository.userrole.UserRoleNotFound
import io.cloudflight.jems.server.user.repository.userrole.UserRolePermissionRepository
import io.cloudflight.jems.server.user.repository.userrole.UserRoleRepository
import io.cloudflight.jems.server.user.service.model.UserRole
import io.cloudflight.jems.server.user.service.model.UserRoleCreate
import io.cloudflight.jems.server.user.service.model.UserRolePermission.ProjectSubmission
import io.cloudflight.jems.server.user.service.model.UserRolePermission.UserCreate
import io.cloudflight.jems.server.user.service.model.UserRolePermission.UserRetrieve
import io.cloudflight.jems.server.user.service.model.UserRoleSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.util.Optional

internal class UserRolePersistenceTest : UnitTest() {

    companion object {
        private const val ROLE_ID = 8L

        private val userRoleEntity = UserRoleEntity(
            id = ROLE_ID,
            name = "maintainer",
        )
        private val permissionEntity = UserRolePermissionEntity(
            UserRolePermissionId(
                userRole = userRoleEntity,
                permission = ProjectSubmission,
            )
        )
    }

    @MockK
    lateinit var userRoleRepo: UserRoleRepository

    @MockK
    lateinit var userRolePermissionRepo: UserRolePermissionRepository

    @InjectMockKs
    private lateinit var persistence: UserRolePersistenceProvider

    @Test
    fun getById() {
        every { userRolePermissionRepo.findAllByIdUserRoleId(ROLE_ID) } returns listOf(permissionEntity)
        every { userRoleRepo.getOne(ROLE_ID) } returns userRoleEntity

        assertThat(persistence.getById(ROLE_ID)).isEqualTo(
            UserRole(
                id = ROLE_ID,
                name = "maintainer",
                permissions = setOf(ProjectSubmission)
            )
        )
    }

    @Test
    fun findAll() {
        every { userRoleRepo.findAll(Pageable.unpaged()) } returns PageImpl(listOf(userRoleEntity))

        assertThat(persistence.findAll(Pageable.unpaged()).content).containsExactly(
            UserRoleSummary(
                id = ROLE_ID,
                name = "maintainer",
            )
        )
    }

    @Test
    fun create() {
        val userRoleCreate = UserRoleCreate(
            name = userRoleEntity.name,
            permissions = setOf(ProjectSubmission)
        )

        val slotRole = slot<UserRoleEntity>()
        every { userRoleRepo.save(capture(slotRole)) } returnsArgument 0
        val slotPermissions = slot<List<UserRolePermissionEntity>>()
        every { userRolePermissionRepo.saveAll(capture(slotPermissions)) } returnsArgument 0

        assertThat(persistence.create(userRoleCreate)).isEqualTo(
            UserRole(
                id = 0L,
                name = "maintainer",
                permissions = setOf(ProjectSubmission),
            )
        )

        assertThat(slotRole.captured.id).isEqualTo(0L)
        assertThat(slotRole.captured.name).isEqualTo("maintainer")

        assertThat(slotPermissions.captured.size).isEqualTo(1)
        assertThat(slotPermissions.captured[0].id.permission).isEqualTo(ProjectSubmission)
        assertThat(slotPermissions.captured[0].id.userRole).isEqualTo(slotRole.captured)

        assertThat(slotRole.captured).isEqualTo(slotPermissions.captured[0].id.userRole)
    }

    @Test
    fun update() {
        val userRetrievePermission = UserRolePermissionId(userRole = userRoleEntity, permission = UserRetrieve)
        val userCreatePermission = UserRolePermissionId(userRole = userRoleEntity, permission = UserCreate)
        val projectSubmissionPermission = UserRolePermissionId(userRole = userRoleEntity, permission = ProjectSubmission)

        val userRoleUpdate = UserRole(
            id = ROLE_ID,
            name = userRoleEntity.name,
            permissions = setOf(
                ProjectSubmission,  // to be added
                UserCreate,         // to stay
            )
        )

        every { userRoleRepo.findById(ROLE_ID) } returns Optional.of(userRoleEntity)
        every { userRolePermissionRepo.findAllByIdUserRoleId(ROLE_ID) } returns listOf( // before save
            UserRolePermissionEntity(userRetrievePermission),   // to be removed
            UserRolePermissionEntity(userCreatePermission),     // to stay
        ) andThen listOf(   // after save
            UserRolePermissionEntity(projectSubmissionPermission),
            UserRolePermissionEntity(userCreatePermission),
        )
        every { userRolePermissionRepo.deleteById(any()) } answers { }
        val slotSavedPermissions = slot<List<UserRolePermissionEntity>>()
        every { userRolePermissionRepo.saveAll(capture(slotSavedPermissions)) } returnsArgument 0

        val result = persistence.update(userRoleUpdate)
        assertThat(result).isEqualTo(userRoleUpdate).overridingErrorMessage("what I expected to be saved is saved exactly")
        assertThat(result !== userRoleUpdate).isTrue.overridingErrorMessage("model objects are not same")

        // verify UserRetrieve permission has been removed
        verify(exactly = 1) { userRolePermissionRepo.deleteById(userRetrievePermission) }

        // verify ProjectSubmission permission has been added
        assertThat(slotSavedPermissions.captured.size).isEqualTo(1)
        assertThat(slotSavedPermissions.captured[0].id.userRole).isEqualTo(userRoleEntity)
        assertThat(slotSavedPermissions.captured[0].id.permission).isEqualTo(ProjectSubmission)
    }

    @Test
    fun `update - user role does not exist`() {
        val userRoleUpdate = UserRole(
            id = -1L,
            name = "valid name",
            permissions = emptySet()
        )

        every { userRoleRepo.findById(-1) } returns Optional.empty()
        assertThrows<UserRoleNotFound> { persistence.update(userRoleUpdate) }
    }

    @Test
    fun `findUserRoleByName - existing role name`() {
        val existingRole = UserRoleEntity(
            id = ROLE_ID,
            name = "existing name",
        )

        every { userRoleRepo.findByName("existing name") } returns Optional.of(existingRole)
        assertThat(persistence.findUserRoleByName("existing name")).isPresent
        assertThat(persistence.findUserRoleByName("existing name").get()).isEqualTo(
            UserRoleSummary(id = ROLE_ID, name = "existing name")
        )
    }

    @Test
    fun `findUserRoleByName - not existing role name`() {
        every { userRoleRepo.findByName("not existing name") } returns Optional.empty()
        assertThat(persistence.findUserRoleByName("not existing name")).isNotPresent
    }

    @ParameterizedTest(name = "exists by id = {0}")
    @ValueSource(booleans = [true, false])
    fun existsById(isExisting: Boolean) {
        every { userRoleRepo.existsById(263L) } returns isExisting
        assertThat(persistence.existsById(263L)).isEqualTo(isExisting)
    }

}
