package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.api.dto.OutputProject
import io.cloudflight.ems.api.dto.OutputProjectSimple
import io.cloudflight.ems.entity.User
import io.cloudflight.ems.entity.Project
import io.cloudflight.ems.entity.ProjectStatus

fun InputProject.toEntity(applicant: User, status: ProjectStatus) = Project(
    id = null,
    acronym = this.acronym!!,
    submissionDate = null,
    applicant = applicant,
    projectStatus = status
)

fun Project.toOutputProject() = OutputProject(
    id = id,
    acronym = acronym,
    applicant = applicant.toOutputUser(),
    submissionDate = submissionDate,
    projectStatus = projectStatus.toOutputProjectStatus(),
    qualityAssessment = qualityAssessment?.toOutputProjectQualityAssessment(),
    eligibilityAssessment = eligibilityAssessment?.toOutputProjectEligibilityAssessment()
)

fun Project.toOutputProjectSimple() = OutputProjectSimple(
    id = id,
    acronym = acronym,
    submissionDate = submissionDate,
    projectStatus = projectStatus.toOutputProjectStatus()
)
