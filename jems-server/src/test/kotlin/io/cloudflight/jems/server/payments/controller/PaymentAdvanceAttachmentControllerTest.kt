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
import io.cloudflight.jems.server.payments.service.advance.attachment.deletePaymentAdvanceAttachment.DeletePaymentAdvAttachmentInteractor
import io.cloudflight.jems.server.payments.service.advance.attachment.downloadPaymentAdvanceAttachment.DownloadPaymentAdvAttachmentInteractor
import io.cloudflight.jems.server.payments.service.advance.attachment.getPaymentAdvanceAttachment.GetPaymentAdvAttachmentInteractor
import io.cloudflight.jems.server.payments.service.advance.attachment.setDescriptionToPaymentAdvAttachment.SetDescriptionToPaymentAdvAttachmentInteractor
import io.cloudflight.jems.server.payments.service.advance.attachment.uploadPaymentAdvanceAttachment.UploadPaymentAdvAttachmentInteractor
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.utils.FILE_NAME
import io.cloudflight.jems.server.utils.file
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.time.ZonedDateTime

class PaymentAdvanceAttachmentControllerTest : UnitTest() {

    companion object {
        private val UPLOADED_DATE = ZonedDateTime.now().minusMonths(1)

        private fun dummyAttachment(id: Long) = JemsFile(
            id = id,
            name = "name $id",
            type = JemsFileType.PaymentAttachment,
            uploaded = UPLOADED_DATE,
            author = UserSimple(9L, "dummy@email", name = "Dummy", surname = "Surname"),
            size = 653225L,
            description = "desc $id",
            indexedPath = ""
        )

        private fun expectedAttachment(id: Long) = JemsFileDTO(
            id = id,
            name = "name $id",
            type = JemsFileTypeDTO.PaymentAttachment,
            uploaded = UPLOADED_DATE,
            author = UserSimpleDTO(9L, "dummy@email", name = "Dummy", surname = "Surname"),
            size = 653225L,
            sizeString = "637.9 kB",
            description = "desc $id",
        )

        private val fileMetadata = JemsFileMetadata(
            id = 904L,
            name = FILE_NAME,
            uploaded = UPLOADED_DATE,
        )

        private val expectedMetadata = JemsFileMetadataDTO(
            id = 904L,
            name = FILE_NAME,
            uploaded = UPLOADED_DATE,
        )
    }

    @MockK
    lateinit var downloadPaymentAdvanceAttachment: DownloadPaymentAdvAttachmentInteractor
    @MockK
    lateinit var deletePaymentAdvanceAttachment: DeletePaymentAdvAttachmentInteractor
    @MockK
    lateinit var setDescriptionToPaymentAdvanceAttachment: SetDescriptionToPaymentAdvAttachmentInteractor
    @MockK
    lateinit var uploadPaymentAdvanceAttachment: UploadPaymentAdvAttachmentInteractor
    @MockK
    lateinit var getPaymentAdvanceAttachment: GetPaymentAdvAttachmentInteractor

    @InjectMockKs
    private lateinit var controller: PaymentAdvanceAttachmentController

    @BeforeEach
    fun reset() {
        clearMocks(
            downloadPaymentAdvanceAttachment, deletePaymentAdvanceAttachment, setDescriptionToPaymentAdvanceAttachment,
            uploadPaymentAdvanceAttachment, getPaymentAdvanceAttachment
        )
    }

    @Test
    fun listPaymentAttachments() {
        val paymentId = 17L
        every { getPaymentAdvanceAttachment.list(paymentId, any()) } returns PageImpl(listOf(dummyAttachment(17L)))

        assertThat(controller.listPaymentAttachments(paymentId, Pageable.unpaged()).content)
            .containsExactly(expectedAttachment(17L))
    }

    @Test
    fun downloadAttachment() {
        val fileContentArray = ByteArray(5)
        every { downloadPaymentAdvanceAttachment.download(fileId = 350L) } returns Pair("fileName.txt", fileContentArray)

        assertThat(controller.downloadAttachment(350L))
            .isEqualTo(
                ResponseEntity.ok()
                    .contentLength(5)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''fileName.txt")
                    .body(ByteArrayResource(fileContentArray))
            )
    }

    @Test
    fun deleteAttachment() {
        every { deletePaymentAdvanceAttachment.delete(358L) } answers { }
        controller.deleteAttachment(358L)
        verify(exactly = 1) { deletePaymentAdvanceAttachment.delete(358L) }
    }

    @Test
    fun updateAttachmentDescription() {
        every { setDescriptionToPaymentAdvanceAttachment.setDescription(361L, description = "new desc") } answers { }
        controller.updateAttachmentDescription(361L, "new desc")
        verify(exactly = 1) { setDescriptionToPaymentAdvanceAttachment.setDescription(361L, description = "new desc") }
    }

    @Test
    fun uploadAttachmentToPayment() {
        val paymentId = 92L
        val projectFileSlot = slot<ProjectFile>()
        every {
            uploadPaymentAdvanceAttachment.upload(paymentId, capture(projectFileSlot))
        } returns fileMetadata

        assertThat(controller.uploadAttachmentToPayment(paymentId, file))
            .isEqualTo(expectedMetadata)
        assertThat(projectFileSlot.captured.name).isEqualTo(FILE_NAME)
        assertThat(projectFileSlot.captured.size).isEqualTo(100)

    }
}
