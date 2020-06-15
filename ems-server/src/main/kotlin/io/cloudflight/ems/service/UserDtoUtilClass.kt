package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputUser
import io.cloudflight.ems.api.dto.OutputUserRole
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.entity.UserRole
import java.util.Optional

class UserDtoUtilClass {
    companion object {

        fun getDtoFrom(user: User): OutputUser {
            with (user) {
                return OutputUser(
                    id,
                    email,
                    name,
                    surname,
                    getDtoFrom(userRole)
                )
            }
        }

        fun getDtoFrom(userRole: UserRole): OutputUserRole {
            with (userRole) {
                return OutputUserRole(
                    id,
                    name
                )
            }
        }

    }
}
