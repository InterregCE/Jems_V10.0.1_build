package io.cloudflight.jems.server.project.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class ProjectData(

    @Column
    val title: String? = null,

    @Column
    val duration: Int? = null,

    @Column
    val intro: String? = null,

    @Column
    val introProgrammeLanguage: String? = null

)
