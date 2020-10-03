package io.cloudflight.jems.server.nuts.entity

import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity(name = "nuts_metadata")
data class NutsMetadata (

    @Id
    val id: Long = 1,

    @Column
    val nutsDate: LocalDate?,

    @Column
    val nutsTitle: String?

)
