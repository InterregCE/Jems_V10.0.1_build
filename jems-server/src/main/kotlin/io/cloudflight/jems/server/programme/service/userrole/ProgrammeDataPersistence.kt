package io.cloudflight.jems.server.programme.service.userrole

import io.cloudflight.jems.server.programme.service.model.ProgrammeData

interface ProgrammeDataPersistence {

    fun getProgrammeData(): ProgrammeData
    fun getProgrammeName(): String?
    fun getDefaultUserRole(): Long?
    fun updateDefaultUserRole(userRoleId: Long)

}
