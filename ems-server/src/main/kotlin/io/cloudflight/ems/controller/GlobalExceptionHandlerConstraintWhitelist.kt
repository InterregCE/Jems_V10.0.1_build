package io.cloudflight.ems.controller

/**
 * Contains all DB errors, that can be resend and translated on FE.
 * We should not send all of them to the FE because of the security.
 */
val DB_ERROR_WHITE_LIST = listOf(
    "user.lastAdmin.cannot.be.removed"
)
