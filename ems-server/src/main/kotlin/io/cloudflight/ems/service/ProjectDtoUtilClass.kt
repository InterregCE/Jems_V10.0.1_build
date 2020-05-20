package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.entity.Project

class ProjectDtoUtilClass {
    companion object {

        fun toEntity(projectCreate: InputProject): Project {
            with (projectCreate) {
                return Project(
                    null,
                    acronym,
                    submissionDate
                )
            }
        }

        fun getDtoFrom(project: Project): OutputProject {
            with (project) {
                return OutputProject(
                    id,
                    acronym,
                    submissionDate
                )
            }
        }

    }
}
