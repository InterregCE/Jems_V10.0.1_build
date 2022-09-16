package io.cloudflight.jems.server.project.repository.report.file

import io.cloudflight.jems.server.project.entity.report.file.ReportProjectFileEntity
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFile
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileCreate
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import org.springframework.data.domain.Page
import java.time.ZonedDateTime

fun ProjectReportFileCreate.toEntity(
    userResolver: (Long) -> UserEntity,
    uploaded: ZonedDateTime,
    bucketForMinio: String,
    locationForMinio: String,
) = ReportProjectFileEntity(
    projectId = projectId,
    partnerId = partnerId,
    path = path,
    minioBucket = bucketForMinio,
    minioLocation = locationForMinio,
    name = name,
    type = type,
    size = size,
    user = userResolver.invoke(userId),
    uploaded = uploaded,
    description = "",
)

fun Page<ReportProjectFileEntity>.toModel() = map {
    ProjectReportFile(
        id = it.id,
        name = it.name,
        type = it.type,
        uploaded = it.uploaded,
        author = it.user.toModel(),
        size = it.size,
        description = it.description,
    )
}

fun UserEntity.toModel() = UserSimple(
    id = id,
    email = email,
    name = name,
    surname = surname,
)
