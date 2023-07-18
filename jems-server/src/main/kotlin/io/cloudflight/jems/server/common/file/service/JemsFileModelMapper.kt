package io.cloudflight.jems.server.common.file.service

import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileCreate
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.user.entity.UserEntity
import io.cloudflight.jems.server.user.service.model.User
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
    description = defaultDescription,
)

fun Page<JemsFileMetadataEntity>.toModel() = map { it.toFullModel() }

fun JemsFileMetadataEntity.toFullModel() = JemsFile(
    id = id,
    name = name,
    type = type,
    uploaded = uploaded,
    author = user.toModel(),
    size = size,
    description = description,
    indexedPath = path,
)

fun UserEntity.toModel() = UserSimple(
    id = id,
    email = email,
    name = name,
    surname = surname,
)

fun JemsFileMetadata.toModel(createModel: JemsFileCreate) = JemsFile(
    id = id,
    name = name,
    uploaded = uploaded,
    type = createModel.type,
    size = createModel.size,
    description = createModel.defaultDescription,
    indexedPath = createModel.path,
    author = UserSimple(createModel.userId, "", "", "")
)
