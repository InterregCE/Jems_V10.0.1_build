package io.cloudflight.jems.server.plugin

import io.cloudflight.jems.plugin.contract.JemsPlugin
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Aspect
@Component
class PluginAspects(private val auditPublisher: ApplicationEventPublisher) {

    @Pointcut("target(io.cloudflight.jems.plugin.contract.JemsPlugin)")
    fun isJemsPlugin() {
        // this is intentional
    }

    @Pointcut("execution(* io.cloudflight.jems.plugin.contract.*.getName(..))")
    fun isNotGetNameMethod() {
        // this is intentional
    }

    @Pointcut("execution(* io.cloudflight.jems.plugin.contract.*.getVersion(..))")
    fun isNotGetVersionMethod() {
        // this is intentional
    }

    @Pointcut("execution(* io.cloudflight.jems.plugin.contract.*.getKey(..))")
    fun isNotGetKeyMethods() {
        // this is intentional
    }

    @Pointcut("execution(* io.cloudflight.jems.plugin.contract.*.getDescription(..))")
    fun isNotGetDescriptionMethods() {
        // this is intentional
    }

    @Around("isJemsPlugin()")
    @Order(Ordered.HIGHEST_PRECEDENCE + 50)
    fun wrapException(joinPoint: ProceedingJoinPoint): Any? =
        try {
            joinPoint.proceed()
        } catch (e: Throwable) {
            throw PluginErrorException(e, (joinPoint.target as JemsPlugin).getKey())
        }


    @Around("isJemsPlugin() && !isNotGetNameMethod() && !isNotGetVersionMethod() && !isNotGetKeyMethods() && !isNotGetDescriptionMethods()")
    @Order(Ordered.HIGHEST_PRECEDENCE + 51)
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
