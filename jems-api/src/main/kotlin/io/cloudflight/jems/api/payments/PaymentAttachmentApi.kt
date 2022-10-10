package io.cloudflight.jems.api.payments

import io.cloudflight.jems.api.payments.PaymentsApi.Companion.ENDPOINT_API_PAYMENTS
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileDTO
import io.cloudflight.jems.api.project.dto.report.file.ProjectReportFileMetadataDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import org.springframework.core.io.ByteArrayResource
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@Api("Payment Attachment")
interface PaymentAttachmentApi {

    companion object {
        private const val ENDPOINT_PAYMENT_ATTACHMENT = "$ENDPOINT_API_PAYMENTS/attachment"
    }

    @ApiOperation("List attachments")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("$ENDPOINT_PAYMENT_ATTACHMENT/byPaymentId/{paymentId}")
    fun listPaymentAttachments(@PathVariable paymentId: Long, pageable: Pageable): Page<ProjectReportFileDTO>

    @ApiOperation("Download payment attachment")
    @GetMapping("$ENDPOINT_PAYMENT_ATTACHMENT/byFileId/{fileId}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun downloadAttachment(@PathVariable fileId: Long): ResponseEntity<ByteArrayResource>

    @ApiOperation("Delete payment attachment")
    @DeleteMapping("$ENDPOINT_PAYMENT_ATTACHMENT/byFileId/{fileId}")
    fun deleteAttachment(@PathVariable fileId: Long)

    @ApiOperation("Update description of payment attachment")
    @PutMapping("$ENDPOINT_PAYMENT_ATTACHMENT/byFileId/{fileId}/description", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateAttachmentDescription(@PathVariable fileId: Long, @RequestBody(required = false) description: String?)

    @ApiOperation("Upload attachment to payment")
    @PostMapping("$ENDPOINT_PAYMENT_ATTACHMENT/byPaymentId/{paymentId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadAttachmentToPayment(@PathVariable paymentId: Long, @RequestPart("file") file: MultipartFile): ProjectReportFileMetadataDTO

}
