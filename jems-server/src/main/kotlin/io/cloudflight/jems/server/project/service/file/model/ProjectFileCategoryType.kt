package io.cloudflight.jems.server.project.service.file.model

enum class ProjectFileCategoryType(val parent: ProjectFileCategoryType? = null) {
    ALL,
    ASSESSMENT(ALL),
    APPLICATION(ALL),
    MODIFICATION(ALL),
    PARTNER(APPLICATION),
    INVESTMENT(APPLICATION),
}
