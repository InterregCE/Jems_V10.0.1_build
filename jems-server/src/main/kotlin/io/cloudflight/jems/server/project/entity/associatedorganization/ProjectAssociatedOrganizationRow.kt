package io.cloudflight.jems.server.project.entity.associatedorganization

import io.cloudflight.jems.server.common.entity.TranslationView

interface ProjectAssociatedOrganizationRow: TranslationView {
    val id: Long
    val active: Boolean
    val nameInOriginalLanguage: String?
    val nameInEnglish: String?
    val sortNumber: Int?
    val roleDescription: String?
}
