package io.cloudflight.jems.server.project.controller.report.project.workPlan

import io.cloudflight.jems.api.project.dto.report.project.workPlan.ProjectReportWorkPackageDTO
import io.cloudflight.jems.api.project.dto.report.project.workPlan.UpdateProjectReportWorkPackageDTO
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackage
import io.cloudflight.jems.server.project.service.report.model.project.workPlan.ProjectReportWorkPackageUpdate
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

private val mapper = Mappers.getMapper(ProjectReportWorkPlanMapper::class.java)

fun List<ProjectReportWorkPackage>.toDto() = map { mapper.map(it) }
fun List<UpdateProjectReportWorkPackageDTO>.toModel() = map { mapper.map(it) }

@Mapper
interface ProjectReportWorkPlanMapper {
    fun map(model: ProjectReportWorkPackage): ProjectReportWorkPackageDTO
    fun map(dto: UpdateProjectReportWorkPackageDTO): ProjectReportWorkPackageUpdate
}
