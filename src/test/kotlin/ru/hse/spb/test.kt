package ru.hse.spb

import org.junit.Assert.*
import org.junit.Test

class TestTitleGetter {
    @Test
    fun `test word with the only question mark`() =
        assertEquals("a", getTitle(1, "?"))

    @Test
    fun `ok if the first and last letters are same`() =
        assertEquals("abba", getTitle(2, "a??a"))

    @Test
    fun `fails if the first and last letters differs`() =
        assertNull(getTitle(3, "a?c"))

    @Test
    fun `ok if all letters are used`() =
        assertEquals("abba", getTitle(2, "????"))

    @Test
    fun `fails if there are unused letters`() =
        assertNull(getTitle(3, "????"))

    @Test
    fun `check that letters are filled by alphabet order`() =
        assertEquals("abba", getTitle(2, "?b?a"))

    @Test
    fun `stress test`() =
        assertEquals("aaaeaaakadavbfghijlomnpqrcstuwxyyxwutscrqpnmoljihgfbvadakaaaeaaa",
            getTitle(25,
                "???e???k?d?v?????????????c??????????????????o???????????????????"))
}