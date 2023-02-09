package io.cloudflight.jems.server.common.file.service

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileCreate
import io.cloudflight.jems.server.project.service.report.model.file.UserSimple
import io.cloudflight.jems.server.user.entity.UserEntity
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

fun Page<JemsFileMetadataEntity>.toModel() = map { it.toFullModel() }

fun JemsFileMetadataEntity.toFullModel() = JemsFile(
    id = id,
    name = name,
    type = type,
    uploaded = uploaded,
    author = user.toModel(),
    size = size,
    description = description
)

fun UserEntity.toModel() = UserSimple(
    id = id,
    email = email,
    name = name,
    surname = surname,
)
