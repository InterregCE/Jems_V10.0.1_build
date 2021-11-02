package io.cloudflight.jems.server.user.controller

import io.cloudflight.jems.api.project.dto.UserPermissionFilterDTO
import io.cloudflight.jems.api.user.UserApi
import io.cloudflight.jems.api.user.dto.OutputUser
import io.cloudflight.jems.api.user.dto.PasswordDTO
import io.cloudflight.jems.api.user.dto.UserChangeDTO
import io.cloudflight.jems.api.user.dto.UserDTO
import io.cloudflight.jems.api.user.dto.UserSearchRequestDTO
import io.cloudflight.jems.api.user.dto.UserSummaryDTO
import io.cloudflight.jems.server.user.service.user.create_user.CreateUserInteractor
import io.cloudflight.jems.server.user.service.user.get_user.GetUserInteractor
import io.cloudflight.jems.server.user.service.user.update_user.UpdateUserInteractor
import io.cloudflight.jems.server.user.service.user.update_user_password.UpdateUserPasswordInteractor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val getUserInteractor: GetUserInteractor,
    private val createUserInteractor: CreateUserInteractor,
    private val updateUserInteractor: UpdateUserInteractor,
    private val updateUserPasswordInteractor: UpdateUserPasswordInteractor,
) : UserApi {

    override fun list(pageable: Pageable, searchRequest: UserSearchRequestDTO?): Page<UserSummaryDTO> =
        getUserInteractor.getUsers(pageable, searchRequest?.toModel()).toDto()

    override fun createUser(user: UserChangeDTO): UserDTO =
        createUserInteractor.createUser(user.toModel()).toDto()

    override fun updateUser(user: UserChangeDTO): UserDTO =
        updateUserInteractor.updateUser(user.toModel()).toDto()

    override fun getById(id: Long): UserDTO =
        getUserInteractor.getUserById(id).toDto()

    override fun resetPassword(userId: Long, newPassword: String) =
        updateUserPasswordInteractor.resetUserPassword(userId, newPassword)

    override fun changeMyPassword(passwordData: PasswordDTO) =
        updateUserPasswordInteractor.updateMyPassword(passwordData.toModel())

    override fun listUsersByPermissions(userPermissionFilter: UserPermissionFilterDTO): List<OutputUser> =
        getUserInteractor.getUsersFilteredByPermissions(
            needsToHaveAtLeastOneFrom = userPermissionFilter.needsToHaveAtLeastOneFrom.toModel(),
            needsNotToHaveAnyOf = userPermissionFilter.needsNotToHaveAnyOf.toModel(),
        ).map { it.toDto() }

}
