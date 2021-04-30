package io.wax911.emoji.buildSrc.module

internal object Modules {

    interface Module {
        val id: String

        /**
         * @return Formatted id of module as a path string
         */
        fun path(): String = ":$id"
    }

    enum class App(override val id: String) : Module {
        Sample("app")
    }

    enum class Library(override val id: String) : Module {
        Emojify("emojify")
    }
}