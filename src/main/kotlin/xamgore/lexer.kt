package xamgore


private operator fun StringBuilder.plusAssign(b: Any) {
    this.append(b)
}

enum class Type {
    EOF, ID, INT, COMMENT,
    `{`, `}`, FUN, RETURN, `,`,
    `(`, `)`, VAR, `=`,
    WHILE, IF, ELSE,
    `+`, `-`, `*`, `%`, DIV,
    LESS, LESS_EQ, GREATER, GREATER_EQ,
    `==`, `!=`,
    `||`, `&&`,
}

class Token(val type: Type, val value: String = "", val row: Int = 0, val col: Int = 0) {
    override fun toString(): String = "$type "

    override fun equals(other: Any?): Boolean {
        return other != null && other is Token && type == other.type && value == other.value
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}


class Lexer(val input: String) : Iterable<Token> {
    private val keywords = mapOf(
        Pair("fun", Type.FUN),
        Pair("return", Type.RETURN),
        Pair("var", Type.VAR),
        Pair("while", Type.WHILE),
        Pair("if", Type.IF),
        Pair("else", Type.ELSE)
    )

    private var row = 0
    private var col = 0
    private var pos = 0
    private var ch: Char = ' '
    private val EOF = 0.toChar()
    val EOL = '\n'

    private var currentToken: Token = createToken(Type.COMMENT)


    override fun iterator(): Iterator<Token> = object : Iterator<Token> {
        override fun hasNext(): Boolean = currentToken.type != Type.EOF
        override fun next(): Token = nextToken()
    }

    private fun lexError(message: String = "") {
        val leftPad = if (col <= 1) 0 else col - 2
        val spaces = StringBuilder("~").repeat(leftPad)
        val source = input.split(EOL)[row]

        println("\n$row:$col $message\n$source\n$spaces^\n")
        throw Exception()
    }

    private fun nextChar(): Char {
        if (pos >= input.length) {
            ch = EOF
            return ch
        }

        ch = input[pos++]

        if (ch == EOL) {
            col = 1; row++
        } else {
            col++
        }

        return ch
    }

    private fun skipSpaces() {
        while (ch.isWhitespace())
            nextChar()
    }

    private fun skipLine() {
        while (ch != EOL && ch != EOF)
            nextChar()
    }

    private fun readNextCharAndReturn(t: Type): Token {
        ch = nextChar()
        return createToken(t)
    }

    private fun readTokenAndReturn(t: Type): Token {
        for (tokChar in t.toString()) {
            ch = nextChar()
            if (ch != tokChar)
                lexError("Expected operator ($t)")
        }

        return createToken(t)
    }

    private fun tryReadMoreAndReturn(more: Char, ok: Type, fail: Type): Token {
        ch = nextChar()

        if (ch == more) {
            ch = nextChar()
            return createToken(ok)
        }

        return createToken(fail)
    }

    private fun readCommentaryOrDivision(): Token {
        ch = nextChar()

        if (ch == '/') {
            skipLine()
            return createToken(Type.COMMENT)
        }

        ch = nextChar()
        return createToken(Type.DIV)
    }

    private fun readKeywordOrIdentifier(): Token {
        val seq = StringBuilder()

        while (ch.toLowerCase() in 'a'..'z' || ch in '0'..'9' || ch == '_') {
            seq += ch; ch = nextChar()
        }

        val word = seq.toString()
        val type = keywords[word] ?: Type.ID
        return createToken(type, word)
    }

    private fun readNumberWithoutLeadingZeros(): Token {
        val seq = StringBuilder()

        while (ch.isDigit()) {
            seq += ch
            ch = nextChar()
        }

        if (seq.length > 1 && seq[0] == '0')
            lexError("Leading zeros are not allowed")

        return Token(Type.INT, seq.toString())
    }

    private fun nextToken(): Token {
        skipSpaces()

        currentToken = when (ch) {
            ',' -> readNextCharAndReturn(Type.`,`)
            '{' -> readNextCharAndReturn(Type.`{`)
            '}' -> readNextCharAndReturn(Type.`}`)
            '(' -> readNextCharAndReturn(Type.`(`)
            ')' -> readNextCharAndReturn(Type.`)`)
            '+' -> readNextCharAndReturn(Type.`+`)
            '-' -> readNextCharAndReturn(Type.`-`)
            '*' -> readNextCharAndReturn(Type.`*`)
            '%' -> readNextCharAndReturn(Type.`%`)

            '!' -> readTokenAndReturn(Type.`!=`)
            '&' -> readTokenAndReturn(Type.`&&`)
            '|' -> readTokenAndReturn(Type.`||`)

            '=' -> tryReadMoreAndReturn('=', Type.`==`, Type.`=`)
            '<' -> tryReadMoreAndReturn('=', Type.LESS_EQ, Type.LESS)
            '>' -> tryReadMoreAndReturn('=', Type.GREATER_EQ, Type.GREATER)

            '/' -> readCommentaryOrDivision()

            in '0'..'9' -> readNumberWithoutLeadingZeros()
            in 'a'..'z', in 'A'..'Z', '_' -> readKeywordOrIdentifier()

            EOF -> createToken(Type.EOF)

            else -> {
                lexError("Unexpected symbol: $ch")
                return createToken(Type.EOF)
            }
        }

        return currentToken
    }

    private fun createToken(type: Type, value: String = "") =
        Token(type, value, row, col)
}
