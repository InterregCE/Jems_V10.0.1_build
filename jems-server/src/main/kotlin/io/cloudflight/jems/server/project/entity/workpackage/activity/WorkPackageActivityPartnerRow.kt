package io.cloudflight.jems.server.project.entity.workpackage.activity

interface WorkPackageActivityPartnerRow {
    val workPackageId: Long
    val activityNumber: Int
    val projectPartnerId: Long
}
