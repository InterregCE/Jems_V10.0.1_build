package io.cloudflight.jems.server.project.entity.workpackage.activity

import java.io.Serializable
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_work_package_activity_transl")
data class WorkPackageActivityTranslationEntity(

    @EmbeddedId
    val translationId: WorkPackageActivityTranslationId,

    val title: String? = null,

    val description: String? = null,
): Serializable
