package io.cloudflight.jems.server.common

import io.cloudflight.jems.api.common.dto.IdNamePairDTO
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.cloudflight.jems.server.project.service.file.model.ProjectFileMetadata
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.nio.charset.StandardCharsets

private val commonTOMapper = Mappers.getMapper(CommonDTOMapper::class.java)

fun List<IdNamePair>.toDTO() =
    map { it.toIdNamePairDTO() }

fun IdNamePair.toIdNamePairDTO() =
    commonTOMapper.map(this)

fun ExportResult.toResponseEntity(): ResponseEntity<ByteArrayResource> {
    val contentDisposition = this.fileName.fileNameToContentDispositionUTF8()
    return ResponseEntity.ok()
        .contentLength(this.content.size.toLong())
        .header(HttpHeaders.CONTENT_TYPE, contentType)
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
        .body(ByteArrayResource(this.content))
}

fun Pair<String, ByteArray>.toResponseFile(): ResponseEntity<ByteArrayResource> {
    val contentDisposition = this.first.fileNameToContentDispositionUTF8()
    return ResponseEntity.ok()
        .contentLength(this.second.size.toLong())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
        .body(ByteArrayResource(this.second))
}

fun Pair<ProjectFileMetadata, ByteArray>.toResponseEntity(): ResponseEntity<ByteArrayResource> {
    val contentDisposition = this.first.name.fileNameToContentDispositionUTF8()
    return ResponseEntity.ok()
        .contentLength(this.second.size.toLong())
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
        .body(ByteArrayResource(this.second))
}
@Mapper
abstract class CommonDTOMapper {

    abstract fun map(idNamePair: IdNamePair): IdNamePairDTO
}

private fun String.fileNameToContentDispositionUTF8() =
    ContentDisposition.attachment().filename(this, StandardCharsets.UTF_8).build()

