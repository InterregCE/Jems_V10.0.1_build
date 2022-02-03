package io.cloudflight.jems.server.programme.repository

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeDataEntity
import io.cloudflight.jems.server.programme.service.model.ProgrammeData
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.util.Optional

class ProgrammeDataPersistenceProviderTest : UnitTest() {

    companion object {
        private const val programmeDataId = 1L

        private val programmeDataEntity = ProgrammeDataEntity(
            id = 1,
            cci = "cci",
            title = "title",
            version = "version",
            firstYear = 2010,
            lastYear = 2030,
            eligibleFrom = LocalDate.of(2020, 1, 1),
            eligibleUntil = LocalDate.of(2030, 1, 1),
            commissionDecisionNumber = "",
            commissionDecisionDate = LocalDate.of(2020, 1, 1),
            programmeAmendingDecisionNumber = "",
            programmeAmendingDecisionDate = LocalDate.of(2020, 1, 1),
            projectIdProgrammeAbbreviation = "NL-DE_",
            projectIdUseCallId = true,
            defaultUserRoleId = 1L
        )

        private val programmeData = ProgrammeData(
            id = 1,
            cci = "cci",
            title = "title",
            version = "version",
            firstYear = 2010,
            lastYear = 2030,
            eligibleFrom = LocalDate.of(2020, 1, 1),
            eligibleUntil = LocalDate.of(2030, 1, 1),
            commissionDecisionNumber = "",
            commissionDecisionDate = LocalDate.of(2020, 1, 1),
            programmeAmendingDecisionNumber = "",
            programmeAmendingDecisionDate = LocalDate.of(2020, 1, 1),
            projectIdProgrammeAbbreviation = "NL-DE_",
            projectIdUseCallId = true,
            defaultUserRoleId = 1L
        )
    }

    @MockK
    lateinit var repository: ProgrammeDataRepository

    @InjectMockKs
    lateinit var persistence: ProgrammeDataPersistenceProvider

    @Test
    fun `should return programme data when there is no problem`(){
        every { repository.findById(programmeDataId) } returns Optional.of(programmeDataEntity)
        assertThat(persistence.getProgrammeData()).isEqualTo(programmeData)
    }
    @Test
    fun getDefaultUserRole() {
        every { repository.findById(programmeDataId) } returns Optional.of(programmeDataEntity)
        assertThat(persistence.getDefaultUserRole()).isEqualTo(1L)
    }

    @Test
    fun getDefaultUserRoleNotFoundException() {
        every { repository.findById(programmeDataId) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> {  persistence.getDefaultUserRole() }
    }

    @Test
    fun updateDefaultUserRole() {
        every { repository.findById(programmeDataId) } returns Optional.of(programmeDataEntity)
        val dataToSave = slot<ProgrammeDataEntity>()
        every { repository.save(capture(dataToSave)) } returnsArgument 0

        persistence.updateDefaultUserRole(2L)
        assertThat(dataToSave.captured).isEqualTo(
            programmeDataEntity.copy(defaultUserRoleId = 2L)
        )
    }
}
