package io.cloudflight.jems.server.call.service.costOption.getCallCostOption

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.call.service.CallPersistence
import io.cloudflight.jems.server.call.service.model.CallCostOption
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GetCallCostOptionTest : UnitTest() {

    @MockK
    lateinit var persistence: CallPersistence

    @InjectMockKs
    private lateinit var interactor: GetCallCostOption

    @Test
    fun getCallCostOption() {
        val costOption = mockk<CallCostOption>()
        every { persistence.getCallCostOption(45L) } returns costOption
        assertThat(interactor.getCallCostOption(45L)).isEqualTo(costOption)
    }

}
