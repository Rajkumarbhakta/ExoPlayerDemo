package com.rkbapps.exoplayerdemo.models

import kotlinx.serialization.Serializable

@Serializable
object StorageLocation {
    const val INTERNAL = "INTERNAL"
    const val EXTERNAL = "EXTERNAL"
}