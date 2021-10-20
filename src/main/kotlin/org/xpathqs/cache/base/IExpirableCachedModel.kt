package org.xpathqs.cache.base

import org.xpathqs.cache.value.CachedValue
import org.xpathqs.cache.value.IExpirable
import org.xpathqs.cache.value.createValue
import java.time.Duration
import kotlin.reflect.KClass

interface IExpirableCachedModel {
    val expCache: ICache<String, CachedValue<Any>>
    val expValueCls: KClass<*>
}

inline fun <reified T : Any> IExpirableCachedModel.fromCache(
    key: String,
    liveTime: Duration,
    updateFun: () -> T
) = fromCache(key, liveTime, false, updateFun)

inline fun <reified T : Any> IExpirableCachedModel.fromCache(
    key: String,
    liveTime: Duration,
    updateInitTime: Boolean,
    updateFun: () -> T
): T {
    if (expCache.contains(key)) {
        val v = (expCache.get(key) as IExpirable)
        if (v.isValid()) {
            if (updateInitTime) {
                v.refresh()
                expCache.put(key, v as CachedValue<Any>)
            }
            return (v as CachedValue<T>).data
        }
    }
    val value = updateFun()
    val v = createValue(expValueCls, value)
    (v as IExpirable).apply {
        this.liveTimeMs = liveTime.toMillis()
    }

    expCache.put(key, v as CachedValue<Any>)
    return value
}