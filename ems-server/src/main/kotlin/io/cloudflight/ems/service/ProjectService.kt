package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.api.dto.OutputProject
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectService {

    fun getById(id: Long): OutputProject

    fun findAll(page: Pageable): Page<OutputProject>

    fun createProject(project: InputProject): OutputProject

}
