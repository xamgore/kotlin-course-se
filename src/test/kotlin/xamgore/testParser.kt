package xamgore

import org.junit.Assert.assertEquals
import org.junit.Test
import xamgore.visitors.Printer

class TestParser {
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

        val expected = """
            var a = 10
            var b = 20
            if (a > b) {
                println(1)
            } else {
                println(0)
            }
        """.trimIndent()

        val p = Printer()
        Parser(Lexer(source)).parse().accept(p)
        assertEquals(expected, p.result())
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

        val expected = """
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

        val p = Printer()
        Parser(Lexer(source)).parse().accept(p)
        assertEquals(expected, p.result())
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

        val expected = """
            fun foo(n) {
                fun bar(m) {
                    return m + n
                }
                return bar(1)
            }
            println(foo(41))
        """.trimIndent()

        val p = Printer()
        Parser(Lexer(source)).parse().accept(p)
        assertEquals(expected, p.result())
    }

}
