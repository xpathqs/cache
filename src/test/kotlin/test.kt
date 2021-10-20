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

import org.xpathqs.cache.impl.CachedModel
import org.xpathqs.cache.impl.ExpiredCachedModel
import org.xpathqs.cache.impl.PersistentCache
import java.time.Duration

data class SomeComplexVal(
    val v1: String = "",
    val v2: String = "",
    val v3: String = ""
)

object SomeData : ICachedModel by CachedModel(), IExpirableCachedModel by ExpiredCachedModel(
    expCache = PersistentCache("test.json")
) {
    val id: String
        get() = fromCache("asd") {
            println("Update was triggered")
            "some val"
        }

    val id2: String
        get() = fromCache("asd", Duration.ofSeconds(2)) {
            println("Update was triggered")
            "some val"
        }

    val id3: SomeComplexVal
        get() = fromCache("k3", Duration.ofSeconds(2)) {
            println("Update was triggered")
            SomeComplexVal("vvv1", "vvv2", "vvv2")
        }

    val id4: Array<SomeComplexVal>
        get() = fromCache("k4", Duration.ofSeconds(2)) {
            println("Update was triggered")
            arrayOf(
                SomeComplexVal("vvv1", "vvv2", "vvv2"),
                SomeComplexVal("vvv1", "vvv2", "vvv2")
            )
        }
}

fun main() {

    // SomeData.id2

    (SomeData.expCache as PersistentCache<*>).load()
    /* val cache = PersistentCache<CachedValue<Any>>("test.json")
     cache.load()*/

    println(SomeData.id4)

    //  SomeData.id2
    //  SomeData.id3

    //  Thread.sleep(3000)

    //  SomeData.id2
}

