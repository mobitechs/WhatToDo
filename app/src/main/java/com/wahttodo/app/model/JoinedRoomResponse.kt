package com.wahttodo.app.model

data class JoinedRoomResponse(
    val Response: List<JoinedRoomListItems>,
    val code: Int,
    val status: Int
)


data class JoinedRoomListItems(
    val id: String,
    val roomId: String,
    val roomName: String,
    val userId: String
)