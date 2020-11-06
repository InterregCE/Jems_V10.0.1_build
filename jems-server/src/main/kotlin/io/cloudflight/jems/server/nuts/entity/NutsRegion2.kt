package io.cloudflight.jems.server.nuts.entity

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "nuts_region_2")
data class NutsRegion2 (

    @Id
    override val id: String,

    @field:NotNull
    override val title: String,

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "nuts_region_1_id")
    @field:NotNull
    val region1: NutsRegion1

): NutsBaseEntity
