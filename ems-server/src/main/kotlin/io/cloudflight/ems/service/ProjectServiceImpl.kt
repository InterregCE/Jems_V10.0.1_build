package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.repository.ProjectRepository
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.ProjectDtoUtilClass.Companion.getDtoFrom
import io.cloudflight.ems.service.ProjectDtoUtilClass.Companion.toEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Service
class ProjectServiceImpl(
    private val projectRepo: ProjectRepository,
    private val auditService: AuditService,
    private val securityService: SecurityService
) : ProjectService {

    @Transactional(readOnly = true)
    override fun getProjects(page: Pageable): Page<OutputProject> {
        return projectRepo.findAll(page).map { getDtoFrom(it) }
    }

    @Transactional
    override fun createProject(project: InputProject): OutputProject {

        val createdProject = projectRepo.save(toEntity(project))
        auditService.logEvent(Audit.projectSubmitted(securityService.currentUser, createdProject.id.toString()))

        return getDtoFrom(createdProject)
    }

    @Transactional(readOnly = true)
    override fun getProjectById(id: Long): Optional<OutputProject> {
        return getDtoFrom(projectRepo.findById(id))
    }
}
