package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileTypeDTO
import io.cloudflight.jems.api.common.dto.file.UserSimpleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.payments.controller.applicationToEc.export.PaymentAuditAttachmentController
import io.cloudflight.jems.server.payments.service.audit.export.attachment.deletePaymentAuditAttachment.DeletePaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.audit.export.attachment.downloadPaymentAuditAttachment.DownloadPaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.audit.export.attachment.getPaymentAuditAttchament.GetPaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.audit.export.attachment.setDescriptionToPaymentAuditAttachment.SetDescriptionToPaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.audit.export.attachment.uploadPaymentAuditAttachment.UploadPaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.utils.FILE_NAME
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.time.ZonedDateTime

class PaymentAuditAttachmentControllerTest : UnitTest() {

    companion object {
        private const val FILE_ID = 5L
        private val YESTERDAY = ZonedDateTime.now().minusDays(1)


        private val file = JemsFile(
            id = FILE_ID,
            name = "audit-attachment.pdf",
            type = JemsFileType.PaymentAuditAttachment,
            uploaded = YESTERDAY,
            author = UserSimple(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
            size = 47889L,
            description = "desc",
            indexedPath = ""
        )

        private val jemsFileDTO = JemsFileDTO(
            id = FILE_ID,
            name = "audit-attachment.pdf",
            type = JemsFileTypeDTO.PaymentAuditAttachment,
            uploaded = YESTERDAY,
            author = UserSimpleDTO(45L, email = "admin@cloudflight.io", name = "Admin", surname = "Big"),
            size = 47889L,
            sizeString = "46.8\u0020kB",
            description = "desc"
        )

        private val fileMetadata = JemsFileMetadata(
            id = 904L,
            name = FILE_NAME,
            uploaded = YESTERDAY,
        )

        private val expectedMetadata = JemsFileMetadataDTO(
            id = 904L,
            name = FILE_NAME,
            uploaded = YESTERDAY,
        )
    }

    @MockK
    lateinit var downloadPaymentAuditAttachment: DownloadPaymentAuditAttachmentInteractor

    @MockK
    lateinit var deletePaymentAuditAttachment: DeletePaymentAuditAttachmentInteractor

    @MockK
    lateinit var setDescriptionToPaymentAuditAttachment: SetDescriptionToPaymentAuditAttachmentInteractor

    @MockK
    lateinit var uploadPaymentAuditAttachment: UploadPaymentAuditAttachmentInteractor

    @MockK
    lateinit var getPaymentAuditAttachment: GetPaymentAuditAttachmentInteractor

    @InjectMockKs
    lateinit var controller: PaymentAuditAttachmentController

    @Test
    fun listPaymentAuditAttachments() {
        every { getPaymentAuditAttachment.list(Pageable.unpaged()) } returns PageImpl(listOf(file))

        assertThat(controller.listPaymentAuditAttachments(Pageable.unpaged()).content).isEqualTo(listOf(jemsFileDTO))
    }

    @Test
    fun downloadAttachment() {
        val fileContentArray = ByteArray(5)
        every { downloadPaymentAuditAttachment.download(fileId = 350L) } returns Pair("fileName.txt", fileContentArray)

        assertThat(controller.downloadAttachment(350L))
            .isEqualTo(
                ResponseEntity.ok()
                    .contentLength(5)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"fileName.txt\"")
                    .body(ByteArrayResource(fileContentArray))
            )
    }

    @Test
    fun deleteAttachment() {
        every { deletePaymentAuditAttachment.delete(358L) } answers { }
        controller.deleteAttachment(358L)
        verify(exactly = 1) { deletePaymentAuditAttachment.delete(358L) }
    }

    @Test
    fun updateAttachmentDescription() {
        every { setDescriptionToPaymentAuditAttachment.setDescription(361L, description = "new desc") } answers { }
        controller.updateAttachmentDescription(361L, "new desc")
        verify(exactly = 1) { setDescriptionToPaymentAuditAttachment.setDescription(361L, description = "new desc") }
    }

    @Test
    fun uploadAttachmentToPaymentAudit() {
        val projectFileSlot = slot<ProjectFile>()
        every {
            uploadPaymentAuditAttachment.upload(capture(projectFileSlot))
        } returns fileMetadata

        assertThat(controller.uploadAttachmentToPaymentAudit(io.cloudflight.jems.server.utils.file))
            .isEqualTo(expectedMetadata)
        assertThat(projectFileSlot.captured.name).isEqualTo(FILE_NAME)
        assertThat(projectFileSlot.captured.size).isEqualTo(100)
    }
}
