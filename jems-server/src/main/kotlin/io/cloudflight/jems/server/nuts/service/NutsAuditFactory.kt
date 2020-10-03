package io.cloudflight.jems.server.nuts.service

import io.cloudflight.jems.api.nuts.dto.OutputNutsMetadata
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditBuilder
import io.cloudflight.jems.server.audit.service.AuditCandidate

fun nutsDownloadRequest(): AuditCandidate {
    return AuditBuilder(AuditAction.NUTS_DATASET_DOWNLOAD)
        .description("There was an attempt to download NUTS regions from GISCO. Download is starting...")
        .build()
}

fun nutsDownloadSuccessful(nutsMetadata: OutputNutsMetadata): AuditCandidate {
    return AuditBuilder(AuditAction.NUTS_DATASET_DOWNLOAD)
        .description("NUTS Dataset '${nutsMetadata.title}' ${nutsMetadata.date} has been downloaded.")
        .build()
}
