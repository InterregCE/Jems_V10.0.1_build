package io.cloudflight.ems.nuts.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity(name = "nuts_region_2")
data class NutsRegion2 (

    @Id
    override val id: String,

    @Column(nullable = false)
    override val title: String,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "nuts_region_1_id")
    val region1: NutsRegion1

): NutsBaseEntity
