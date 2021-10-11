package io.cloudflight.jems.server.user.service.model

data class User(
    val id: Long = 0,
    val email: String,
    val name: String,
    val surname: String,
    val userRole: UserRole,
    val userStatus: UserStatus
) {
    fun getDiff(old: User? = null): Map<String, Pair<Any?, Any?>> {
        val changes = mutableMapOf<String, Pair<Any?, Any?>>()

        if (old == null || email != old.email)
            changes["email"] = Pair(old?.email, email)

        if (old == null || name != old.name)
            changes["name"] = Pair(old?.name, name)

        if (old == null || surname != old.surname)
            changes["surname"] = Pair(old?.surname, surname)

        if (old == null || userRole.id != old.userRole.id)
            changes["userRole"] =
                Pair("${old?.userRole?.name}(id=${old?.userRole?.id})", "${userRole.name}(id=${userRole.id})")

        if (old == null || userStatus != old.userStatus)
            changes["userStatus"] = Pair(old?.userStatus, userStatus)

        return changes
    }
}
