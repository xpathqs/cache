package org.xpathqs.cache.impl

import org.xpathqs.cache.base.ICache
import org.xpathqs.cache.base.IExpirableCachedModel
import org.xpathqs.cache.value.CachedValue
import org.xpathqs.cache.value.ExpirableCachedValue
import kotlin.reflect.KClass

open class ExpiredCachedModel(
    override val expCache: ICache<String, CachedValue<Any>> = Cache(),
    override val expValueCls: KClass<*> = ExpirableCachedValue::class
) : IExpirableCachedModel