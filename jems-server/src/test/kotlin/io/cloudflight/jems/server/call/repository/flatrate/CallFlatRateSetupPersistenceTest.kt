package io.cloudflight.jems.server.call.repository.flatrate

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole
import io.cloudflight.jems.api.project.dto.status.ProjectApplicationStatus
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.callWithId
import io.cloudflight.jems.server.call.entity.CallEntity
import io.cloudflight.jems.server.call.entity.ProjectCallFlatRateEntity
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.service.flatrate.CallFlatRateSetupPersistence
import io.cloudflight.jems.server.call.service.flatrate.model.ProjectCallFlatRate
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
import io.cloudflight.jems.server.programme.entity.ProgrammeLegalStatus
import io.cloudflight.jems.server.project.entity.Project
import io.cloudflight.jems.server.project.entity.ProjectStatus
import io.cloudflight.jems.server.project.entity.partner.ProjectPartnerEntity
import io.cloudflight.jems.server.project.repository.partner.ProjectPartnerRepository
import io.cloudflight.jems.server.project.service.partner.ProjectPartnerTestUtil.Companion.user
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional

class CallFlatRateSetupPersistenceTest {

    companion object {
        private fun callWithIdAndFlatRate(id: Long, flatRate: Set<ProjectCallFlatRateEntity>) = callWithId(id).copy(
            flatRates = flatRate.toMutableSet()
        )
        private fun dummyPartner(call: CallEntity) = ProjectPartnerEntity(
            project = Project(
                id = 1,
                call = call,
                acronym = "test",
                applicant = user,
                projectStatus = ProjectStatus(status = ProjectApplicationStatus.DRAFT, user = user)
            ),
            abbreviation = "test",
            role = ProjectPartnerRole.PARTNER,
            legalStatus = ProgrammeLegalStatus()
        )
    }

    @MockK
    lateinit var callRepository: CallRepository

    @MockK
    lateinit var projectPartnerRepository: ProjectPartnerRepository

    @RelaxedMockK
    lateinit var auditService: AuditService

    private lateinit var callFlatRateSetupPersistence: CallFlatRateSetupPersistence

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        callFlatRateSetupPersistence = CallFlatRateSetupPersistenceProvider(
            callRepository,
            projectPartnerRepository
        )
    }

    @Test
    fun `updateFlatRateSetup not-existing`() {
        every { callRepository.findById(eq(-1)) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { callFlatRateSetupPersistence.updateProjectCallFlatRate(-1, emptySet()) }
        assertThat(ex.entity).isEqualTo("call")
    }

    @Test
    fun updateFlatRateSetup() {
        val flatRateToUpdate = ProjectCallFlatRateEntity(
            setupId = FlatRateSetupId(callId = 1, type = FlatRateType.OtherOnStaff),
            rate = 10,
            isAdjustable = true
        )
        val flatRateToDelete = ProjectCallFlatRateEntity(
            setupId = FlatRateSetupId(callId = 1, type = FlatRateType.TravelOnStaff),
            rate = 2,
            isAdjustable = false
        )
        val modelToUpdate = ProjectCallFlatRate(
            type = flatRateToUpdate.setupId.type, // existing
            rate = 5, // changed
            isAdjustable = false // changed
        )
        val modelToCreate = ProjectCallFlatRate(
            type = FlatRateType.OfficeOnOther, // new
            rate = 5,
            isAdjustable = true
        )
        val call = callWithIdAndFlatRate(1, setOf(flatRateToUpdate, flatRateToDelete))
        every { callRepository.findById(eq(1)) } returns Optional.of(call)
        callFlatRateSetupPersistence.updateProjectCallFlatRate(1, setOf(modelToCreate, modelToUpdate))
        assertThat(call.flatRates).containsExactly(
            ProjectCallFlatRateEntity(
                setupId = flatRateToUpdate.setupId,
                rate = modelToUpdate.rate,
                isAdjustable = modelToUpdate.isAdjustable
            ),
            ProjectCallFlatRateEntity(
                setupId = FlatRateSetupId(callId = 1, type = modelToCreate.type),
                rate = modelToCreate.rate,
                isAdjustable = modelToCreate.isAdjustable
            )
        )
    }

    @Test
    fun getFlatRateSetup() {
        val flatRate = setOf(ProjectCallFlatRateEntity(
            setupId = FlatRateSetupId(callId = 1, type = FlatRateType.OtherOnStaff),
            rate = 10,
            isAdjustable = true
        ))
        every { callRepository.findById(eq(1)) } returns Optional.of(callWithIdAndFlatRate(1, flatRate))
        assertThat(callFlatRateSetupPersistence.getProjectCallFlatRate(1)).isEqualTo(
            setOf(ProjectCallFlatRate(
                type = FlatRateType.OtherOnStaff,
                rate = 10,
                isAdjustable = true
            ))
        )
    }

    @Test
    fun `getFlatRateSetup empty`() {
        every { callRepository.findById(eq(1)) } returns Optional.of(callWithIdAndFlatRate(1, emptySet()))
        assertThat(callFlatRateSetupPersistence.getProjectCallFlatRate(1)).isEmpty()
    }

    @Test
    fun `getFlatRateSetup not-existing`() {
        every { callRepository.findById(eq(-1)) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { callFlatRateSetupPersistence.getProjectCallFlatRate(-1) }
        assertThat(ex.entity).isEqualTo("call")
    }

    @Test
    fun getProjectCallFlatRate() {
        val flatRate = setOf(ProjectCallFlatRateEntity(
            setupId = FlatRateSetupId(callId = 10, type = FlatRateType.OtherOnStaff),
            rate = 15,
            isAdjustable = true
        ))
        every { projectPartnerRepository.findById(eq(1)) } returns Optional.of(
            dummyPartner(callWithIdAndFlatRate(10, flatRate))
        )

        assertThat(callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(1)).containsExactly(
            ProjectCallFlatRate(
                type = FlatRateType.OtherOnStaff,
                rate = 15,
                isAdjustable = true
            )
        )
    }

    @Test
    fun `getProjectCallFlatRate not-existing`() {
        every { projectPartnerRepository.findById(eq(-1)) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { callFlatRateSetupPersistence.getProjectCallFlatRateByPartnerId(-1) }
        assertThat(ex.entity).isEqualTo("projectPartner")
    }

}
