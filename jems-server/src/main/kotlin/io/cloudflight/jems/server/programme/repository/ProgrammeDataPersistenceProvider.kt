package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeData
import io.cloudflight.jems.server.programme.service.userrole.ProgrammeDataPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProgrammeDataPersistenceProvider(
    private val programmeDataRepository: ProgrammeDataRepository
) : ProgrammeDataPersistence {

    companion object {
        const val programmeDataId = 1L
    }

    @Transactional(readOnly = true)
    override fun getProgrammeName(): String? =
        getProgrammeDataOrThrow().title

    @Transactional(readOnly = true)
    override fun getDefaultUserRole(): Long? {
        return getProgrammeDataOrThrow().defaultUserRoleId
    }

    @Transactional
    override fun updateDefaultUserRole(userRoleId: Long) {
        val programmeData = getProgrammeDataOrThrow()
        programmeDataRepository.save(programmeData.copy(defaultUserRoleId = userRoleId))
    }

    private fun getProgrammeDataOrThrow(): ProgrammeData =
        programmeDataRepository.findById(programmeDataId).orElseThrow { ResourceNotFoundException("programmeData") }
}
