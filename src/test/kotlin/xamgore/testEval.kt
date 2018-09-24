package xamgore

import org.junit.Assert.assertEquals
import org.junit.Test
import xamgore.visitors.Eval

class TestEval {
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

        val expected = "0"
        val sb = StringBuilder()
        val printLn: (Any?) -> Unit = getPrintLn(sb)

        val evaluator = Eval(printLn)
        Parser(Lexer(source)).parse().accept(evaluator)
        assertEquals(expected.trim(), sb.toString().trim())
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
            1 1
            2 2
            3 3
            4 5
            5 8
        """.trimIndent()

        val sb = StringBuilder()
        val evaluator = Eval(getPrintLn(sb))
        Parser(Lexer(source)).parse().accept(evaluator)
        assertEquals(expected.trim(), sb.toString().trim())
    }

    @Test
    fun `functions are available as soon as been defined`() {
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
            42
        """.trimIndent()

        val sb = StringBuilder()
        val evaluator = Eval(getPrintLn(sb))
        Parser(Lexer(source)).parse().accept(evaluator)
        assertEquals(expected.trim(), sb.toString().trim())
    }

    @Test
    fun `function returns 0 by default`() {
        val source = """
            fun foo() { }
            println(foo()) // prints 0
        """.trimIndent()

        val expected = """
            0
        """.trimIndent()

        val sb = StringBuilder()
        val evaluator = Eval(getPrintLn(sb))
        Parser(Lexer(source)).parse().accept(evaluator)
        assertEquals(expected.trim(), sb.toString().trim())
    }

    fun getPrintLn(sb: StringBuilder): (Any?) -> Unit =
        { sb.appendln(it.toString()) }
}
