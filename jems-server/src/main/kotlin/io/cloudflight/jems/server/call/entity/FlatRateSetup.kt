package io.cloudflight.jems.server.call.entity

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_call_flat_rate")
data class FlatRateSetup(

    @EmbeddedId
    val setupId: FlatRateSetupId,

    @Column(nullable = false)
    var rate: Int,

    @Column(nullable = false)
    var isAdjustable: Boolean

)
