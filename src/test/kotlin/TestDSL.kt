import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

class TestDSL {

    fun Tex.str() = ByteArrayOutputStream().also(this::toOutputStream).toString().trim()

    @Test
    fun `test doc structure`() {
        assertEquals("\\documentclass{beamer}",
            document {
                documentClass("beamer")
            }.str())
    }

    @Test
    fun `test use packages`() {
        assertEquals("""
            \usepackage[]{kek}
            \usepackage[1]{kek}
            \usepackage[1,2]{kek}
        """.trimIndent(),
            document {
                usepackage("kek")
                usepackage("kek", "1")
                usepackage("kek", "1", "2")
            }.str())
    }

    @Test
    fun `test frame`() {
        assertEquals("""
            \begin{frame}[]
            \frametitle{kek}
            \end{frame}
        """.trimIndent(),
            document {
                frame("kek") {}
            }.str())
    }

    @Test
    fun `test frame var args`() {
        assertEquals("""
            \begin{frame}[1=2,3=4]
            \frametitle{kek}
            \end{frame}
        """.trimIndent(),
            document {
                frame("kek", "1" to "2", "3" to "4") {}
            }.str())
    }

    @Test
    fun `test frame body`() {
        assertEquals("""
            \begin{frame}[]
            \frametitle{kek}
            body
            \end{frame}
        """.trimIndent(),
            document {
                frame("kek") {
                    +"body"
                }
            }.str())
    }


    @Test
    fun `test itemize`() {
        assertEquals("""
            \begin{itemize}
            \item
            1

            \item
            2

            \item
            3

            \end{itemize}
        """.trimIndent(),
            document {
                itemize {
                    for (row in 1..3) {
                        item {
                            +row.toString()

                        }

                    }
                }
            }.str())
    }

    @Test
    fun `test alignment`() {
        assertEquals("""
            \begin{flushleft}

            \end{flushleft}
            \begin{flushright}

            \end{flushright}
            \begin{flushcenter}

            \end{flushcenter}
        """.trimIndent(),
        document {
            for (row in 1..3) {
                val align = ALIGN.values()[row - 1]
                alignment(align) {
                    +""
                }
            }
        }.str())
    }

    @Test
    fun `test full example`() {
        assertEquals("""
            \documentclass{beamer}
            \usepackage[russian,english]{babel}
            \begin{frame}[arg1=arg2,arg3=arg4]
            \frametitle{frametitle}
            \begin{itemize}
            \item
            \begin{flushleft}
            ${'$'}${'$'}\Pi \eq 1.14${'$'}${'$'}
            1 text flushleft
            \end{flushleft}

            \item
            \begin{flushright}
            ${'$'}${'$'}\Pi \eq 2.14${'$'}${'$'}
            2 text flushright
            \end{flushright}

            \item
            \begin{flushcenter}
            ${'$'}${'$'}\Pi \eq 3.14${'$'}${'$'}
            3 text flushcenter
            \end{flushcenter}

            \end{itemize}
            \end{frame}
            \begin{pyglist}[language=kotlin]
            |val a = 1
            |
            \end{pyglist}
        """.trimIndent(),
        document {
            documentClass("beamer")
            usepackage("babel", "russian", "english")
            frame("frametitle", "arg1" to "arg2", "arg3" to "arg4") {
                itemize {
                    for (row in 1..3) {
                        item {
                            val align = ALIGN.values()[row - 1]
                            alignment(align) {
                                math("""\Pi \eq $row.14""")
                                +"$row text $align"
                            }
                        }

                    }
                }
            }

            // begin{pyglist}[language=kotlin]...\end{pyglist}
            customTag("pyglist", "language" to "kotlin") {
                +"""
                |val a = 1
                |
            """
            }
        }.str())
    }

}
