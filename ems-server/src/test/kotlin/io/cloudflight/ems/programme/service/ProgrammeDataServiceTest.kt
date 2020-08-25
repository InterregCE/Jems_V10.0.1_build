package io.cloudflight.ems.programme.service

import io.cloudflight.ems.api.dto.user.OutputUserRole
import io.cloudflight.ems.api.dto.user.OutputUserWithRole
import io.cloudflight.ems.api.programme.dto.InputProgrammeData
import io.cloudflight.ems.api.programme.dto.OutputProgrammeData
import io.cloudflight.ems.entity.Audit
import io.cloudflight.ems.entity.AuditAction
import io.cloudflight.ems.entity.ProgrammeData
import io.cloudflight.ems.exception.ResourceNotFoundException
import io.cloudflight.ems.nuts.entity.NutsCountry
import io.cloudflight.ems.nuts.entity.NutsRegion1
import io.cloudflight.ems.nuts.entity.NutsRegion2
import io.cloudflight.ems.nuts.entity.NutsRegion3
import io.cloudflight.ems.nuts.repository.NutsRegion3Repository
import io.cloudflight.ems.nuts.service.NutsIdentifier
import io.cloudflight.ems.repository.ProgrammeDataRepository
import io.cloudflight.ems.security.model.LocalCurrentUser
import io.cloudflight.ems.security.service.SecurityService
import io.cloudflight.ems.service.AuditService
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
import java.util.Optional

/**
 * tests ProgrammeDataService methods including ProgrammeDataMapper.
 */
internal class ProgrammeDataServiceTest {

    private val user = OutputUserWithRole(
        id = 1,
        email = "admin@admin.dev",
        name = "Name",
        surname = "Surname",
        userRole = OutputUserRole(id = 1, name = "ADMIN")
    )

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
    lateinit var securityService: SecurityService

    @RelaxedMockK
    lateinit var auditService: AuditService
    lateinit var programmeDataService: ProgrammeDataService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        every { securityService.currentUser } returns LocalCurrentUser(user, "hash_pass", emptyList())
        programmeDataService = ProgrammeDataServiceImpl(
            programmeDataRepository,
            nutsRegion3Repository,
            auditService,
            securityService
        )
    }

    @Test
    fun get() {
        val programmeDataInput =
            OutputProgrammeData("cci", "title", "version", 2020, 2024, null, null, null, null, null, null, emptyMap<String, String>())
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
            OutputProgrammeData("cci-updated", "title", "version", 2020, 2024, null, null, null, null, null, null, emptyMap<String, String>())

        every { programmeDataRepository.save(any<ProgrammeData>()) } returns programmeDataUpdated
        every { programmeDataRepository.findById(1) } returns Optional.of(existingProgrammeData)

        val programmeData = programmeDataService.update(programmeDataInput)

        assertThat(programmeData).isEqualTo(programmeDataExpectedOutput)

        val event = slot<Audit>()
        verify { auditService.logEvent(capture(event)) }
        with(event) {
            assertThat(AuditAction.PROGRAMME_BASIC_DATA_EDITED).isEqualTo(captured.action)
            assertThat("Programme basic data changed:\n" +
                "cci changed from cci to cci-updated").isEqualTo(captured.description)
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
            mapOf(NutsIdentifier("CO", "CO title") to
                mapOf(NutsIdentifier("CO0", "CO0 title") to
                    mapOf(
                        NutsIdentifier("CO01", "CO01 title") to
                            setOf(NutsIdentifier("CO011", "CO011 title"))
                        )
                )
            )
        )
    }


}
