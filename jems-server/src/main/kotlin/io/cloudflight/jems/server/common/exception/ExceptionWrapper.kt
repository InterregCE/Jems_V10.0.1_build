package io.cloudflight.jems.server.common.exception

import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExceptionWrapper(val exception: KClass<out ApplicationException>)
