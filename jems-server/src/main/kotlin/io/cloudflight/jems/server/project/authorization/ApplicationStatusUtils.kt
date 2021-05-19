package io.cloudflight.jems.server.project.authorization

import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO
import io.cloudflight.jems.api.project.dto.status.ApplicationStatusDTO.*

fun ApplicationStatusDTO.isNotSubmittedNow(): Boolean =
    this == DRAFT || this == STEP1_DRAFT || this == RETURNED_TO_APPLICANT

fun ApplicationStatusDTO.isNotFinallyFunded(): Boolean =
    this != APPROVED && this != NOT_APPROVED && this != INELIGIBLE &&
        this != STEP1_APPROVED && this != STEP1_NOT_APPROVED && this != STEP1_INELIGIBLE

fun ApplicationStatusDTO.wasSubmittedAtLeastOnce(): Boolean =
    this != DRAFT && this != STEP1_DRAFT
