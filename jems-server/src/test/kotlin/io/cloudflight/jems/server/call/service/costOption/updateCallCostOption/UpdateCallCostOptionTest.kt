package io.cloudflight.jems.server.call.service.costOption.updateCallCostOption

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallCostOption
import io.cloudflight.jems.server.call.service.model.CallDetail
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class UpdateCallCostOptionTest : UnitTest() {

    companion object {
        private fun call(id: Long, published: Boolean): CallDetail {
            val call = mockk<CallDetail>()
            every { call.id } returns id
            every { call.isPublished() } returns published
            return call
        }

    }

    @MockK
    lateinit var persistence: CallPersistence

    @InjectMockKs
    private lateinit var interactor: UpdateCallCostOption

    @BeforeEach
    fun reset() {
        clearMocks(persistence)
    }

    @Test
    fun `updateCallCostOption - DRAFT`() {
        val callId = 52L
        every { persistence.getCallById(callId) } returns call(callId, published = false)

        val costOption = mockk<CallCostOption>()
        every { persistence.updateCallCostOption(any(), any()) } returnsArgument 1
        assertThat(interactor.updateCallCostOption(callId, costOption)).isEqualTo(costOption)

        verify(exactly = 1) { persistence.updateCallCostOption(callId, costOption) }
    }

    @ParameterizedTest(name = "updateCallCostOption - PUBLISHED (selecting unitCost {0})")
    @ValueSource(booleans = [true, false])
    fun `updateCallCostOption - PUBLISHED`(variant: Boolean) {
        val callId = 59L
        every { persistence.getCallById(callId) } returns call(callId, published = true)
        every { persistence.getCallCostOption(callId) } returns CallCostOption(variant, !variant)

        val costOption = CallCostOption(true, true)
        every { persistence.updateCallCostOption(any(), any()) } returnsArgument 1
        assertThat(interactor.updateCallCostOption(callId, costOption.copy())).isEqualTo(costOption.copy())

        verify(exactly = 1) { persistence.updateCallCostOption(callId, costOption) }
    }

    @ParameterizedTest(name = "updateCallCostOption - PUBLISHED - restricted (deselecting lumpSum {0})")
    @ValueSource(booleans = [true, false])
    fun `updateCallCostOption - PUBLISHED - restricted`(variant: Boolean) {
        val callId = 61L
        every { persistence.getCallById(callId) } returns call(callId, published = true)
        every { persistence.getCallCostOption(callId) } returns CallCostOption(true, true)
        val costOption = CallCostOption(variant, !variant)
        assertThrows<CallNotEditableException> { interactor.updateCallCostOption(callId, costOption) }
    }

}
