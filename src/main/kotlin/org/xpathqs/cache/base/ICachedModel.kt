package org.xpathqs.cache.base

import org.xpathqs.cache.value.CachedValue
import org.xpathqs.cache.value.createValue
import kotlin.reflect.KClass

interface ICachedModel {
    val cache: ICache<String, CachedValue<Any>>
    val valueCls: KClass<*>
}

inline fun <reified T: Any> ICachedModel.fromCache(key: String, updateFun: ()->T) : T {
    if(cache.contains(key)) {
        return (cache.get(key) as CachedValue<T>).data
    }
    val value = updateFun()
    val v = createValue(valueCls, value)

    cache.put(key, v)
    return value
}








