package io.cloudflight.jems.server.project.entity

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.OneToMany

@Embeddable
data class ProjectData(

    @Column
    val duration: Int? = null,

    // title, intro
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.projectId")
    val translatedValues: Set<ProjectTransl> = emptySet()

)
