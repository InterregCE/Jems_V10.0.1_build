package io.cloudflight.jems.server.plugin

import io.cloudflight.jems.plugin.contract.JemsPlugin
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Aspect
@Component
class PluginAuditLogAspect(private val auditPublisher: ApplicationEventPublisher) {

    @Pointcut("target(io.cloudflight.jems.plugin.contract.JemsPlugin)")
    fun isJemsPlugin() {
    }

    @Pointcut("execution(* io.cloudflight.jems.plugin.contract.*.getName(..))")
    fun isNotGetNameMethod() {
    }

    @Pointcut("execution(* io.cloudflight.jems.plugin.contract.*.getVersion(..))")
    fun isNotGetVersionMethod() {
    }

    @Pointcut("execution(* io.cloudflight.jems.plugin.contract.*.getKey(..))")
    fun isNotGetKeyMethods() {
    }

    @Pointcut("execution(* io.cloudflight.jems.plugin.contract.*.getDescription(..))")
    fun isNotGetDescriptionMethods() {
    }

    @Around("isJemsPlugin() && !isNotGetNameMethod() && !isNotGetVersionMethod() && !isNotGetKeyMethods() && !isNotGetDescriptionMethods()")
    fun audit(joinPoint: ProceedingJoinPoint): Any? {
        val plugin = joinPoint.target as JemsPlugin
        auditPublisher.publishEvent(
            pluginCalled(
                plugin,
                plugin.getName(),
                plugin.getKey(),
                joinPoint.signature.name
            )
        )
        val result = joinPoint.proceed()
        auditPublisher.publishEvent(
            pluginExecuted(
                plugin,
                plugin.getName(),
                plugin.getKey(),
                joinPoint.signature.name
            )
        )
        return result
    }
}
