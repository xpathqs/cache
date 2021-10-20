/*
 * Copyright (c) 2021 XPATH-QS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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