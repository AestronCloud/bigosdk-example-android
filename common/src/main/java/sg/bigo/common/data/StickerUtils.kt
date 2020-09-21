package sg.bigo.common.data



val stickersString = """
                        是	1	ok	1536225842	功夫熊	    自研	是	https://bigf.bigo.sg/asia_live/3h3/0d2qUY.zip	20180106193743610696672	0	101	0	null
                        是	2	ok	1527734206	小可爱	    自研	是	https://giftesx.bigo.sg/live/7h3/M03/10/0E/LvsbAFzonQ2Id_L5AAAxawzC8BYAADxxgOPWrUAADGD770.zip	20180301173326472535203	0	101	0	null
                        是	3	ok	1527738913	泰迪熊    	自研	是	https://bigf.bigo.sg/asia_live/3h3/00PDrT.zip	20180411180806892173593	0	101	0	null
                        是	4	ok	1533622507	小黑猫	    自研	是	https://giftesx.bigo.sg/live/7h2/M0E/10/BD/8vobAFzonciIQB21AADzd6djB_EAAD3_AOMpkEAAPOP424.zip	20171024112939064333917	0	101	0	null
                        是	5	ok	1533613016	亮粉猫	    自研	是	https://giftesx.bigo.sg/live/7h3/M02/10/68/MPsbAFzon8-IIqocAB8UxR4lPP4AADuWAETmMcAHxTd904.zip	20171103202501713414433	0	101	0	null
                        是	6	ok	1532664513	小熊猫	    自研	是	https://giftesx.bigo.sg/live/7h1/M02/3E/1D/s_obAFzonbCICSrVACRCsEgUnakABJ5iQFrc4EAJELI995.zip	20180315153029553522478	0	101	0	null
                        是	7	ok	1527907014	爱心桃	    自研	是	https://bigf.bigo.sg/asia_live/3h4/0RWJesv.zip	20180213093012984162072	0	101	0	null
                        是	8	ok	1532600708	比心     	自研	是	https://bigf.bigo.sg/asia_live/3h4/016tESa.zip                                               	20180205114924945578917	0	101	0	null
                        是	9	ok	1533622051	小熊发夹	    自研	是	https://giftesx.bigo.sg/live/7h3/M08/14/1D/LvsbAFzrTV-ICvt1AAXCD_JYTgIAAEvzgPf54gABcIn217.zip	20180531110852703184693	0	101	0	null
                        是	10	ok	1533622264	粉红咩咩	    自研	是	https://giftesx.bigo.sg/live/7h2/M0A/15/EF/8fobAFzrRsuIVLrkAAj3pVM5b9gAAEwbQGIWPcACPe9470.zip	20180605202355993962414	0	101	0	null
                """.trimIndent()




object StickerUtils {

    fun prase(): List<StickerInfo> {
        val stickers = stickersString.split("\n")
        val stickerList = mutableListOf<StickerInfo>()
        for (sticker in stickers) {
//            val stickerInfo = StickerInfo.prase(sticker)
            stickerList.add(StickerInfo.prase(sticker))
//            println("====>>>>${stickerInfo}")
        }

        return stickerList
    }
}