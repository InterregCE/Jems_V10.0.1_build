package io.cloudflight.jems.server.programme.service.userrole

interface ProgrammeDataPersistence {

    fun getProgrammeName(): String?
    fun getDefaultUserRole(): Long?
    fun updateDefaultUserRole(userRoleId: Long)

}
