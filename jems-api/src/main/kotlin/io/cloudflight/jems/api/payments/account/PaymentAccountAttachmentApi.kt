package io.cloudflight.jems.api.payments.account

import io.cloudflight.jems.api.common.dto.file.JemsFileDTO
import io.cloudflight.jems.api.common.dto.file.JemsFileMetadataDTO
import io.cloudflight.jems.api.payments.account.PaymentAccountApi.Companion.ENDPOINT_API_PAYMENT_ACCOUNT
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

@Api("Payment Account Attachment")
interface PaymentAccountAttachmentApi {
    companion object {
        private const val ENDPOINT_PAYMENT_ACCOUNT_ATTACHMENT = "${ENDPOINT_API_PAYMENT_ACCOUNT}/attachment"
    }

    @ApiOperation("List attachments")
    @ApiImplicitParams(
        ApiImplicitParam(paramType = "query", name = "page", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "size", dataType = "integer"),
        ApiImplicitParam(paramType = "query", name = "sort", dataType = "string")
    )
    @GetMapping("$ENDPOINT_PAYMENT_ACCOUNT_ATTACHMENT/byPaymentAccountId/{paymentAccountId}")
    fun listPaymentAccountAttachments(@PathVariable paymentAccountId: Long, pageable: Pageable): Page<JemsFileDTO>

    @ApiOperation("Download payment account attachment")
    @GetMapping(
        "$ENDPOINT_PAYMENT_ACCOUNT_ATTACHMENT/byFileId/{fileId}",
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    fun downloadAttachment(@PathVariable fileId: Long): ResponseEntity<ByteArrayResource>

    @ApiOperation("Delete payment account attachment")
    @DeleteMapping("$ENDPOINT_PAYMENT_ACCOUNT_ATTACHMENT/byFileId/{fileId}")
    fun deleteAttachment(@PathVariable fileId: Long)

    @ApiOperation("Update description of payment account attachment")
    @PutMapping("$ENDPOINT_PAYMENT_ACCOUNT_ATTACHMENT/byFileId/{fileId}/description", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateAttachmentDescription(@PathVariable fileId: Long, @RequestBody(required = false) description: String?)

    @ApiOperation("Upload payment account attachment")
    @PostMapping("$ENDPOINT_PAYMENT_ACCOUNT_ATTACHMENT/byPaymentAccountId/{paymentAccountId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadAttachmentToPaymentAccount(@PathVariable paymentAccountId: Long, @RequestPart("file") file: MultipartFile): JemsFileMetadataDTO

}
