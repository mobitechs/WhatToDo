package com.wahttodo.app.callbacks

import com.wahttodo.app.model.JoinedRoomListItems

interface GroupListCallback {
    fun getRoomId(roomId: String)
    fun deleteRoom(item: JoinedRoomListItems, position: Int)
    fun deleteRoomByHost(item: JoinedRoomListItems, position: Int)
}