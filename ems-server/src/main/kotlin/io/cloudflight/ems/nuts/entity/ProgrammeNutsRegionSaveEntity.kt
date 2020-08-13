package io.cloudflight.ems.nuts.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "programme_nuts")
data class ProgrammeNutsRegionSaveEntity (

    @Id
    @Column(name = "nuts_region_3_id")
    val nutsRegionId: String

)
