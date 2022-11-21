package io.cloudflight.jems.server.common.file.service

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import org.springframework.data.domain.Page
import java.time.ZonedDateTime

fun JemsFileCreate.toEntity(
    userResolver: (Long) -> UserEntity,
    uploaded: ZonedDateTime,
    bucketForMinio: String,
    locationForMinio: String,
) = JemsFileMetadataEntity(
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

fun Page<JemsFileMetadataEntity>.toModel() = map {
    JemsFile(
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
