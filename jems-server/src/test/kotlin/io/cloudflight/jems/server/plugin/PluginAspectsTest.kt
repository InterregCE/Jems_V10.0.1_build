package io.cloudflight.jems.server.plugin

import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.plugin.contract.pre_condition_check.PreConditionCheckPlugin
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.audit.model.AuditCandidateEvent
import io.cloudflight.jems.server.common.event.JemsAuditEvent
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import org.aspectj.lang.ProceedingJoinPoint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.context.ApplicationEventPublisher

internal class PluginAspectsTest : UnitTest() {

    @RelaxedMockK
    lateinit var auditPublisher: ApplicationEventPublisher

    @RelaxedMockK
    lateinit var preConditionCheckPlugin: PreConditionCheckPlugin

    @InjectMockKs
    lateinit var pluginAspects: PluginAspects

    @RelaxedMockK
    lateinit var joinPoint: ProceedingJoinPoint

    @Test
    fun `should catch uncaught exceptions thrown by the plugin`() {
        val expectedException = RuntimeException("plugin exception")
        every { joinPoint.proceed() } throws expectedException
        every { joinPoint.target } returns preConditionCheckPlugin
        val exception = assertThrows<PluginErrorException> {
            pluginAspects.wrapException(joinPoint)
        }
        assertThat(exception.cause).isEqualTo(expectedException)
    }

    @Test
    fun `should publish audit logs when a method of a plugins is called`() {
        every { joinPoint.target } returns preConditionCheckPlugin
        every { joinPoint.proceed() } returns Unit

        val pluginAudits = mutableListOf<JemsAuditEvent>()
        pluginAspects.audit(joinPoint)
        verify {
            auditPublisher.publishEvent(capture(pluginAudits))
        }
        assertThat(pluginAudits[0].auditCandidate.action).isEqualTo(AuditAction.PLUGIN_CALLED)
        assertThat(pluginAudits[1].auditCandidate.action).isEqualTo(AuditAction.PLUGIN_EXECUTED)

    }

}
