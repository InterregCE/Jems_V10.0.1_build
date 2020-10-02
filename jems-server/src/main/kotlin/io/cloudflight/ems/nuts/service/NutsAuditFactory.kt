package io.cloudflight.ems.nuts.service

import io.cloudflight.ems.api.nuts.dto.OutputNutsMetadata
import io.cloudflight.ems.audit.entity.AuditAction
import io.cloudflight.ems.audit.service.AuditBuilder
import io.cloudflight.ems.audit.service.AuditCandidate

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
