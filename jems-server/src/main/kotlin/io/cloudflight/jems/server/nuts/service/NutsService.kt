package io.cloudflight.jems.server.nuts.service

import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.nuts.dto.OutputNutsMetadata

interface NutsService {

    fun getNutsMetadata(): OutputNutsMetadata?

    fun downloadLatestNutsFromGisco(): OutputNutsMetadata

    fun getNuts(): List<OutputNuts>

}
