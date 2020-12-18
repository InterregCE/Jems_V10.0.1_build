package io.cloudflight.jems.server.project.entity.associatedorganization

import io.cloudflight.jems.server.project.entity.TranslationOrganizationId
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * Associated organization lang table
 */
@Entity(name = "project_associated_organization_transl")
data class ProjectAssociatedOrganizationTransl(

    @EmbeddedId
    val translationId: TranslationOrganizationId,

    @Column
    val roleDescription: String? = null

)
