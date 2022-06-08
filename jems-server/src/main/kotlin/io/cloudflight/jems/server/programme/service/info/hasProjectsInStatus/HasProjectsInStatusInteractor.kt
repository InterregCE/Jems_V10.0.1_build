package io.cloudflight.jems.server.programme.service.info.hasProjectsInStatus

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO

interface HasProjectsInStatusInteractor {

    fun programmeHasProjectsInStatus(projectStatus: ApplicationStatusDTO): Boolean
}
