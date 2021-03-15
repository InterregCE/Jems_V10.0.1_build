package io.cloudflight.jems.server.common.exception

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.lang.reflect.Method


@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class ExceptionWrapperAspect {

    @Around("@annotation(io.cloudflight.jems.server.common.exception.ExceptionWrapper)")
    fun wrapException(joinPoint: ProceedingJoinPoint): Any? {
        try {
            return joinPoint.proceed()
        } catch (e: Throwable) {
            val methodSignature: MethodSignature = joinPoint.signature as MethodSignature
            val method: Method = methodSignature.method
            val exceptionWrapper: ExceptionWrapper =
                method.annotations.find { it is ExceptionWrapper } as ExceptionWrapper
            val constructor =
                exceptionWrapper.exception.constructors.firstOrNull { it.parameters.size == 1 && it.parameters.first().type.classifier == Throwable::class }
            throw
            constructor?.call(e) ?: ApplicationException(
                code = DEFAULT_ERROR_CODE,
                i18nMessage = DEFAULT_ERROR_MESSAGE,
                cause = e
            )
        }
    }
}
