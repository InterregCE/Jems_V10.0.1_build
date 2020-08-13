package io.cloudflight.ems.nuts.service

import io.cloudflight.ems.nuts.entity.ProgrammeNutsRegionSaveEntity
import io.cloudflight.ems.nuts.repository.NutsRegion3Repository
import io.cloudflight.ems.nuts.repository.ProgrammeNutsSaveRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProgrammeNutsServiceImpl(
    val nutsRegion3Repository: NutsRegion3Repository,
    val programmeNutsSaveRepository: ProgrammeNutsSaveRepository
) : ProgrammeNutsService {

    @Transactional(readOnly = true)
    override fun getProgrammeNuts(): Map<NutsIdentifier, Map<NutsIdentifier, Map<NutsIdentifier, Set<NutsIdentifier>>>> {
        return groupNuts(nutsRegion3Repository.findAllProgrammeNutsRegions())
    }

    @Transactional
    override fun saveProgrammeNuts(regions: Collection<String>): Map<NutsIdentifier, Map<NutsIdentifier, Map<NutsIdentifier, Set<NutsIdentifier>>>> {
        programmeNutsSaveRepository.deleteAllInBatch()
        programmeNutsSaveRepository.saveAll(regions.map { ProgrammeNutsRegionSaveEntity(it) })
        programmeNutsSaveRepository.flush()
        return groupNuts(nutsRegion3Repository.findAllProgrammeNutsRegions())
    }

}
