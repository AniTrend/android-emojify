package io.wax911.emoji.buildSrc

import io.wax911.emoji.buildSrc.module.Modules

object Libraries {
    object AniTrend {

        object Emojify {
            val emojify = Modules.Library.Emojify.path()
            val contract = Modules.Library.Contract.path()
            val serializerKotlinx = Modules.Library.SerializerKotlinX.path()
            val serializerGson = Modules.Library.SerializerGson.path()
            val serializerMoshi = Modules.Library.SerializerMoshi.path()
        }
    }
}
