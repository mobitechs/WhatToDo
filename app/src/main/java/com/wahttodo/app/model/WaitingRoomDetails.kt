package com.wahttodo.app.model

import com.google.firebase.Timestamp

data class WaitingRoomDetails(
    var timeStamp: Timestamp,
    var matchedMoviesList: ArrayList<MatchedMoviesList>,
    var joinedUserList: ArrayList<JoinedUserList>
)

data class JoinedUserList(
    var userId: String,
    var userName: String
)

data class MatchedMoviesList(
    var movieName: String,
    var rating: String,
    var description: String,
)

data class DumpMovieDetails(
    var dumpedMoviesList: ArrayList<DumpedMoviesList>,
    var noOfUsers:Int
)

data class DumpedMoviesList(
    var movieName: String,
    var rating: String,
    var description: String,
    var matchedCount: Int,
)