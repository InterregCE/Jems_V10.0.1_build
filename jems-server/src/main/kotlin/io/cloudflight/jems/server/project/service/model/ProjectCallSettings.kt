package io.cloudflight.jems.server.project.service.model

import io.cloudflight.jems.api.call.dto.CallType
import io.cloudflight.jems.server.call.service.model.ApplicationFormFieldConfiguration
import io.cloudflight.jems.server.call.service.model.CallCostOption
import io.cloudflight.jems.server.call.service.model.ProjectCallFlatRate
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeLumpSum
import io.cloudflight.jems.server.programme.service.costoption.model.ProgrammeUnitCost
import io.cloudflight.jems.server.programme.service.stateaid.model.ProgrammeStateAid
import java.time.ZonedDateTime

data class ProjectCallSettings(
    val callId: Long,
    val callName: String,
    val callType: CallType,
    val startDate: ZonedDateTime,
    val endDate: ZonedDateTime,
    val endDateStep1: ZonedDateTime?,
    val lengthOfPeriod: Int,
    val isAdditionalFundAllowed: Boolean,
    val flatRates: Set<ProjectCallFlatRate>,
    val lumpSums: List<ProgrammeLumpSum>,
    val unitCosts: List<ProgrammeUnitCost>,
    val stateAids: List<ProgrammeStateAid>,
    var applicationFormFieldConfigurations: MutableSet<ApplicationFormFieldConfiguration>,
    val preSubmissionCheckPluginKey: String?,
    val firstStepPreSubmissionCheckPluginKey: String?,
    val costOption: CallCostOption,
) {
    fun isCallStep1Closed(): Boolean {
        return if (endDateStep1 == null) {
            false
        }
        else {
            ZonedDateTime.now().isBefore(startDate) || ZonedDateTime.now().isAfter(endDateStep1)
        }
    }

    fun isCallStep2Closed(): Boolean {
        return ZonedDateTime.now().isBefore(startDate) || ZonedDateTime.now().isAfter(endDate)
    }
}
