package org.xpathqs.cache.impl

import org.xpathqs.cache.base.ICache
import org.xpathqs.cache.base.ICachedModel
import org.xpathqs.cache.value.CachedValue
import kotlin.reflect.KClass

open class CachedModel(
    override val cache: ICache<String, CachedValue<Any>> = Cache(),
    override val valueCls: KClass<*> = CachedValue::class
) : ICachedModel