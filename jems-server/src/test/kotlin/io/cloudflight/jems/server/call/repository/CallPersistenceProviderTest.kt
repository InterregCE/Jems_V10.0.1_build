package io.cloudflight.jems.server.call.repository

import io.cloudflight.jems.api.call.dto.CallStatus
import io.cloudflight.jems.server.UnitTest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class CallPersistenceProviderTest : UnitTest() {

    @MockK
    private lateinit var repository: CallRepository

    @InjectMockKs
    private lateinit var callPersistenceProvider: CallPersistenceProvider

    @Test
    fun `should return false when there is no published call`() {
        every { repository.existsByStatus(CallStatus.PUBLISHED) } returns false
        assertThat(callPersistenceProvider.hasAnyCallPublished())
            .isEqualTo(false)
    }

    @Test
    fun `should return true when there is a published call`() {
        every { repository.existsByStatus(CallStatus.PUBLISHED) } returns true
        assertThat(callPersistenceProvider.hasAnyCallPublished())
            .isEqualTo(true)
    }
}
