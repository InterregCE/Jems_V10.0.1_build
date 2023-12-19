package io.cloudflight.jems.server.payments.controller

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileTypeDTO
import io.cloudflight.jems.api.common.dto.file.UserSimpleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.common.file.service.model.UserSimple
import io.cloudflight.jems.server.payments.controller.applicationToEc.export.PaymentAuditAttachmentController
import io.cloudflight.jems.server.payments.service.audit.export.attachment.deletePaymentAuditAttachment.DeletePaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.audit.export.attachment.downloadPaymentAuditAttachment.DownloadPaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.audit.export.attachment.getPaymentAuditAttchament.GetPaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.audit.export.attachment.setDescriptionToPaymentAuditAttachment.SetDescriptionToPaymentAuditAttachmentInteractor
import io.cloudflight.jems.server.payments.service.audit.export.attachment.uploadPaymentAuditAttachment.UploadPaymentAuditAttachmentInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.ZonedDateTime

class PaymentAuditAttachmentControllerTest: UnitTest() {

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
    fun  listPaymentAuditAttachments() {
        every { getPaymentAuditAttachment.list(Pageable.unpaged()) } returns PageImpl(listOf(file))

        assertThat(controller.listPaymentAuditAttachments(Pageable.unpaged()).content).isEqualTo(listOf(jemsFileDTO))
    } //TODO add rest of the tests
}
