package io.cloudflight.jems.server.user.service.user.update_user

import io.cloudflight.jems.server.user.service.model.User
import io.cloudflight.jems.server.user.service.model.UserChange
import io.cloudflight.jems.server.user.service.model.UserSettings
import io.cloudflight.jems.server.user.service.model.UserSettingsChange

interface UpdateUserInteractor {

    fun updateUser(user: UserChange): User

    fun updateUserSetting(userSettings: UserSettingsChange): UserSettings

}
