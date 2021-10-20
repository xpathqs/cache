package org.xpathqs.cache.value

open class ExpirableCachedValue<T> (data: T)  : CachedValue<T>(data), IExpirable {
    override var liveTimeMs = 0L
    override var initTimeMs = System.currentTimeMillis()
}