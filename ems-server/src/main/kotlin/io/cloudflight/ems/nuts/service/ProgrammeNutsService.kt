package io.cloudflight.ems.nuts.service

interface ProgrammeNutsService {

    fun getProgrammeNuts(): Map<NutsIdentifier, Map<NutsIdentifier, Map<NutsIdentifier, Set<NutsIdentifier>>>>

    fun saveProgrammeNuts(regions: Collection<String>): Map<NutsIdentifier, Map<NutsIdentifier, Map<NutsIdentifier, Set<NutsIdentifier>>>>

}
