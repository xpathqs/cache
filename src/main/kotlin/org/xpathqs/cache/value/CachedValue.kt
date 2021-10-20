package org.xpathqs.cache.value

import kotlin.reflect.KClass

open class CachedValue<T>(
    val data: T
)

fun createValue(valueCls: KClass<*>, data: Any): CachedValue<Any> {
    val c = valueCls.constructors.firstOrNull {
        it.parameters.size == 1 && it.parameters.first().name == "data"
    }
    return c?.call(data) as? CachedValue<Any> ?: throw IllegalArgumentException("can't create an instance")
}