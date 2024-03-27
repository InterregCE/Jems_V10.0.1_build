package io.cloudflight.jems.server.common.file.service

import io.cloudflight.jems.plugin.contract.models.common.file.JemsFileMetadataData
import io.cloudflight.jems.server.common.file.service.model.JemsFile


fun JemsFile.toSimpleModelMedataData() = JemsFileMetadataData(
    id = this.id,
    name = this.name,
    uploaded = this.uploaded
)
