package xamgore


class Parser(private val lexer: Lexer) {

    private val tokens = lexer.filter { it.type != Type.COMMENT }.toList()
    private var pos: Int = 0

    private val EOF = Token(Type.EOF)

    private val type: Type
        get() = this[0].type

    fun parse(): Node = block(Type.EOF)

    // BLOCK = (STATEMENT)*
    private fun block(end: Type) = Block(consumeUntil(end, ::statement))

    // BLOCK_WITH_BRACES = "{" BLOCK "}"
    private fun blockWithBraces(): Block {
        consume(Type.`{`)
        val body = block(Type.`}`)
        consume(Type.`}`)
        return body
    }

    // STATEMENT = FUNCTION | VARIABLE | EXPRESSION | WHILE | IF | ASSIGNMENT | RETURN
    private fun statement(): Statement =
        when (type) {
            Type.FUN ->
                function()
            Type.VAR ->
                variable()
            Type.WHILE ->
                `while`()
            Type.IF ->
                `if`()
            Type.RETURN ->
                `return`()
            else -> when (this[1].type) {
                Type.`=` -> assignment()  // has `=` at the second position
                else -> expression()
            }
        }

    // FUNCTION = "fun" IDENTIFIER "(" PARAMETER_NAMES ")" BLOCK_WITH_BRACES
    private fun function(): Function {
        consume(Type.FUN)
        val id = consume(Type.ID)

        consume(Type.`(`)
        val params = parameterNames()
        consume(Type.`)`)

        val body = blockWithBraces()
        return Function(id, params, body)
    }

    // PARAMETER_NAMES = IDENTIFIER{,}
    private fun parameterNames(): List<Token> =
        consumeUntil(Type.`)`) {
            val arg = consume(Type.ID)
            match(Type.`,`)
            return@consumeUntil arg
        }

    // VARIABLE = "var" IDENTIFIER ("=" EXPRESSION)?
    private fun variable(): Binding {
        consume(Type.VAR)
        val id = consume(Type.ID)
        val expr = if (match(Type.`=`)) expression() else null
        return Binding(id, expr)
    }

    // EXPRESSION = FUNCTION_CALL | BINARY_EXPRESSION | IDENTIFIER | LITERAL | "(" EXPRESSION ")"
    private fun expression(): Expression = binaryLogicalOr()

    private fun binaryLogicalOr(): Expression =
        binary(listOf(Type.`||`), ::binaryLogicalAnd)

    private fun binaryLogicalAnd(): Expression =
        binary(listOf(Type.`&&`), ::binaryEquality)

    private fun binaryEquality(): Expression =
        binary(listOf(Type.`==`, Type.`!=`), ::binaryCompare)

    private fun binaryCompare(): Expression =
        binary(listOf(Type.LESS, Type.LESS_EQ, Type.GREATER, Type.GREATER_EQ), ::binaryAdd)

    private fun binaryAdd(): Expression =
        binary(listOf(Type.`+`, Type.`-`), ::binaryMult)

    private fun binaryMult(): Expression =
        binary(listOf(Type.`*`, Type.`%`, Type.DIV), ::operand)

    private fun operand(): Expression {
        // ( expr )
        if (match(Type.`(`)) {
            val expr = expression()
            consume(Type.`)`)
            return expr
        }

        // func(..)
        if (type == Type.ID && this[1].type == Type.`(`)
            return functionCall()

        if (type == Type.INT)
            return Literal(consume(Type.INT))
        if (type == Type.ID)
            return Identifier(consume(Type.ID))

        syntaxError("Unexpected token")
    }

    private fun binary(operators: List<Type>, getOperand: () -> Expression): Expression {
        val leftOperand = getOperand()

        if (type in operators)
            return BinaryExpression(op = consume(), left = leftOperand, right = getOperand())

        return leftOperand
    }

    // FUNCTION_CALL = IDENTIFIER "(" ARGUMENTS ")"
    private fun functionCall(): FunctionCall {
        val id = consume(Type.ID)

        consume(Type.`(`)
        val args = arguments()
        consume(Type.`)`)

        return FunctionCall(id, args)
    }

    // ARGUMENTS = EXPRESSION{","}
    private fun arguments(): List<Expression> =
        consumeUntil(Type.`)`) {
            val arg = expression()
            match(Type.`,`)
            return@consumeUntil arg
        }

    // WHILE = "while" "(" EXPRESSION ")" BLOCK_WITH_BRACES
    private fun `while`(): While {
        consume(Type.WHILE)

        consume(Type.`(`)
        val clause = expression()
        consume(Type.`)`)

        val body = blockWithBraces()
        return While(clause, body)
    }

    // IF = "if" "(" EXPRESSION ")" BLOCK_WITH_BRACES ("else" BLOCK_WITH_BRACES)?
    private fun `if`(): If {
        consume(Type.IF)

        consume(Type.`(`)
        val clause = expression()
        consume(Type.`)`)

        val then = blockWithBraces()
        val `else` = if (match(Type.ELSE)) blockWithBraces() else null
        return If(clause, then, `else`)
    }

    // RETURN = "return" EXPRESSION
    private fun `return`(): Return {
        consume(Type.RETURN)
        return Return(expression())
    }

    // ASSIGNMENT = IDENTIFIER "=" EXPRESSION
    private fun assignment(): Assignment {
        val id = consume(Type.ID)
        consume(Type.`=`)
        val expr = expression()
        return Assignment(id, expr)
    }

    private operator fun get(relativePosition: Int): Token {
        val position = pos + relativePosition
        return if (position >= tokens.size) EOF else tokens[position]
    }

    private fun consume(): Token {
        val tok = get(0)
        pos++
        return tok
    }

    private fun consume(type: Type): Token {
        val tok = consume()
        if (tok.type != type)
            syntaxError("Expected $type")
        return tok
    }

    private fun match(type: Type): Boolean {
        val matched = get(0).type === type
        if (matched) pos++
        return matched
    }

    private fun <T> consumeUntil(until: Type, supplier: () -> T): List<T> {
        val list = mutableListOf<T>()
        while (get(0).type != until)
            list.add(supplier())
        return list
    }

    fun syntaxError(errMsg: String = ""): Nothing {
        val token = this[0]
        val row = token.row
        val col = token.col

        val leftPad = if (col <= 1) 0 else col - 1
        val spaces = StringBuilder("~").repeat(leftPad)
        val source = lexer.input.split(lexer.EOL)[row]

        println("\n${row+1}:${col+1} $errMsg\n$source\n$spaces^\n")
        throw Exception()
    }
}
