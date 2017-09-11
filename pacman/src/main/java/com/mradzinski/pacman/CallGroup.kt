package com.mradzinski.pacman

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class CallGroup(val groupId: Long, var calls: Int): Parcelable, Serializable {

    constructor(source: Parcel): this(source.readLong(), source.readInt())

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null || other::javaClass !== this.javaClass) return false
        return (other as? CallGroup)?.groupId == this.groupId
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(groupId)
        dest?.writeInt(calls)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<CallGroup> = object : Parcelable.Creator<CallGroup> {
            override fun createFromParcel(source: Parcel): CallGroup{
                return CallGroup(source)
            }

            override fun newArray(size: Int): Array<CallGroup?> {
                return arrayOfNulls(size)
            }
        }
    }
}