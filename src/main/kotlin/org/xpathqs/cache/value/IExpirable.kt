package org.xpathqs.cache.value


interface IExpirable  {
    var liveTimeMs: Long
    var initTimeMs: Long

    fun isValid(ms: Long = System.currentTimeMillis())
        = (liveTimeMs + initTimeMs) > ms

    fun refresh(ms: Long = System.currentTimeMillis()) {
        initTimeMs = ms
    }
}