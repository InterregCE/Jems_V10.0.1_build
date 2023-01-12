package io.cloudflight.jems.server.plugin.services.report

import io.cloudflight.jems.plugin.contract.models.report.partner.control.ReportDesignatedControllerData
import io.cloudflight.jems.plugin.contract.models.report.partner.control.ReportVerificationData
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportDesignatedController
import io.cloudflight.jems.server.project.service.report.model.partner.identification.control.ReportVerification
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers


private val mapper = Mappers.getMapper(PartnerControlReportDataMapper::class.java)

fun ReportDesignatedController.toDataModel() = mapper.map(this)
fun ReportVerification.toDataModel() = mapper.map(this)
@Mapper
interface PartnerControlReportDataMapper {
    fun map(model: ReportDesignatedController): ReportDesignatedControllerData

    fun map(model: ReportVerification): ReportVerificationData
}
