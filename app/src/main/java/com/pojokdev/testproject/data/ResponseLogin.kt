package com.pojokdev.testproject.data

import com.google.gson.annotations.SerializedName

data class ResponseLogin(
    @SerializedName("values") val values: Boolean
)