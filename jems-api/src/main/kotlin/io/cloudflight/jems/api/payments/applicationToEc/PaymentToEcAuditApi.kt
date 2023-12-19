package io.cloudflight.jems.api.payments.applicationToEc

import io.cloudflight.jems.api.payments.dto.export.PaymentToEcExportMetadataDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Api("Payment to EC audit")
interface PaymentToEcAuditApi {
    companion object {
        private const val ENDPOINT_API_PAYMENT_TO_EC_AUDIT = "/api/paymentApplicationsToEc/audit"
    }
    @ApiOperation("Trigger payment to ec audit exportation")
    @PostMapping("${ENDPOINT_API_PAYMENT_TO_EC_AUDIT}/export")
    fun export(
        @RequestParam(required = true) pluginKey: String,
        @RequestParam(required = false) programmeFundId: Long?,
        @RequestParam(required = false) accountingYearId: Long?
    )

    @ApiOperation("Get list of payment to ec exported files metadata")
    @GetMapping("${ENDPOINT_API_PAYMENT_TO_EC_AUDIT}/list")
    fun list() : List<PaymentToEcExportMetadataDTO>

    @ApiOperation("Download payment to ec audit export")
    @GetMapping(
        "${ENDPOINT_API_PAYMENT_TO_EC_AUDIT}/download/{fileId}",
        produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE]
    )
    fun download(
        @PathVariable fileId: Long,
        @RequestParam pluginKey: String
    ): ResponseEntity<ByteArrayResource>
}
