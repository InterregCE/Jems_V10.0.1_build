package io.cloudflight.jems.server.project.service.report.partner.base

import io.cloudflight.jems.server.project.service.report.model.partner.ProjectPartnerReportSummary
import org.springframework.data.domain.Page

fun Page<ProjectPartnerReportSummary>.removeEligibleAfterControlFromNotInControlOnes() =
    this.onEach {
        if (!it.status.isFinalized()) {
            it.totalEligibleAfterControl = null
            it.controlEnd = null
        }
        if (it.status.isOpenForNumbersChanges()) {
            it.totalAfterSubmitted = null
        }
    }
