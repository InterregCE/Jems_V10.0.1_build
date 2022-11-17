package io.cloudflight.jems.server.user.controller

import io.cloudflight.jems.api.user.UserRegistrationApi
import io.cloudflight.jems.api.user.dto.UserDTO
import io.cloudflight.jems.api.user.dto.UserRegistrationDTO
import io.cloudflight.jems.server.user.service.user.activate_user.ActivateUserInteractor
import io.cloudflight.jems.server.user.service.user.register_user.RegisterUserInteractor
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class UserRegistrationController(
    val registerUserInteractor: RegisterUserInteractor,
    val activateUserInteractor: ActivateUserInteractor
) : UserRegistrationApi {

    override fun registerApplicant(user: UserRegistrationDTO): UserDTO {
        return registerUserInteractor.registerUser(user.toModel()).toDto()
    }

    override fun confirmUserRegistration(token: UUID): Boolean {
        return activateUserInteractor.activateUser(token)
    }

}
