package org.telegram.translatebot.config

import java.util.Properties

class PropertiesSetup {
    @Suppress("UNCHECKED_CAST")
    fun <T> getProp(key: String): T {
        val props  = javaClass.classLoader.getResourceAsStream("application.properties").use {
            Properties().apply { load(it) }
        }
        return (props.getProperty(key) as T) ?: throw RuntimeException("could not find property $key")
    }
}