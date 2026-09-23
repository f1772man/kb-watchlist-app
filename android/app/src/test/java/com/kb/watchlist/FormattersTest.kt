package com.kb.watchlist

import com.kb.watchlist.util.Formatters
import org.junit.Assert.assertEquals
import org.junit.Test

class FormattersTest {

    @Test
    fun testFormatPrice() {
        assertEquals("74,500원", Formatters.formatPrice(74500L))
        assertEquals("0원", Formatters.formatPrice(0L))
        assertEquals("1,234,567원", Formatters.formatPrice(1234567L))
    }

    @Test
    fun testFormatChange() {
        assertEquals("+1,200", Formatters.formatChange(1200L, "+"))
        assertEquals("-500", Formatters.formatChange(500L, "-"))
        assertEquals("0", Formatters.formatChange(0L, "0"))
    }

    @Test
    fun testFormatChangeRate() {
        assertEquals("+1.64%", Formatters.formatChangeRate(1.64, "+"))
        assertEquals("-0.72%", Formatters.formatChangeRate(0.72, "-"))
        assertEquals("0.00%", Formatters.formatChangeRate(0.0, "0"))
    }

    @Test
    fun testFormatVolume() {
        assertEquals("12,345,678주", Formatters.formatVolume(12345678L))
    }
}
