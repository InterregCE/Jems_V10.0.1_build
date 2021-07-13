package io.cloudflight.jems.server.programme.service.userrole

interface ProgrammeDataPersistence {

    fun getDefaultUserRole(): Long?
    fun updateDefaultUserRole(userRoleId: Long)

}
