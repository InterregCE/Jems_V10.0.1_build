package io.cloudflight.jems.server.payments.service.audit.attachment.deletePaymentAttachment

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.file.entity.JemsFileMetadataEntity
import io.cloudflight.jems.server.common.file.repository.JemsFileMetadataRepository
import io.cloudflight.jems.server.common.file.repository.JemsFilePersistenceProviderTest
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class DeletePaymentAuditAttachmentTest : UnitTest() {

    companion object {
        val file =  JemsFileMetadataEntity(
            id = 16L,
            projectId = null,
            partnerId = null,
            path = "",
            minioBucket = "Payments",
            minioLocation = "filePathFull",
            name = "name",
            type = JemsFileType.Contract,
            size = 45L,
            user = mockk(),
            uploaded = ZonedDateTime.now(),
            description = "dummy description",
        )
    }

//    @MockK
//    lateinit var jemsSystemFileService: JemsSystemFileService
//
//    @MockK
//    lateinit var projectFileMetadataRepository: JemsFileMetadataRepository
//
//    @InjectMockKs
//    lateinit var interactor: DeletePaymentAttachment
//
//    @BeforeEach
//    fun reset() {
//        clearMocks(jemsSystemFileService)
//    }
//
//    @Test
//    fun delete() {
//        every { jemsSystemFileService.delete(file) } answers { }
//        interactor.delete(15L)
//        verify(exactly = 1) { paymentPersistence.deletePaymentAttachment(15L) }
//    }

}
