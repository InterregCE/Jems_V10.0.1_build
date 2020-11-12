package io.cloudflight.jems.server.nuts.entity

import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "nuts_region_3")
data class NutsRegion3 (

    @Id
    override val id: String,

    @field:NotNull
    override val title: String,

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "nuts_region_2_id")
    @field:NotNull
    val region2: NutsRegion2

): NutsBaseEntity
