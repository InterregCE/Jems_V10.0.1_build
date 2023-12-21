package io.cloudflight.jems.server.common

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.server.call.service.model.IdNamePair
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

private val commonTOMapper = Mappers.getMapper(CommonDTOMapper::class.java)

fun List<IdNamePair>.toDTO() =
    map { it.toIdNamePairDTO() }

fun IdNamePair.toIdNamePairDTO() =
    commonTOMapper.map(this)

fun ExportResult.toResponseEntity(): ResponseEntity<ByteArrayResource> =
    ResponseEntity.ok()
        .contentLength(this.content.size.toLong())
        .header(HttpHeaders.CONTENT_TYPE, contentType)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.fileName}\"")
        .body(ByteArrayResource(this.content))

fun Pair<String, ByteArray>.toResponseFile(): ResponseEntity<ByteArrayResource> =
    ResponseEntity.ok()
        .contentLength(this.second.size.toLong())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${this.first}\"")
        .body(ByteArrayResource(this.second))

@Mapper
abstract class CommonDTOMapper {

    abstract fun map(idNamePair: IdNamePair): IdNamePairDTO
}



