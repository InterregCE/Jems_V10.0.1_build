package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.programme.dto.OutputProgrammePriority
import io.cloudflight.ems.api.project.dto.InputProject
import io.cloudflight.ems.api.project.dto.InputProjectData
import io.cloudflight.ems.api.project.dto.OutputProject
import io.cloudflight.ems.api.project.dto.OutputProjectSimple
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectService {

    fun getById(id: Long): OutputProject

    /**
     * List of projects is restricted based on actual user role.
     */
    fun findAll(page: Pageable): Page<OutputProjectSimple>

    fun createProject(project: InputProject): OutputProject

    fun update(id: Long, projectData: InputProjectData): OutputProject

}
