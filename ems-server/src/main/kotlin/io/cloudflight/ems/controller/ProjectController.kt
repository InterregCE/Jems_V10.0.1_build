package io.cloudflight.ems.controller

import io.cloudflight.ems.api.ProjectApi
import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.service.ProjectService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectController(
    private val projectService: ProjectService
) : ProjectApi {

    override fun getProjects(pageable: Pageable): Page<OutputProject> {
        return projectService.getProjects(pageable)
    }

    override fun createProject(project: InputProject): OutputProject {
        return projectService.createProject(project)
    }

    override fun getProjectById(id: Long): ResponseEntity<OutputProject> {
        val project = projectService.getProjectById(id)
        if (project.isEmpty) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.ok(project.get())

    }
}
