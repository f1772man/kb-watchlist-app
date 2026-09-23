package com.kb.watchlist.util

import java.text.DecimalFormat
import java.util.Locale

object Formatters {

    private val numberFormat = DecimalFormat("#,###")

    fun formatPrice(price: Long?): String {
        val p = price ?: 0L
        return "${numberFormat.format(p)}원"
    }

    fun formatChange(change: Long?, sign: String?): String {
        val c = change ?: 0L
        val s = sign ?: "0"
        val formatted = numberFormat.format(kotlin.math.abs(c))
        return when (s) {
            "+" -> "+$formatted"
            "-" -> "-$formatted"
            else -> "0"
        }
    }

    fun formatChangeRate(rate: Double?, sign: String?): String {
        val r = rate ?: 0.0
        val s = sign ?: "0"
        val absRate = String.format(Locale.KOREA, "%.2f", kotlin.math.abs(r))
        return when (s) {
            "+" -> "+$absRate%"
            "-" -> "-$absRate%"
            else -> "0.00%"
        }
    }

    fun formatVolume(volume: Long?): String {
        val v = volume ?: 0L
        return "${numberFormat.format(v)}주"
    }
}
