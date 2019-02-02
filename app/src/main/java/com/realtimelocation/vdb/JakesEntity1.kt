package com.realtimelocation.vdb

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "jakes_data")
class JakesEntity {

    @PrimaryKey(autoGenerate = false)
    var id: Int? = null

    @SerializedName("full_name")
    @Expose
    @ColumnInfo(name = "full_name")
    var fullName: String? = null

    @SerializedName("description")
    @Expose
    @ColumnInfo(name = "description")
    var description: String? = null

    @SerializedName("language")
    @Expose
    @ColumnInfo(name = "language")
    var language: String? = null

    @SerializedName("open_issues_count")
    @Expose
    @ColumnInfo(name = "open_issues_count")
    var openIssuesCount: Int? = null

    @SerializedName("forks_count")
    @Expose
    @ColumnInfo(name = "forks_count")
    var forksCount: Int? = null


}
