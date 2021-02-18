package io.cloudflight.jems.server.common.exception

import io.cloudflight.jems.api.common.dto.I18nMessage
import io.cloudflight.jems.server.UnitTest
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.aspectj.lang.ProceedingJoinPoint
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class SampleWrapperException(cause: Throwable) :
    ApplicationException(code = "ABC-123", i18nMessage = I18nMessage(i18nKey = "sample.exception"), cause = cause)

internal class ExceptionWrapperAspectTest : UnitTest() {

    private val exceptionWrapperAspect: ExceptionWrapperAspect = ExceptionWrapperAspect()

    @MockK
    lateinit var joinPoint: ProceedingJoinPoint

    @Test
    fun `should catch uncaught exceptions thrown by the annotated method and wrap them in a subclass of ApplicationException which is specified as the input parameter of annotation`() {
        val customMethodSignature = MockedMethodSignature(true)
        val internalException = Exception("sample exception!!")
        every { joinPoint.signature } returns customMethodSignature
        every { joinPoint.proceed() } throws internalException
        val exception = Assertions.assertThrows(SampleWrapperException::class.java) {
            exceptionWrapperAspect.wrapException(joinPoint)
        }
        Assertions.assertEquals("ABC-123", exception.code)
        Assertions.assertEquals("sample.exception", exception.i18nMessage.i18nKey)
        Assertions.assertEquals(internalException, exception.cause)

    }

    @Test
    fun `should return normally when annotated method returns normally`() {
        val customMethodSignature = MockedMethodSignature(false)
        every { joinPoint.signature } returns customMethodSignature
        every { joinPoint.proceed() } returns customMethodSignature.methodWithAspectWhichReturnsNormally()
        val result = exceptionWrapperAspect.wrapException(joinPoint)
        Assertions.assertEquals("result", result)
    }

}


