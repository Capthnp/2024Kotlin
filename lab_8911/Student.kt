package com.example.lab_8911
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Student(

    @Expose
    @SerializedName("std_id") val std_id: String,

    @Expose
    @SerializedName("std_name") val std_name: String,

    @Expose
    @SerializedName("std_gender") val std_gender: String,

    @Expose
    @SerializedName("std_age") val std_age: Int
) : Parcelable {}