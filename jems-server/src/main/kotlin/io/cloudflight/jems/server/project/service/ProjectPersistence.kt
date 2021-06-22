package io.cloudflight.jems.server.project.service

import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.service.application.ApplicationStatus
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProjectPersistence {

    fun getProject(projectId: Long, version: String? = null): Project

    fun getApplicantAndStatusById(id: Long): ProjectApplicantAndStatus

    fun getProjectSummary(projectId: Long): ProjectSummary

    fun getProjectCallSettings(projectId: Long): ProjectCallSettings

    fun getProjects(pageable: Pageable, filterByOwnerId: Long? = null): Page<ProjectSummary>

    fun getProjectUnitCosts(projectId: Long): List<ProgrammeUnitCost>

    fun getProjectPeriods(projectId: Long): List<ProjectPeriod>

    fun getProjectIdForPartner(partnerId: Long): Long

    fun createProjectWithStatus(acronym: String, status: ApplicationStatus, userId: Long, callId: Long): Project

}
