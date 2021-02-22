package io.cloudflight.jems.server.programme.service.indicator.get_output_indicator

import io.cloudflight.jems.server.programme.repository.indicator.OutputIndicatorNotFoundException
import io.cloudflight.jems.server.programme.service.indicator.IndicatorsBaseTest
import io.cloudflight.jems.server.programme.service.indicator.OutputIndicatorPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test


internal class GetOutputIndicatorTest : IndicatorsBaseTest() {

    @MockK
    lateinit var persistence: OutputIndicatorPersistence

    @InjectMockKs
    lateinit var getOutputIndicator: GetOutputIndicator

    @Test
    fun `should return output indicator when there is no problem`() {
        val outputIndicatorDetail = buildOutputIndicatorDetailInstance()
        every { persistence.getOutputIndicator(1L) } returns outputIndicatorDetail
        assertThat(getOutputIndicator.getOutputIndicator(1L))
            .isEqualTo(outputIndicatorDetail)
    }

    @Test
    fun `should throw OutputIndicatorNotFoundException when output indicator not exists`() {
        val exception = OutputIndicatorNotFoundException()
        every { persistence.getOutputIndicator(-1L) } throws exception
        assertThatThrownBy { getOutputIndicator.getOutputIndicator(-1L) }
            .isEqualTo(exception)
    }
}
