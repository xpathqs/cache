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

package org.xpathqs.cache.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.xpathqs.cache.value.CachedValue
import org.xpathqs.cache.value.createValue
import java.io.File
import java.lang.reflect.Array.newInstance
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

open class PersistentCache<V>(
    private var filePath: String = "",
    private val mapper: ObjectMapper = jacksonObjectMapper()
) : Cache<String, V>() {
    data class SerializedValue(
        val key: Any,
        val value: Any?,
        val valueWrapperCls: String,
        val valueCls: String
    )

    override fun put(k: String, v: V) {
        super.put(k, v)
        save()
    }

    fun toSerializedValue() =
        map.map { (k, v) ->
            SerializedValue(
                key = k,
                value = v,
                valueWrapperCls = v!!::class.java.name,
                valueCls = (v as CachedValue<*>).data!!::class.java.name
            )
        }

    fun save() {
        val sb = StringBuilder()
        toSerializedValue().forEach {
            sb.append(mapper.writeValueAsString(it) + "\n")
        }
        File(filePath).writeText(sb.toString())
    }

    @Suppress("PrintStackTrace", "TooGenericExceptionCaught")
    fun load() {
        map.clear()
        if (!File(filePath).isFile) {
            return
        }
        try {
            val lines = File(filePath).readLines()
            lines.forEach {
                parse(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun changePath(path: String) {
        this.filePath = path
    }

    fun parse(line: String) {
        val obj = mapper.readTree(line)

        val key = obj.get(SerializedValue::key.name).asText()
        val valueWrapperCls = obj.get(SerializedValue::valueWrapperCls.name).asText()
        val valueCls = obj.get(SerializedValue::valueCls.name).asText()

        val value = obj.get(SerializedValue::value.name)
        val dataValueJson = value.get(CachedValue<*>::data.name)
        val dataValue = getObjFromNode(dataValueJson) {
            val json = dataValueJson.toString()
            val cls = getCls(valueCls)
            mapper.readValue(json, cls)
        }

        val cls = this::class.java.classLoader.loadClass(valueWrapperCls).kotlin
        val v = createValue(cls, dataValue) as V

        cls.memberProperties
            .filterIsInstance<KMutableProperty<*>>()
            .filter {
                it.name != CachedValue<*>::data.name
                        && !it.name.contains("$")
            }.forEach {
                it.setter.call(v, getObjFromNode(value.get(it.name)))
            }

        map[key] = v
    }

    fun getCls(clsName: String): Class<*> {
        if (clsName.startsWith("[L")) {
            val cn = clsName.drop(2).dropLast(1)
            val cls = this::class.java.classLoader.loadClass(cn)
            return newInstance(cls, 0).javaClass
        }
        val cls = this::class.java.classLoader.loadClass(clsName)
        return cls
    }


    fun getObjFromNode(v: JsonNode, custom: (() -> Any)? = null): Any {
        return when (v) {
            is TextNode -> {
                v.asText()
            }
            is IntNode -> {
                v.asInt()
            }
            is LongNode -> {
                v.asLong()
            }
            is DoubleNode -> {
                v.asDouble()
            }
            is BooleanNode -> {
                v.asBoolean()
            }
            else -> {
                custom!!()
            }
        }
    }
}