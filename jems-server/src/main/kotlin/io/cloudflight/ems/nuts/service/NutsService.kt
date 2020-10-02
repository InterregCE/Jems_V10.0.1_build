package io.cloudflight.ems.nuts.service

import io.cloudflight.ems.api.nuts.dto.OutputNutsMetadata

interface NutsService {

    fun getNutsMetadata(): OutputNutsMetadata?

    fun downloadLatestNutsFromGisco(): OutputNutsMetadata

    fun getNuts(): Map<NutsIdentifier, Map<NutsIdentifier, Map<NutsIdentifier, Set<NutsIdentifier>>>>

}
