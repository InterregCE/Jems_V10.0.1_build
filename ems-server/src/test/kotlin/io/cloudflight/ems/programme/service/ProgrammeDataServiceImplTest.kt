package io.cloudflight.ems.programme.service

import io.cloudflight.ems.api.programme.dto.InputProgrammeData
import io.cloudflight.ems.entity.ProgrammeData
import io.cloudflight.ems.repository.ProgrammeDataRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Optional

/**
 * tests ProgrammeDataService methods including ProgrammeDataMapper.
 */
internal class ProgrammeDataServiceImplTest {

    @MockK
    lateinit var programmeDataRepository: ProgrammeDataRepository

    lateinit var programmeDataService: ProgrammeDataService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        programmeDataService = ProgrammeDataServiceImpl(programmeDataRepository)
    }

    @Test
    fun get() {
        val programmeDataInput = InputProgrammeData("cci", "title", "version", 2020, 2024, null, null, null, null, null, null)
        val existingProgrammeData = ProgrammeData(1, "cci", "title", "version", 2020, 2024, null, null, null, null, null, null)
        every { programmeDataRepository.findById(1) } returns Optional.of(existingProgrammeData)

        val programmeData = programmeDataService.get()

        assertThat(programmeData).isEqualTo(programmeDataInput)
    }

    @Test
    fun update() {
        val programmeDataInput = InputProgrammeData("cci", "title", "version", 2020, 2024, null, null, null, null, null, null)
        val programmeDataUpdated = ProgrammeData(1, "cci", "title", "version", 2020, 2024, null, null, null, null, null, null)
        every { programmeDataRepository.save(any<ProgrammeData>()) } returns programmeDataUpdated

        val programmeData = programmeDataService.update(programmeDataInput)

        assertThat(programmeData).isEqualTo(programmeDataInput)
    }
}
