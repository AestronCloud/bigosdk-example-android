package sg.bigo.common.view

import android.graphics.Rect

class MultiLiveLayoutPolicy: IOnLayoutPolicy {
    override fun split(rootRect: Rect, c: Int): List<Rect> {
        val result = mutableListOf<Rect>()
        val totalRow = getRow(c)
        val totalCol = getColumn(c)
        val micWidth = (rootRect.width()) / totalCol
        val micHeight = (rootRect.height()) / totalRow

        when (c) {
            2 -> {
                result.add(rootRect)
                val subW = rootRect.width()/3;
                val subH = rootRect.height()/3
                val x = rootRect.width() - subW;
                val y = rootRect.height()/6;
                result.add(Rect(x,y,x+subW,y+subH));
            }
            3 -> {
                result.add(rootRect)
                val subW = rootRect.width()/3;
                val subH = rootRect.height()/3
                val x = rootRect.width() - subW;
                val y = rootRect.height()/6;
                result.add(Rect(x,y,x+subW,y+subH));
                result.add(Rect(x,3*y,x+subW,3*y+subH));
            }
            else -> {
                for (i in 0 until c) {
                    val row = i / totalCol
                    val col = i % totalCol
                    var left = col * micWidth + rootRect.left
                    val top = row * micHeight + rootRect.top
                    result.add(Rect(left,top,left + micWidth,top + micHeight))
                }
            }
        }

        return result
    }

    override fun getRow(c: Int) = when {
        c <= 2 -> c
        else -> (c + 1) / 2
    }

    override fun getColumn(c: Int) = when {
        c <= 2 -> 1
        else -> 2
    }
}