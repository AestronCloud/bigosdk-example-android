package sg.bigo.common.data

import org.json.JSONArray


/*
var CONFIG_FILTER_LIST = "[{\"defaultStrength\":0,\"filterType\":2,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h1\\/M09\\/83\\/F6\\/svobAFyLaqGIfN0bAABJHKlQ-q8AAJGyQE_LNkAAEk0643.png\",\"id\":\"1_0\",\"tob_id\":\"0\",\"name\":\"原圖\",\"resource\":\"\",\"resourceName\":\"\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g2\\/M05\\/06\\/F5\\/iwVsD1wOEO2IMld3AAAneS0weqsAAbxzwGwTkQAACeR346.png\",\"id\":\"1_8_v2\",\"tob_id\":\"1\",\"name\":\"情书\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h1\\/M07\\/45\\/EF\\/s_obAFz_qCyIPpiXAAMrh6Pg5DwABL9wwD7-zYAAyuf696.png\",\"resourceName\":\"lut_se_new.png\"}," +
        "{\"defaultStrength\":52,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h1\\/M08\\/83\\/F5\\/svobAFyLaqGIVEj3AABGfHHPIZMAAJGyAK6wrQAAEaU974.png\",\"id\":\"1_10\",\"tob_id\":\"2\",\"name\":\"鮮明\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g1\\/M04\\/12\\/17\\/iwVsDlwRwY-IGwtnAAHqOT8o_REAAiTtwIwDCMAAepR850.png\",\"resourceName\":\"lutimage_beauty_default.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigolive.tv\\/live\\/4h5\\/2dlJhT.png\",\"id\":\"1_46\",\"tob_id\":\"3\",\"name\":\"少女\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/4h7\\/1KNd3o.png\",\"resourceName\":\"f_pink_v2.png\"}," +
        "{\"defaultStrength\":60,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigolive.tv\\/live\\/4h7\\/2H1Xxk.png\",\"id\":\"1_45\",\"tob_id\":\"4\",\"name\":\"質感\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/4h6\\/0nKpbf.png\",\"resourceName\":\"f_texture_v2.png\"}," +
        "{\"defaultStrength\":80,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigolive.tv\\/live\\/4h6\\/0FDQ8I.png\",\"id\":\"1_44\",\"tob_id\":\"5\",\"name\":\"高雅\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/4h6\\/0RyTFp.png\",\"resourceName\":\"f_chic_v2.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigolive.tv\\/live\\/4h7\\/2TDj9e.png\",\"id\":\"1_47\",\"tob_id\":\"6\",\"name\":\"彩虹\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/4h5\\/1ED5uJ.png\",\"resourceName\":\"f_rainbow_v2.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g1\\/M04\\/11\\/DD\\/iwVsDlwOEOuIC-0SAAAsLtMcNVQAAh_dACpvk0AACxG423.png\",\"id\":\"1_1_v2\",\"tob_id\":\"7\",\"name\":\"粉嫩\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h2\\/M0C\\/29\\/E3\\/8vobAFz_qCaIN2LXAAG7v2-c1g8AAJlkgKCaP8AAbvX452.png\",\"resourceName\":\"lut_babypink.png\"}," +
        "{\"defaultStrength\":80,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g2\\/M00\\/06\\/F5\\/iwVsD1wOEOyITBzQAAAtz6xWgYkAAbx0QD2piEAAC3n699.png\",\"id\":\"1_2\",\"tob_id\":\"8\",\"name\":\"清晰\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g2\\/M04\\/07\\/08\\/nXfpA1wPOIeIV-CWAAN-DmbfsQQAAb4egEwcigAA34m770.png\",\"resourceName\":\"lutimage_modern.png\"}," +
        "{\"defaultStrength\":80,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g2\\/M05\\/06\\/F5\\/iwVsD1wOEOyIMbuaAAAuLVrXvpYAAbxzwGwH_8AAC5F862.png\",\"id\":\"1_3\",\"tob_id\":\"9\",\"name\":\"自然\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g1\\/M00\\/11\\/F0\\/LXz8F1wPOd2IaFjBAALh0WE7HfgAAiGIQGSytgAAuHp032.png\",\"resourceName\":\"lutimage_original.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g2\\/M0B\\/06\\/F5\\/iwVsD1wOEOuIdzPbAAAszEbW178AAbx0wDM0sMAACzk084.png\",\"id\":\"1_4_v2\",\"tob_id\":\"10\",\"name\":\"櫻桃\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h2\\/M0E\\/36\\/63\\/8PobAFz_qCeIHcihAAIL98A0aXUAAKKogIqVrYAAgwP827.png\",\"resourceName\":\"lut_cherry.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g1\\/M0A\\/11\\/DD\\/nXfpBFwOEOuIOH3DAAAm9H9Ua7UAAh_bAOqpYEAACcM327.png\",\"id\":\"1_5_v2\",\"tob_id\":\"11\",\"name\":\"神秘\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h3\\/M0E\\/35\\/6E\\/LvsbAFz_qCaIEZx9AABop6-aKpsAAKFQAHnsGcAAGi_598.png\",\"resourceName\":\"lut_danube_new.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g2\\/M09\\/06\\/F5\\/nXfpA1wOEO2IRpbqAAAngqJLqCsAAbx0gDS7BsAACea442.png\",\"id\":\"1_6_v2\",\"tob_id\":\"12\",\"name\":\"暖陽\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h2\\/M04\\/29\\/EB\\/8vobAFz_qhmIU04UAANuuS-YvvMAAJl7AC2m1IAA27R828.png\",\"resourceName\":\"lut_a6.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\//\\giftesx.bigo.sg\\/live\\/g2\\/M0A\\/06\\/F5\\/iwVsD1wOEOyIKZ-rAAAnC2ZtEnwAAbxywP-mXwAACcj278.png\",\"id\":\"1_7_v2\",\"tob_id\":\"13\",\"name\":\"樱花\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h1\\/M0B\\/A0\\/06\\/svobAFz_qCyIaAr7AANuOVVuru8ABEPCAGj3d0AA25R474.png\",\"resourceName\":\"lut_sakura.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h1\\/M06\\/83\\/F8\\/tPobAFyLaqGIaV4VAABHBQ-awDAAAJG0wDcLTAAAEcd595.png\",\"id\":\"1_9\",\"tob_id\":\"lut_beauty\",\"name\":\"經典\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g1\\/M01\\/11\\/F2\\/iwVsDlwPXPeIZ5R8AAwAaIiYBKAAAiGzAPO6ZIADACA668.png\",\"resourceName\":\"lutimage_classic.png\"}]"
 */

var CONFIG_FILTER_LIST = "[{\"defaultStrength\":0,\"filterType\":2,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h1\\/M09\\/83\\/F6\\/svobAFyLaqGIfN0bAABJHKlQ-q8AAJGyQE_LNkAAEk0643.png\",\"id\":\"1_0\",\"tob_id\":\"0\",\"name\":\"原图\",\"resource\":\"\",\"resourceName\":\"\"}," +
        "{\"defaultStrength\":30,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h1\\/M08\\/83\\/F5\\/svobAFyLaqGIVEj3AABGfHHPIZMAAJGyAK6wrQAAEaU974.png\",\"id\":\"1_10\",\"tob_id\":\"1\",\"name\":\"鲜明\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g2\\/M00\\/0A\\/3B\\/iwVsD1vqgOyIK9PyAAHj5mbGw-kAAYuBQBqePMAAeP-066.mp3\",\"resourceName\":\"lutimage_beauty_brown.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigolive.tv\\/live\\/4h5\\/2dlJhT.png\",\"id\":\"1_46\",\"tob_id\":\"2\",\"name\":\"少女\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/4h7\\/1KNd3o.png\",\"resourceName\":\"f_pink_v2.png\"}," +
        "{\"defaultStrength\":60,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigolive.tv\\/live\\/4h7\\/2H1Xxk.png\",\"id\":\"1_45\",\"tob_id\":\"3\",\"name\":\"质感\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/4h6\\/0nKpbf.png\",\"resourceName\":\"f_texture_v2.png\"}," +
        "{\"defaultStrength\":80,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigolive.tv\\/live\\/4h6\\/0FDQ8I.png\",\"id\":\"1_44\",\"tob_id\":\"4\",\"name\":\"高雅\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/4h6\\/0RyTFp.png\",\"resourceName\":\"f_chic_v2.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigolive.tv\\/live\\/4h7\\/2TDj9e.png\",\"id\":\"1_47\",\"tob_id\":\"5\",\"name\":\"彩虹\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/4h5\\/1ED5uJ.png\",\"resourceName\":\"f_rainbow_v2.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g1\\/M04\\/11\\/DD\\/iwVsDlwOEOuIC-0SAAAsLtMcNVQAAh_dACpvk0AACxG423.png\",\"id\":\"1_1_v2\",\"tob_id\":\"6\",\"name\":\"粉嫩\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h2\\/M0C\\/29\\/E3\\/8vobAFz_qCaIN2LXAAG7v2-c1g8AAJlkgKCaP8AAbvX452.png\",\"resourceName\":\"lut_babypink.png\"}," +
        "{\"defaultStrength\":80,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g2\\/M00\\/06\\/F5\\/iwVsD1wOEOyITBzQAAAtz6xWgYkAAbx0QD2piEAAC3n699.png\",\"id\":\"1_2\",\"tob_id\":\"7\",\"name\":\"清晰\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g2\\/M04\\/07\\/08\\/nXfpA1wPOIeIV-CWAAN-DmbfsQQAAb4egEwcigAA34m770.png\",\"resourceName\":\"lutimage_modern.png\"}," +
        "{\"defaultStrength\":80,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g2\\/M05\\/06\\/F5\\/iwVsD1wOEOyIMbuaAAAuLVrXvpYAAbxzwGwH_8AAC5F862.png\",\"id\":\"1_3\",\"tob_id\":\"8\",\"name\":\"自然\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g1\\/M00\\/11\\/F0\\/LXz8F1wPOd2IaFjBAALh0WE7HfgAAiGIQGSytgAAuHp032.png\",\"resourceName\":\"lutimage_original.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g2\\/M0B\\/06\\/F5\\/iwVsD1wOEOuIdzPbAAAszEbW178AAbx0wDM0sMAACzk084.png\",\"id\":\"1_4_v2\",\"tob_id\":\"9\",\"name\":\"櫻桃\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h2\\/M0E\\/36\\/63\\/8PobAFz_qCeIHcihAAIL98A0aXUAAKKogIqVrYAAgwP827.png\",\"resourceName\":\"lut_cherry.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g1\\/M0A\\/11\\/DD\\/nXfpBFwOEOuIOH3DAAAm9H9Ua7UAAh_bAOqpYEAACcM327.png\",\"id\":\"1_5_v2\",\"tob_id\":\"10\",\"name\":\"神秘\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h3\\/M0E\\/35\\/6E\\/LvsbAFz_qCaIEZx9AABop6-aKpsAAKFQAHnsGcAAGi_598.png\",\"resourceName\":\"lut_danube_new.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g2\\/M09\\/06\\/F5\\/nXfpA1wOEO2IRpbqAAAngqJLqCsAAbx0gDS7BsAACea442.png\",\"id\":\"1_6_v2\",\"tob_id\":\"11\",\"name\":\"暖阳\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h2\\/M04\\/29\\/EB\\/8vobAFz_qhmIU04UAANuuS-YvvMAAJl7AC2m1IAA27R828.png\",\"resourceName\":\"lut_a6.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\//\\giftesx.bigo.sg\\/live\\/g2\\/M0A\\/06\\/F5\\/iwVsD1wOEOyIKZ-rAAAnC2ZtEnwAAbxywP-mXwAACcj278.png\",\"id\":\"1_7_v2\",\"tob_id\":\"12\",\"name\":\"樱花\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h1\\/M0B\\/A0\\/06\\/svobAFz_qCyIaAr7AANuOVVuru8ABEPCAGj3d0AA25R474.png\",\"resourceName\":\"lut_sakura.png\"}," +
        "{\"defaultStrength\":70,\"filterType\":1,\"icon\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/g2\\/M05\\/06\\/F5\\/iwVsD1wOEO2IMld3AAAneS0weqsAAbxzwGwTkQAACeR346.png\",\"id\":\"1_8_v2\",\"tob_id\":\"13\",\"name\":\"情书\",\"resource\":\"https:\\/\\/giftesx.bigo.sg\\/live\\/7h1\\/M07\\/45\\/EF\\/s_obAFz_qCyIPpiXAAMrh6Pg5DwABL9wwD7-zYAAyuf696.png\",\"resourceName\":\"lut_se_new.png\"}]"


val CONFIG_WHITE_SKIN_FILTER = """
    {"defaultStrength":70,"filterType":1,"icon":"https://giftesx.bigo.sg/live/7h1/M06/83/F8/tPobAFyLaqGIaV4VAABHBQ-awDAAAJG0wDcLTAAAEcd595.png","id":"1_9","tob_id":"lut_beauty","name":"經典","resource":"https://giftesx.bigo.sg/live/g1/M01/11/F2/iwVsDlwPXPeIZ5R8AAwAaIiYBKAAAiGzAPO6ZIADACA668.png","resourceName":"lutimage_classic.png"}
""".trimIndent()


object FilterUtils {
    private const val TAG = "FilterUtils"

    val whiteSkinFilter :FilterInfo by lazy {
        FilterInfo.prase(CONFIG_WHITE_SKIN_FILTER)
    }

    val allFilters :List<FilterInfo> by lazy {
        prase()
    }

    fun prase(): List<FilterInfo> {
        val filters = mutableListOf<FilterInfo>()
        val jsonArr = JSONArray(CONFIG_FILTER_LIST);
        for (i in 0 until jsonArr.length()) {
            filters.add(FilterInfo.prase(jsonArr[i].toString()))
        }
        return filters
    }
}