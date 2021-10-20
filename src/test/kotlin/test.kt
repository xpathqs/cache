package org.xpathqs.cache.base

import org.xpathqs.cache.impl.CachedModel
import org.xpathqs.cache.impl.ExpiredCachedModel
import org.xpathqs.cache.impl.PersistentCache
import org.xpathqs.cache.value.CachedValue
import java.time.Duration

data class SomeComplexVal(
    val v1: String = "",
    val v2: String = "",
    val v3: String = ""
)

object SomeData : ICachedModel by CachedModel() , IExpirableCachedModel by ExpiredCachedModel(
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

