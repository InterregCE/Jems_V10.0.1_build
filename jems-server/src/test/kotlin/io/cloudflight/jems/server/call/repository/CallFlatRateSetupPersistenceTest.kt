package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.api.call.dto.flatrate.FlatRateType
import io.cloudflight.jems.server.audit.service.AuditService
import io.cloudflight.jems.server.call.callWithId
import io.cloudflight.jems.server.call.entity.FlatRateSetup
import io.cloudflight.jems.server.call.entity.FlatRateSetupId
import io.cloudflight.jems.server.call.service.flatrate.CallFlatRateSetupPersistence
import io.cloudflight.jems.server.call.service.flatrate.model.FlatRateModel
import io.cloudflight.jems.server.common.exception.ResourceNotFoundException
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
        private fun callWithIdAndFlatRate(id: Long, flatRate: Set<FlatRateSetup>) = callWithId(id).copy(
            flatRateSetup = flatRate.toMutableSet()
        )
    }

    @MockK
    lateinit var callRepository: CallRepository

    @RelaxedMockK
    lateinit var auditService: AuditService

    private lateinit var callFlatRateSetupPersistence: CallFlatRateSetupPersistence

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        callFlatRateSetupPersistence = CallFlatRateSetupPersistenceProvider(
            callRepository
        )
    }

    @Test
    fun `updateFlatRateSetup not-existing`() {
        every { callRepository.findById(eq(-1)) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { callFlatRateSetupPersistence.updateFlatRateSetup(-1, emptySet()) }
        assertThat(ex.entity).isEqualTo("call")
    }

    @Test
    fun updateFlatRateSetup() {
        val flatRateToUpdate = FlatRateSetup(
            setupId = FlatRateSetupId(callId = 1, type = FlatRateType.OtherOnStaff),
            rate = 10,
            isAdjustable = true
        )
        val flatRateToDelete = FlatRateSetup(
            setupId = FlatRateSetupId(callId = 1, type = FlatRateType.TravelOnStaff),
            rate = 2,
            isAdjustable = false
        )
        val modelToUpdate = FlatRateModel(
            callId = 1,
            type = flatRateToUpdate.setupId.type, // existing
            rate = 5, // changed
            isAdjustable = false // changed
        )
        val modelToCreate = FlatRateModel(
            callId = 1,
            type = FlatRateType.OfficeOnOther, // new
            rate = 5,
            isAdjustable = true
        )
        val call = callWithIdAndFlatRate(1, setOf(flatRateToUpdate, flatRateToDelete))
        every { callRepository.findById(eq(1)) } returns Optional.of(call)
        callFlatRateSetupPersistence.updateFlatRateSetup(1, setOf(modelToCreate, modelToUpdate))
        assertThat(call.flatRateSetup).containsExactly(
            FlatRateSetup(
                setupId = flatRateToUpdate.setupId,
                rate = modelToUpdate.rate,
                isAdjustable = modelToUpdate.isAdjustable
            ),
            FlatRateSetup(
                setupId = FlatRateSetupId(callId = 1, type = modelToCreate.type),
                rate = modelToCreate.rate,
                isAdjustable = modelToCreate.isAdjustable
            )
        )
    }

    @Test
    fun getFlatRateSetup() {
        val flatRate = setOf(FlatRateSetup(
            setupId = FlatRateSetupId(callId = 1, type = FlatRateType.OtherOnStaff),
            rate = 10,
            isAdjustable = true
        ))
        every { callRepository.findById(eq(1)) } returns Optional.of(callWithIdAndFlatRate(1, flatRate))
        assertThat(callFlatRateSetupPersistence.getFlatRateSetup(1)).isEqualTo(
            setOf(FlatRateModel(
                callId = 1,
                type = FlatRateType.OtherOnStaff,
                rate = 10,
                isAdjustable = true
            ))
        )
    }

    @Test
    fun `getFlatRateSetup empty`() {
        every { callRepository.findById(eq(1)) } returns Optional.of(callWithIdAndFlatRate(1, emptySet()))
        assertThat(callFlatRateSetupPersistence.getFlatRateSetup(1)).isEmpty()
    }

    @Test
    fun `getFlatRateSetup not-existing`() {
        every { callRepository.findById(eq(-1)) } returns Optional.empty()
        val ex = assertThrows<ResourceNotFoundException> { callFlatRateSetupPersistence.getFlatRateSetup(-1) }
        assertThat(ex.entity).isEqualTo("call")
    }

}
