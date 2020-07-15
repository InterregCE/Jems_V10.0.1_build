package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.OutputProjectSimple
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectService {

    fun getById(id: Long): OutputProject

    /**
     * List of projects is restricted based on actual user role.
     */
    fun findAll(page: Pageable): Page<OutputProjectSimple>

    fun createProject(project: InputProject): OutputProject

}
