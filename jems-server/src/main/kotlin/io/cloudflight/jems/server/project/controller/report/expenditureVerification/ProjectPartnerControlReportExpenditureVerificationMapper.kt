package io.cloudflight.jems.server.project.controller.report.expenditureVerification

import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationUpdateDTO
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control.ProjectPartnerReportExpenditureVerificationUpdate
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectPartnerControlReportMapper::class.java)

fun ProjectPartnerReportExpenditureVerification.toDto() =
    mapper.map(this)

fun ProjectPartnerControlReportExpenditureVerificationUpdateDTO.toModel() =
    mapper.map(this)

fun List<ProjectPartnerReportExpenditureVerification>.toDto() = map { mapper.map(it) }
fun List<ProjectPartnerControlReportExpenditureVerificationUpdateDTO>.toModel() = map { mapper.map(it) }.toList()

@Mapper
interface ProjectPartnerControlReportMapper {
    fun map(partnerReportExpenditureVerification: ProjectPartnerReportExpenditureVerification): ProjectPartnerControlReportExpenditureVerificationDTO
    fun map(
        partnerReportExpenditureVerificationDTO: ProjectPartnerControlReportExpenditureVerificationUpdateDTO
    ): ProjectPartnerReportExpenditureVerificationUpdate
}

