package sg.bigo.common.view

import android.graphics.Rect

class MultiLiveLayoutPolicy: IOnLayoutPolicy {
    override fun split(rect: Rect, c: Int): List<Rect> {
        val result = mutableListOf<Rect>()
        val totalRow = getRow(c)
        val totalCol = getColumn(c)
        val micWidth = (rect.width()) / totalCol
        val micHeight = (rect.height()) / totalRow

        for (i in 0 until c) {
            val row = i / totalCol
            val col = i % totalCol
            var left = col * micWidth + rect.left
            val top = row * micHeight + rect.top
            result.add(Rect(left,top,left + micWidth,top + micHeight))
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