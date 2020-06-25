package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.entity.Project
import java.util.Optional

class ProjectDtoUtilClass {
    companion object {

        fun getDtoFrom(project: Project): OutputProject {
            with (project) {
                return OutputProject(
                    id,
                    acronym,
                    applicant.toOutputUser(),
                    submissionDate
                )
            }
        }

        fun getDtoFrom(project: Optional<Project>): Optional<OutputProject> {
            if (project.isEmpty) {
                return Optional.empty()
            }
            return Optional.of(getDtoFrom(project.get()))
        }


    }
}
