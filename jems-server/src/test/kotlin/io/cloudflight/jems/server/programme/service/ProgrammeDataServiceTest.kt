package io.cloudflight.jems.server.programme.service

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.api.nuts.dto.OutputNuts
import io.cloudflight.jems.api.programme.dto.InputProgrammeData
import io.cloudflight.jems.api.programme.dto.OutputProgrammeData
import io.cloudflight.jems.server.audit.entity.AuditAction
import io.cloudflight.jems.server.audit.service.AuditCandidate
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.repository.CallRepository
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.nuts.entity.NutsCountry
import io.cloudflight.jems.server.nuts.entity.NutsRegion1
import io.cloudflight.jems.server.nuts.entity.NutsRegion2
import io.cloudflight.jems.server.nuts.entity.NutsRegion3
import io.cloudflight.jems.server.nuts.repository.NutsRegion3Repository
import io.cloudflight.jems.server.programme.entity.ProgrammeData
import io.cloudflight.jems.server.programme.repository.ProgrammeDataRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.util.Optional

/**
 * tests ProgrammeDataService methods including ProgrammeDataMapper.
 */
internal class ProgrammeDataServiceTest {

    private val existingProgrammeData =
        ProgrammeData(
            1,
            "cci",
            "title",
            "version",
            2020,
            2024,
            null, null,
            null,
            null,
            null,
            null
        )

    @MockK
    lateinit var programmeDataRepository: ProgrammeDataRepository
    @MockK
    lateinit var nutsRegion3Repository: NutsRegion3Repository
    @MockK
    lateinit var callRepository: CallRepository

    @RelaxedMockK
    lateinit var auditService: AuditService
    lateinit var programmeDataService: ProgrammeDataService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        programmeDataService = ProgrammeDataServiceImpl(
            programmeDataRepository,
            callRepository,
            nutsRegion3Repository,
            auditService
        )
        every { callRepository.existsByStatus(CallStatus.PUBLISHED) } returns false
    }

    @Test
    fun get() {
        val programmeDataInput =
            OutputProgrammeData("cci", "title", "version", 2020, 2024, null, null, null, null, null, null, emptyList())
        every { programmeDataRepository.findById(1) } returns Optional.of(existingProgrammeData)

        val programmeData = programmeDataService.get()

        assertThat(programmeData).isEqualTo(programmeDataInput)
    }

    @Test
    fun `update existing programme data`() {
        val programmeDataInput =
            InputProgrammeData("cci-updated", "title", "version", 2020, 2024, null, null, null, null, null, null)
        val programmeDataUpdated =
            ProgrammeData(1, "cci-updated", "title", "version", 2020, 2024, null, null, null, null, null, null)
        val programmeDataExpectedOutput =
            OutputProgrammeData("cci-updated", "title", "version", 2020, 2024, null, null, null, null, null, null, emptyList())

        every { programmeDataRepository.save(any<ProgrammeData>()) } returns programmeDataUpdated
        every { programmeDataRepository.findById(1) } returns Optional.of(existingProgrammeData)

        val programmeData = programmeDataService.update(programmeDataInput)

        assertThat(programmeData).isEqualTo(programmeDataExpectedOutput)

        val event = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(event)) }
        with(event) {
            assertThat(captured.action).isEqualTo(AuditAction.PROGRAMME_BASIC_DATA_EDITED)
            assertThat(captured.description).isEqualTo("Programme basic data changed:\n" +
                "cci changed from cci to cci-updated")
        }
    }

    @Test
    fun `update existing programme data with different data`() {
        val programmeDataInput =
            InputProgrammeData("cci-updated", "title-updated", "version-updated", 2021, 2025,
                LocalDate.of(2020, 1, 1), LocalDate.of(2021, 2, 2),
                "d1",  LocalDate.of(2022, 3, 3),
                "d2", LocalDate.of(2022, 4, 4))
        val programmeDataUpdated = programmeDataInput.toEntity(emptySet())
        val programmeDataExpectedOutput = programmeDataUpdated.toOutputProgrammeData()

        every { programmeDataRepository.save(any<ProgrammeData>()) } returns programmeDataUpdated
        every { programmeDataRepository.findById(1) } returns Optional.of(existingProgrammeData)

        val programmeData = programmeDataService.update(programmeDataInput)

        assertThat(programmeData).isEqualTo(programmeDataExpectedOutput)

        val event = slot<AuditCandidate>()
        verify { auditService.logEvent(capture(event)) }
        with(event) {
            assertThat(captured.action).isEqualTo(AuditAction.PROGRAMME_BASIC_DATA_EDITED)
            assertThat(captured.description).isEqualTo("Programme basic data changed:\n" +
                "cci changed from cci to cci-updated,\n" +
                "title changed from title to title-updated,\n" +
                "version changed from version to version-updated,\n" +
                "firstYear changed from 2020 to 2021,\n" +
                "lastYear changed from 2024 to 2025,\n" +
                "eligibleFrom changed from null to 2020-01-01,\n" +
                "eligibleUntil changed from null to 2021-02-02,\n" +
                "commissionDecisionNumber changed from null to d1,\n" +
                "commissionDecisionDate changed from null to 2022-03-03,\n" +
                "programmeAmendingDecisionNumber changed from null to d2,\n" +
                "programmeAmendingDecisionDate changed from null to 2022-04-04")
        }
    }

    @Test
    fun `update nuts - missing programme data hardcoded row ID=1`() {
        every { programmeDataRepository.findById(eq(1)) } returns Optional.empty()
        assertThrows<ResourceNotFoundException> { programmeDataService.saveProgrammeNuts(emptySet()) }
    }

    @Test
    fun `update nuts`() {
        val regionToBeSaved = NutsRegion3(
            id = "CO011",
            title = "CO011 title",
            region2 = NutsRegion2(
                id = "CO01",
                title = "CO01 title",
                region1 = NutsRegion1(id = "CO0", title = "CO0 title", country = NutsCountry(id = "CO", title = "CO title")))
        )

        every { programmeDataRepository.findById(eq(1)) } returns Optional.of(existingProgrammeData)
        every { nutsRegion3Repository.findAllById(eq(setOf("nuts_3_id"))) } returns setOf(regionToBeSaved)
        every { programmeDataRepository.save(any<ProgrammeData>()) } returnsArgument 0

        val result = programmeDataService.saveProgrammeNuts(setOf("nuts_3_id"))
        assertThat(result.programmeNuts).isEqualTo(
            listOf(OutputNuts("CO", "CO title", listOf(
                OutputNuts("CO0", "CO0 title", listOf(
                    OutputNuts("CO01", "CO01 title", listOf(
                        OutputNuts("CO011", "CO011 title")
                    ))
                ))
            )))
        )
    }

    @Test
    fun getNuts() {
        val nuts = NutsRegion3(id = "SK010", title = "Slovakia R3",
            region2 = NutsRegion2(id = "SK01", title = "Slovakia R2",
                region1 = NutsRegion1(id = "SK0", title = "Slovakia R1",
                    country = NutsCountry(id = "SK", title = "Slovakia")
                )
            )
        )
        every { programmeDataRepository.findById(1) } returns Optional.of(existingProgrammeData.copy(programmeNuts = setOf(nuts)))

        assertThat(programmeDataService.getAvailableNuts()).containsExactly(
            OutputNuts(code = "SK", title = "Slovakia", areas = listOf(
                OutputNuts(code = "SK0", title = "Slovakia R1", areas = listOf(
                    OutputNuts(code = "SK01", title = "Slovakia R2", areas = listOf(
                        OutputNuts(code = "SK010", title = "Slovakia R3")
                    ))
                ))
            ))
        )
    }

    @Test
    fun `update programme areas - failed on call already published`() {
        every { callRepository.existsByStatus(CallStatus.PUBLISHED) } returns true
        val nuts = NutsRegion3(id = "SK010", title = "Slovakia R3",
            region2 = NutsRegion2(id = "SK01", title = "Slovakia R2",
                region1 = NutsRegion1(id = "SK0", title = "Slovakia R1",
                    country = NutsCountry(id = "SK", title = "Slovakia")
                )
            )
        )
        every { programmeDataRepository.findById(1) } returns Optional.of(existingProgrammeData.copy(programmeNuts = setOf(nuts)))

        assertThrows<UpdateProgrammeAreasWhenProgrammeSetupRestricted> { programmeDataService.saveProgrammeNuts(emptySet()) }
    }


}
