package io.cloudflight.jems.server.call.service.list_calls

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.IdNamePair
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ListCallsTest: UnitTest() {

    companion object {
        private const val CALL_ID = 1L

        private val call = IdNamePair(
            id = CALL_ID,
            name = "name"
        )
    }

    @MockK
    lateinit var persistence: CallPersistence

    @InjectMockKs
    private lateinit var listCalls: ListCalls

    @Test
    fun `list calls`() {
        every { persistence.listCalls(null) } returns listOf(call)
        assertThat(listCalls.list(null)).containsExactly(call)
    }

    @Test
    fun `list calls - none found`() {
        every { persistence.listCalls(null) } returns emptyList()
        assertThat(listCalls.list(null)).isEmpty()
    }
}
