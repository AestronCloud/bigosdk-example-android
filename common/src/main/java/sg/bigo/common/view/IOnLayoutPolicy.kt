package sg.bigo.common.view

import android.graphics.Rect

interface IOnLayoutPolicy {
    fun split(rect: Rect, c: Int): List<Rect>
    fun getRow(c: Int): Int
    fun getColumn(c: Int): Int
}