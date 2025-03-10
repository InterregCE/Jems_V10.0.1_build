package io.cloudflight.jems.server.project.service.lumpsum.model

import io.cloudflight.jems.server.project.service.model.ProjectPeriod
import java.time.ZonedDateTime

const val PREPARATION_PERIOD_NUMBER = 0
const val CLOSURE_PERIOD_NUMBER = 255

val closurePeriod = ProjectPeriod(
    number = CLOSURE_PERIOD_NUMBER,
    start = -1,
    end = -1
)

data class ProjectLumpSum (
    var orderNr: Int,
    val programmeLumpSumId: Long,
    val period: Int? = null,
    val lumpSumContributions: List<ProjectPartnerLumpSum> = emptyList(),
    val fastTrack: Boolean = false,
    val readyForPayment: Boolean = false,
    val comment: String? = null,
    var paymentEnabledDate: ZonedDateTime? = null,
    var lastApprovedVersionBeforeReadyForPayment: String? = null,
    var installmentsAlreadyCreated: Boolean? = null,
    var linkedToEcPaymentId: Long? = null
)
