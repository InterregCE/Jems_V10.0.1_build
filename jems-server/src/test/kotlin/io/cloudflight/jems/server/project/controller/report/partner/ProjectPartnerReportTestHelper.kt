package io.cloudflight.jems.server.project.controller.report.partner

import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.mockk.every
import io.mockk.mockk
import org.springframework.web.multipart.MultipartFile
import java.time.ZonedDateTime

private val UPLOADED = ZonedDateTime.now().minusWeeks(1)

private val stream = ByteArray(5).inputStream()

val dummyFile = JemsFileMetadata(id = 90L, "file_name.ext", uploaded = UPLOADED)

val dummyFileDto = JemsFileMetadataDTO(id = 90L, "file_name.ext", uploaded = UPLOADED)

val dummyFileExpected = ProjectFile(stream, "file_name.ext", 50L)

fun dummyMultipartFile(name: String = "file_name.ext", originalName: String? = null): MultipartFile {
    val file = mockk<MultipartFile>()
    every { file.inputStream } returns stream
    every { file.originalFilename } returns originalName
    every { file.name } returns name
    every { file.size } returns 50L
    return file
}
