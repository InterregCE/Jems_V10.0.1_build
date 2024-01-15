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
import io.cloudflight.jems.server.payments.service.regular.attachment.deletePaymentAttachment.DeletePaymentAttachmentInteractor
import io.cloudflight.jems.server.payments.service.regular.attachment.downloadPaymentAttachment.DownloadPaymentAttachmentInteractor
import io.cloudflight.jems.server.payments.service.regular.attachment.getPaymentAttchament.GetPaymentAttachmentInteractor
import io.cloudflight.jems.server.payments.service.regular.attachment.setDescriptionToPaymentAttachment.SetDescriptionToPaymentAttachmentInteractor
import io.cloudflight.jems.server.payments.service.regular.attachment.uploadPaymentAttachment.UploadPaymentAttachmentInteractor
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

class PaymentAttachmentControllerTest : UnitTest() {

    companion object {
        private val YEARS_AGO_10 = ZonedDateTime.now().minusYears(10)

        private fun dummyAttachment(id: Long) = JemsFile(
            id = id,
            name = "name $id",
            type = JemsFileType.PaymentAttachment,
            uploaded = YEARS_AGO_10,
            author = UserSimple(45L, "dummy@email", name = "Dummy", surname = "Surname"),
            size = 653225L,
            description = "desc $id",
            indexedPath = ""
        )

        private fun expectedAttachment(id: Long) = JemsFileDTO(
            id = id,
            name = "name $id",
            type = JemsFileTypeDTO.PaymentAttachment,
            uploaded = YEARS_AGO_10,
            author = UserSimpleDTO(45L, "dummy@email", name = "Dummy", surname = "Surname"),
            size = 653225L,
            sizeString = "637.9 kB",
            description = "desc $id",
        )

        private val fileMetadata = JemsFileMetadata(
            id = 904L,
            name = FILE_NAME,
            uploaded = YEARS_AGO_10,
        )

        private val expectedMetadata = JemsFileMetadataDTO(
            id = 904L,
            name = FILE_NAME,
            uploaded = YEARS_AGO_10,
        )
    }

    @MockK
    lateinit var downloadPaymentAttachment: DownloadPaymentAttachmentInteractor
    @MockK
    lateinit var deletePaymentAttachment: DeletePaymentAttachmentInteractor
    @MockK
    lateinit var setDescriptionToPaymentAttachment: SetDescriptionToPaymentAttachmentInteractor
    @MockK
    lateinit var uploadPaymentAttachment: UploadPaymentAttachmentInteractor
    @MockK
    lateinit var getPaymentAttachment: GetPaymentAttachmentInteractor

    @InjectMockKs
    private lateinit var controller: PaymentAttachmentController

    @BeforeEach
    fun reset() {
        clearMocks(downloadPaymentAttachment, deletePaymentAttachment, setDescriptionToPaymentAttachment,
            uploadPaymentAttachment, getPaymentAttachment)
    }

    @Test
    fun listPaymentAttachments() {
        val paymentId = 85L
        every { getPaymentAttachment.list(paymentId, any()) } returns PageImpl(listOf(dummyAttachment(45L)))

        assertThat(controller.listPaymentAttachments(paymentId, Pageable.unpaged()).content)
            .containsExactly(expectedAttachment(45L))
    }

    @Test
    fun downloadAttachment() {
        val fileContentArray = ByteArray(5)
        every { downloadPaymentAttachment.download(fileId = 350L) } returns Pair("fileName.txt", fileContentArray)

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
        every { deletePaymentAttachment.delete(358L) } answers { }
        controller.deleteAttachment(358L)
        verify(exactly = 1) { deletePaymentAttachment.delete(358L) }
    }

    @Test
    fun updateAttachmentDescription() {
        every { setDescriptionToPaymentAttachment.setDescription(361L, description = "new desc") } answers { }
        controller.updateAttachmentDescription(361L, "new desc")
        verify(exactly = 1) { setDescriptionToPaymentAttachment.setDescription(361L, description = "new desc") }
    }

    @Test
    fun uploadAttachmentToPayment() {
        val paymentId = 92L
        val projectFileSlot = slot<ProjectFile>()
        every {
            uploadPaymentAttachment.upload(paymentId, capture(projectFileSlot))
        } returns fileMetadata

        assertThat(controller.uploadAttachmentToPayment(paymentId, file))
            .isEqualTo(expectedMetadata)
        assertThat(projectFileSlot.captured.name).isEqualTo(FILE_NAME)
        assertThat(projectFileSlot.captured.size).isEqualTo(100)
    }
}
