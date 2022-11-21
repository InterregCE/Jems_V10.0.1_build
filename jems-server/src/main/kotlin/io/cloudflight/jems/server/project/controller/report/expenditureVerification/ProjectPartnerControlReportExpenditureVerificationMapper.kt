package io.cloudflight.jems.server.project.controller.report.expenditureVerification

import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationDTO
import io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification.ProjectPartnerControlReportExpenditureVerificationUpdateDTO
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerification
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerControlReportExpenditureVerificationUpdate
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectPartnerControlReportMapper::class.java)

fun ProjectPartnerControlReportExpenditureVerification.toDto() =
    mapper.map(this)

fun ProjectPartnerControlReportExpenditureVerificationUpdateDTO.toModel() =
    mapper.map(this)

fun List<ProjectPartnerControlReportExpenditureVerification>.toDto() = map { mapper.map(it) }.toList()
fun List<ProjectPartnerControlReportExpenditureVerificationUpdateDTO>.toModel() = map { mapper.map(it) }.toList()

@Mapper
interface ProjectPartnerControlReportMapper {
    fun map(partnerReportExpenditureVerification: ProjectPartnerControlReportExpenditureVerification): ProjectPartnerControlReportExpenditureVerificationDTO
    fun map(
        partnerReportExpenditureVerificationDTO: ProjectPartnerControlReportExpenditureVerificationUpdateDTO
    ): ProjectPartnerControlReportExpenditureVerificationUpdate
}

