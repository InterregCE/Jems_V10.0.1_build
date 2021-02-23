package io.cloudflight.jems.server.programme.service.indicator.get_result_indicator

import io.cloudflight.jems.server.programme.repository.indicator.ResultIndicatorNotFoundException
import io.cloudflight.jems.server.programme.service.indicator.IndicatorsBaseTest
import io.cloudflight.jems.server.programme.service.indicator.ResultIndicatorPersistence
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class GetResultIndicatorTest : IndicatorsBaseTest() {

    @MockK
    lateinit var persistence: ResultIndicatorPersistence

    @InjectMockKs
    lateinit var getResultIndicator: GetResultIndicator

    @Test
    fun `should return result indicator when there is no problem`() {
        val resultIndicatorDetail = buildResultIndicatorDetailInstance()
        every { persistence.getResultIndicator(1L) } returns resultIndicatorDetail
        assertThat(getResultIndicator.getResultIndicator(1L))
            .isEqualTo(resultIndicatorDetail)
    }

    @Test
    fun `should throw ResultIndicatorNotFoundException when result indicator not exists`() {
        val exception = ResultIndicatorNotFoundException()
        every { persistence.getResultIndicator(-1L) } throws exception
        assertThatThrownBy { getResultIndicator.getResultIndicator(-1L) }
            .isEqualTo(exception)
    }
}
