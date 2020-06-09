package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.OutputProjectFile
import io.cloudflight.ems.entity.ProjectFile

class ProjectFileDtoUtilClass {
    companion object {

        fun getDtoFrom(projectFile: ProjectFile): OutputProjectFile {
            with (projectFile) {
                return OutputProjectFile(
                    id,
                    name,
                    description,
                    size,
                    updated,
                    "programme user"
                )
            }
        }

    }
}
