package com.example.lab10_login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProfileClass(
    @Expose
    @SerializedName("std_id") val std_id :String,

    @Expose
    @SerializedName("std_name") val std_name :String,

    @Expose
    @SerializedName("std_gender") val std_gender :String,

    @Expose
    @SerializedName("role") val role :String
)
