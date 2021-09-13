package io.cloudflight.jems.server.project.entity.workpackage.activity

interface WorkPackageActivityPartnerRow {
    val activityId: Long
    val workPackageId: Long
    val projectPartnerId: Long
}
