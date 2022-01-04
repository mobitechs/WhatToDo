package  com.wahttodo.app.utils

class Constants {

    companion object {

        const val clientBusinessId = "1"

        //        const val BASE_URL = "https://mobitechs.in/plasdor/api/plasdor.php"
        const val BASE_URL = "https://mobitechs.in/whattodoApi/api/whattodo.php"
        const val TNC = "http://plasdorservice.com/tnc.html"
        const val Privacy = "http://plasdorservice.com/privacy.html"

        const val PROJECT_NAME = "WhatToDO"
        const val HORIZONTAL = "horizontal"
        const val VERTICAL = "vertical"
        const val USERDATA = "userData"

        const val ROOM_ID = "roomId"
        const val EARNED_POINTS = "earnedPoints"
        const val FIRST_FREE_ORDER_COMPLETE = "firstFreeOrderComplete"

        const val IS_TOKEN_SAVE_API_CALLED = "isTokenSAveAPICalled"
        const val IS_TOKEN_UPDATE = "isTokenUpdate"
        const val TOKEN = "token"
        const val DEVICE_ID = "deviceId"
        const val ISLOGIN = "isLogin"
        const val userId = "userId"


        //if u add new item in weightArray then please add in qtyArray as well its must
        val qtyArray = arrayOf("1", "2", "3", "4", "6", "8 ", "10", "12", "14", "16", "18", "20")

        val languageArray = arrayOf("English", "Hindi", "Marathi", "Telgu")
        val typeArray = arrayOf("Comedy", "Action", "Horror", "Drama","Thrill")

        //val daysArray = arrayOf("1", "2", "3", "4", "5", "6", "7")
        val daysArray = arrayOf("1", "2")
        val hourArray = arrayOf("1", "2", "4", "6")

        val ps5NdXSeriesXPriceArray = arrayOf("999", "1799", "2499", "3199", "3499", "3999", "4499")
        val ps4NdXOneXPriceArray = arrayOf("499", "899", "1399", "1699", "2099", "2399", "2699")
        val XOneSPriceArray = arrayOf("449", "849", "1349", "1649", "2049", "2349", "2649")
        val XSeriesSPriceArray = arrayOf("949", "1749", "2449", "3149", "3449", "3949", "4449")

        val ps4NdXOneXNdSPriceArrayHr = arrayOf("99", "199", "299", "399")
        val ps5NdXSeriesXNdSPriceArrayHr = arrayOf("199", "299", "399", "499")
        val xboxSeriesSPriceArrayHr = arrayOf("149", "249", "449", "599")
        val xboxOneSPriceArrayHr = arrayOf("79", "149", "249", "349")
//        val XOneSPriceArrayHr = arrayOf("200", "300", "400", "500", "600", "700", "800")
//        val XSeriesSPriceArrayHr = arrayOf("250", "350", "450", "550", "650", "750", "850")

        val orderStatusArray = arrayOf("Pending", "Complete")

    }
}


