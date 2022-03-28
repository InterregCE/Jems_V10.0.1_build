package io.cloudflight.jems.server.project.service.report.partner.workPlan

import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.server.project.service.report.model.workPlan.ProjectPartnerReportWorkPackage

interface ProjectReportWorkPlanPersistence {

    fun getPartnerReportWorkPlanById(partnerId: Long, reportId: Long): List<ProjectPartnerReportWorkPackage>

    fun existsByActivityId(partnerId: Long, reportId: Long, activityId: Long): Boolean

    fun existsByDeliverableId(partnerId: Long, reportId: Long, activityId: Long, deliverableId: Long): Boolean

    fun existsByOutputId(partnerId: Long, reportId: Long, outputId: Long): Boolean

    fun updatePartnerReportWorkPackage(workPackageId: Long, translations: Set<InputTranslation>)

    fun updatePartnerReportWorkPackageActivity(activityId: Long, translations: Set<InputTranslation>)

    fun updatePartnerReportWorkPackageDeliverable(deliverableId: Long, contribution: Boolean?, evidence: Boolean?)

    fun updatePartnerReportWorkPackageOutput(outputId: Long, contribution: Boolean?, evidence: Boolean?)

}
