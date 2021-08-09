package io.cloudflight.jems.server.project.service.file.delete_project_file

interface DeleteProjectFileInteractor {

    fun delete(projectId: Long, fileId: Long)

}
