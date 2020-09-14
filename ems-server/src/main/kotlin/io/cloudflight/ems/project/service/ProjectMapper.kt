package io.cloudflight.ems.project.service

import io.cloudflight.ems.api.project.dto.InputProject
import io.cloudflight.ems.api.project.dto.InputProjectData
import io.cloudflight.ems.api.project.dto.InputProjectHorizontalPrinciples
import io.cloudflight.ems.api.project.dto.InputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.InputProjectManagement
import io.cloudflight.ems.api.project.dto.OutputProject
import io.cloudflight.ems.api.project.dto.OutputProjectData
import io.cloudflight.ems.api.project.dto.OutputProjectLongTermPlans
import io.cloudflight.ems.api.project.dto.OutputProjectManagement
import io.cloudflight.ems.api.project.dto.OutputProjectSimple
import io.cloudflight.ems.call.entity.Call
import io.cloudflight.ems.call.service.toOutputCallWithDates
import io.cloudflight.ems.project.entity.Project
import io.cloudflight.ems.project.entity.ProjectStatus
import io.cloudflight.ems.programme.entity.ProgrammePriorityPolicy
import io.cloudflight.ems.programme.service.toOutputProgrammePriorityPolicy
import io.cloudflight.ems.programme.service.toOutputProgrammePrioritySimple
import io.cloudflight.ems.project.dto.ProjectApplicantAndStatus
import io.cloudflight.ems.project.entity.ProjectData
import io.cloudflight.ems.project.entity.ProjectDescription
import io.cloudflight.ems.project.entity.ProjectHorizontalPrinciples
import io.cloudflight.ems.user.entity.User
import io.cloudflight.ems.user.service.toOutputUser

fun InputProject.toEntity(
    call: Call,
    applicant: User,
    status: ProjectStatus
) = Project(
    id = null,
    call = call,
    acronym = this.acronym!!,
    applicant = applicant,
    projectStatus = status
)

fun Project.toOutputProject() = OutputProject(
    id = id,
    call = call.toOutputCallWithDates(),
    acronym = acronym,
    applicant = applicant.toOutputUser(),
    projectStatus = projectStatus.toOutputProjectStatus(),
    firstSubmission = firstSubmission?.toOutputProjectStatus(),
    lastResubmission = lastResubmission?.toOutputProjectStatus(),
    qualityAssessment = qualityAssessment?.toOutputProjectQualityAssessment(),
    eligibilityAssessment = eligibilityAssessment?.toOutputProjectEligibilityAssessment(),
    eligibilityDecision = eligibilityDecision?.toOutputProjectStatus(),
    fundingDecision = fundingDecision?.toOutputProjectStatus(),
    projectData = projectData?.toOutputProjectData(),
    projectManagement = projectDescription?.toOutputProjectManagement(),
    projectLongTermPlans = projectDescription?.toOutputProjectLongTermPlans()
    // projectPartners are handled in its own endpoint
)

fun Project.toOutputProjectSimple() = OutputProjectSimple(
    id = id,
    callName = call.name,
    acronym = acronym,
    projectStatus = projectStatus.status,
    firstSubmissionDate = firstSubmission?.updated,
    lastResubmissionDate = lastResubmission?.updated,
    specificObjectiveCode = projectData?.priorityPolicy?.code,
    programmePriorityCode = projectData?.priorityPolicy?.programmePriority?.code
)

fun Project.toApplicantAndStatus() = ProjectApplicantAndStatus(
    applicantId = applicant.id!!,
    projectStatus = projectStatus.status
)

fun InputProjectData.toEntity(project: Project, priorityPolicy: ProgrammePriorityPolicy?) = ProjectData(
    projectId = project.id!!,
    project = project,
    title = title,
    duration = duration,
    intro = intro,
    priorityPolicy = priorityPolicy,
    introProgrammeLanguage = introProgrammeLanguage
)

fun ProjectData.toOutputProjectData() = OutputProjectData(
    title = title,
    duration = duration,
    intro = intro,
    introProgrammeLanguage = introProgrammeLanguage,
    specificObjective = priorityPolicy?.toOutputProgrammePriorityPolicy(),
    programmePriority = priorityPolicy?.programmePriority?.toOutputProgrammePrioritySimple()
)

fun InputProjectManagement.toEntity(project: Project, projectLongTermPlans: OutputProjectLongTermPlans?) =
    ProjectDescription(
        projectId = project.id!!,
        project = project,
        projectCoordination = projectCoordination,
        projectQualityAssurance = projectQualityAssurance,
        projectCommunication = projectCommunication,
        projectFinancialManagement = projectFinancialManagement,
        projectJointDevelopment = projectJointDevelopment,
        projectJointImplementation = projectJointImplementation,
        projectJointStaffing = projectJointStaffing,
        projectJointFinancing = projectJointFinancing,
        projectHorizontalPrinciples = projectHorizontalPrinciples?.toEntity(project),
        projectOwnership = projectLongTermPlans?.projectOwnership,
        projectDurability = projectLongTermPlans?.projectDurability,
        projectTransferability = projectLongTermPlans?.projectTransferability
    )

fun InputProjectLongTermPlans.toEntity(project: Project, projectManagement: OutputProjectManagement?) =
    ProjectDescription(
        projectId = project.id!!,
        project = project,
        projectCoordination = projectManagement?.projectCoordination,
        projectQualityAssurance = projectManagement?.projectQualityAssurance,
        projectCommunication = projectManagement?.projectCommunication,
        projectFinancialManagement = projectManagement?.projectFinancialManagement,
        projectJointDevelopment = projectManagement?.projectJointDevelopment,
        projectJointImplementation = projectManagement?.projectJointImplementation,
        projectJointStaffing = projectManagement?.projectJointStaffing,
        projectJointFinancing = projectManagement?.projectJointFinancing,
        projectHorizontalPrinciples = projectManagement?.projectHorizontalPrinciples?.toEntity(project),
        projectOwnership = projectOwnership,
        projectDurability = projectDurability,
        projectTransferability = projectTransferability
    )

fun ProjectDescription.toOutputProjectManagement() = OutputProjectManagement(
    projectCoordination = projectCoordination,
    projectQualityAssurance = projectQualityAssurance,
    projectCommunication = projectCommunication,
    projectFinancialManagement = projectFinancialManagement,
    projectJointDevelopment = projectJointDevelopment,
    projectJointImplementation = projectJointImplementation,
    projectJointStaffing = projectJointStaffing,
    projectJointFinancing = projectJointFinancing,
    projectHorizontalPrinciples = projectHorizontalPrinciples?.toOutputHorizontalPrinciples()
)

fun ProjectDescription.toOutputProjectLongTermPlans() = OutputProjectLongTermPlans(
    projectOwnership = projectOwnership,
    projectDurability = projectDurability,
    projectTransferability = projectTransferability
)

fun InputProjectHorizontalPrinciples.toEntity(project: Project) = ProjectHorizontalPrinciples(
    projectId = project.id!!,
    project = project,
    sustainableDevelopmentCriteriaEffect = sustainableDevelopmentCriteriaEffect,
    sustainableDevelopmentDescription = sustainableDevelopmentDescription,
    equalOpportunitiesEffect = equalOpportunitiesEffect,
    equalOpportunitiesDescription = equalOpportunitiesDescription,
    sexualEqualityEffect = sexualEqualityEffect,
    sexualEqualityDescription = sexualEqualityDescription
)

fun ProjectHorizontalPrinciples.toOutputHorizontalPrinciples() = InputProjectHorizontalPrinciples(
    sustainableDevelopmentCriteriaEffect = sustainableDevelopmentCriteriaEffect,
    sustainableDevelopmentDescription = sustainableDevelopmentDescription,
    equalOpportunitiesEffect = equalOpportunitiesEffect,
    equalOpportunitiesDescription = equalOpportunitiesDescription,
    sexualEqualityEffect = sexualEqualityEffect,
    sexualEqualityDescription = sexualEqualityDescription
)
