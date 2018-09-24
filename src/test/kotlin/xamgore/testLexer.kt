package xamgore

import org.junit.Assert.assertEquals
import org.junit.Test
import xamgore.Type.*

class TestLexer {
    @Test
    fun testExample1() {
        val source = """
            // example 1
            var a = 10
            var b = 20
            if (a > b) {
                println(1)
            } else {
                println(0)
            }
        """.trimIndent()

        val expected = listOf(Token(COMMENT, ""), Token(VAR, ""), Token(ID, "a"), Token(`=`, ""), Token(INT, "10"), Token(VAR, ""), Token(ID, "b"), Token(`=`, ""), Token(INT, "20"), Token(IF, ""), Token(`(`, ""), Token(ID, "a"), Token(GREATER, ""), Token(ID, "b"), Token(`)`, ""), Token(`{`, ""), Token(ID, "println"), Token(`(`, ""), Token(INT, "1"), Token(`)`, ""), Token(`}`, ""), Token(ELSE, ""), Token(`{`, ""), Token(ID, "println"), Token(`(`, ""), Token(INT, "0"), Token(`)`, ""), Token(`}`, ""), Token(EOF, ""))

        assertEquals(
            expected.joinToString(" "),
            Lexer(source).joinToString(" "))
    }

    @Test
    fun testExample2() {
        val source = """
            // example 2
            fun fib(n) {
                if (n <= 1) {
                    return 1
                }
                return fib(n - 1) + fib(n - 2)
            }

            var i = 1
            while (i <= 5) {
                println(i, fib(i))
                i = i + 1
            }
        """.trimIndent()

        val expected = listOf(Token(COMMENT, ""), Token(FUN, ""), Token(ID, "fib"), Token(`(`, ""), Token(ID, "n"), Token(`)`, ""), Token(`{`, ""), Token(IF, ""), Token(`(`, ""), Token(ID, "n"), Token(LESS_EQ, ""), Token(INT, "1"), Token(`)`, ""), Token(`{`, ""), Token(RETURN, ""), Token(INT, "1"), Token(`}`, ""), Token(RETURN, ""), Token(ID, "fib"), Token(`(`, ""), Token(ID, "n"), Token(`-`, ""), Token(INT, "1"), Token(`)`, ""), Token(`+`, ""), Token(ID, "fib"), Token(`(`, ""), Token(ID, "n"), Token(`-`, ""), Token(INT, "2"), Token(`)`, ""), Token(`}`, ""), Token(VAR, ""), Token(ID, "i"), Token(`=`, ""), Token(INT, "1"), Token(WHILE, ""), Token(`(`, ""), Token(ID, "i"), Token(LESS_EQ, ""), Token(INT, "5"), Token(`)`, ""), Token(`{`, ""), Token(ID, "println"), Token(`(`, ""), Token(ID, "i"), Token(`,`, ""), Token(ID, "fib"), Token(`(`, ""), Token(ID, "i"), Token(`)`, ""), Token(`)`, ""), Token(ID, "i"), Token(`=`, ""), Token(ID, "i"), Token(`+`, ""), Token(INT, "1"), Token(`}`, ""), Token(EOF, ""))

        assertEquals(
            expected.joinToString(" "),
            Lexer(source).joinToString(" "))
    }

    @Test
    fun testExample3() {
        val source = """
            // example 3
            fun foo(n) {
                fun bar(m) {
                    return m + n
                }

                return bar(1)
            }

            println(foo(41)) // prints 42
        """.trimIndent()

        val expected = listOf(Token(COMMENT, ""), Token(FUN, ""), Token(ID, "foo"), Token(`(`, ""), Token(ID, "n"), Token(`)`, ""), Token(`{`, ""), Token(FUN, ""), Token(ID, "bar"), Token(`(`, ""), Token(ID, "m"), Token(`)`, ""), Token(`{`, ""), Token(RETURN, ""), Token(ID, "m"), Token(`+`, ""), Token(ID, "n"), Token(`}`, ""), Token(RETURN, ""), Token(ID, "bar"), Token(`(`, ""), Token(INT, "1"), Token(`)`, ""), Token(`}`, ""), Token(ID, "println"), Token(`(`, ""), Token(ID, "foo"), Token(`(`, ""), Token(INT, "41"), Token(`)`, ""), Token(`)`, ""), Token(COMMENT, ""), Token(EOF, ""))

        assertEquals(
            expected.joinToString(" "),
            Lexer(source).joinToString(" "))
    }

}
