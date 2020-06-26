package io.cloudflight.ems.service

import io.cloudflight.ems.api.dto.InputProject
import io.cloudflight.ems.entity.Account
import io.cloudflight.ems.entity.Project

fun InputProject.toEntity(applicant: Account) = Project(
    id = null,
    acronym = this.acronym!!,
    submissionDate = this.submissionDate!!,
    applicant = applicant
)
