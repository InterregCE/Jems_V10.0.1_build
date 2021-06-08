package io.cloudflight.jems.server.project.repository

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.project.entity.ProjectEntity
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentEntity
import io.cloudflight.jems.server.project.entity.assessment.ProjectAssessmentId
import io.cloudflight.jems.server.project.repository.assessment.ProjectAssessmentEligibilityRepository
import io.cloudflight.jems.server.project.repository.assessment.ProjectAssessmentQualityRepository
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.ProjectPersistence
import io.cloudflight.jems.server.project.service.model.Project
import io.cloudflight.jems.server.project.service.model.ProjectApplicantAndStatus
import io.cloudflight.jems.server.project.service.model.ProjectCallSettings
import io.cloudflight.jems.server.project.service.model.ProjectSummary
import io.cloudflight.jems.server.project.service.toApplicantAndStatus
import java.sql.Timestamp
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPersistenceProvider(
    private val projectVersionUtils: ProjectVersionUtils,
    private val projectRepository: ProjectRepository,
    private val projectPartnerRepository: ProjectPartnerRepository,
    private val projectAssessmentQualityRepository: ProjectAssessmentQualityRepository,
    private val projectAssessmentEligibilityRepository: ProjectAssessmentEligibilityRepository,
) : ProjectPersistence {

    @Transactional(readOnly = true)
    override fun getProject(projectId: Long, version: String?): Project {
        val project = getProjectOrThrow(projectId)

        val assessmentStep1 = ProjectAssessmentEntity(
            assessmentQuality = projectAssessmentQualityRepository.findById(project.idInStep(1)).orElse(null),
            assessmentEligibility = projectAssessmentEligibilityRepository.findById(project.idInStep(1)).orElse(null),
            eligibilityDecision = project.decisionEligibilityStep1,
            fundingDecision = project.decisionFundingStep1,
        )
        val assessmentStep2 = ProjectAssessmentEntity(
            assessmentQuality = projectAssessmentQualityRepository.findById(project.idInStep(2)).orElse(null),
            assessmentEligibility = projectAssessmentEligibilityRepository.findById(project.idInStep(2)).orElse(null),
            eligibilityDecision = project.decisionEligibilityStep2,
            preFundingDecision = project.decisionPreFundingStep2,
            fundingDecision = project.decisionFundingStep2,
        )

        return projectVersionUtils.fetch(version, projectId,
            currentVersionFetcher = {
                project.toModel(assessmentStep1 = assessmentStep1, assessmentStep2 = assessmentStep2)
            },
            // grouped this will be only one result
            previousVersionFetcher = { timestamp ->
                getProjectHistoricalData(projectId, timestamp, project, assessmentStep1, assessmentStep2)
            }
        ) ?: throw ApplicationVersionNotFoundException()
    }

    @Transactional(readOnly = true)
    override fun getApplicantAndStatusById(id: Long): ProjectApplicantAndStatus =
        projectRepository.getOne(id).toApplicantAndStatus()

    @Transactional(readOnly = true)
    override fun getProjectSummary(projectId: Long): ProjectSummary =
        projectRepository.getOne(projectId).toSummaryModel()

    @Transactional(readOnly = true)
    override fun getProjectCallSettings(projectId: Long): ProjectCallSettings =
        getProjectOrThrow(projectId).call.toSettingsModel()

    @Transactional(readOnly = true)
    override fun getProjects(pageable: Pageable, filterByOwnerId: Long?): Page<ProjectSummary> =
        if (filterByOwnerId == null)
            projectRepository.findAll(pageable).toModel()
        else
            projectRepository.findAllByApplicantId(pageable = pageable, applicantId = filterByOwnerId).toModel()

    @Transactional(readOnly = true)
    override fun getProjectUnitCosts(projectId: Long): List<ProgrammeUnitCost> =
        getProjectOrThrow(projectId).call.unitCosts.toModel()

    @Transactional(readOnly = true)
    override fun getProjectIdForPartner(partnerId: Long) =
        projectPartnerRepository.getProjectIdForPartner(partnerId) ?: throw ResourceNotFoundException("ProjectPartner")

    @Transactional(readOnly = true)
    override fun getProjectPeriods(projectId: Long) =
        getProjectOrThrow(projectId).periods.toProjectPeriods()

    private fun getProjectOrThrow(projectId: Long) =
        projectRepository.findById(projectId).orElseThrow { ResourceNotFoundException("project") }

    private fun getProjectHistoricalData(
        projectId: Long,
        timestamp: Timestamp,
        project: ProjectEntity,
        assessmentStep1: ProjectAssessmentEntity,
        assessmentStep2: ProjectAssessmentEntity,
    ): Project {
        val periods = projectRepository.findPeriodsByProjectIdAsOfTimestamp(projectId, timestamp).toProjectPeriodHistoricalData();
        return projectRepository.findByIdAsOfTimestamp(projectId, timestamp)
            .toProjectEntryWithDetailData(project, periods, assessmentStep1, assessmentStep2)
    }

    private fun ProjectEntity.idInStep(step: Int) = ProjectAssessmentId(this, step)
}
