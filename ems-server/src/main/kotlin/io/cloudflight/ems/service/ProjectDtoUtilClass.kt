package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.entity.Project
import java.util.Optional

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

        fun getDtoFrom(project: Optional<Project>): Optional<OutputProject> {
            if (project.isEmpty) {
                return Optional.empty()
            }
            return Optional.of(getDtoFrom(project.get()))
        }


    }
}
