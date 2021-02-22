package io.cloudflight.jems.server.common.exception

import org.aspectj.lang.reflect.MethodSignature
import java.lang.reflect.Method


class MockedMethodSignature(var isExceptional: Boolean) : MethodSignature {


    @ExceptionWrapper(SampleWrapperException::class)
    fun methodWithAspectWhichThrowsException() {
        throw Exception("sample exception!")
    }

    @ExceptionWrapper(SampleWrapperException::class)
    fun methodWithAspectWhichReturnsNormally(): String {
        return "result"
    }

    override fun getMethod(): Method {
        return if (isExceptional) javaClass.getDeclaredMethod("methodWithAspectWhichThrowsException")
        else javaClass.getDeclaredMethod("methodWithAspectWhichReturnsNormally")
    }

    override fun getModifiers(): Int {
        TODO("Not yet implemented")
    }

    override fun getParameterNames(): Array<String> {
        TODO("Not yet implemented")
    }

    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun getReturnType(): Class<*> {
        TODO("Not yet implemented")
    }

    override fun toLongString(): String {
        TODO("Not yet implemented")
    }

    override fun getExceptionTypes(): Array<Class<Any>> {
        TODO("Not yet implemented")
    }

    override fun toShortString(): String {
        TODO("Not yet implemented")
    }

    override fun getDeclaringType(): Class<*> {
        TODO("Not yet implemented")
    }

    override fun getParameterTypes(): Array<Class<Any>> {
        TODO("Not yet implemented")
    }

    override fun getDeclaringTypeName(): String {
        TODO("Not yet implemented")
    }

}
