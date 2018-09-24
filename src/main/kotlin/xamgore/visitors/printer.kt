package xamgore.visitors

import xamgore.*
import xamgore.Function

class Printer : Visitor {
    private var code: String = ""
    private val indent = "    "

    fun result() = code

    override fun visit(s: Block) {
        val list = mutableListOf<String>()

        for (st in s.statements) {
            st.accept(this)
            list.add(code)
        }

        val inner = list.joinToString("\n")

//        val hasBraces = s.brace == Type.`}`
//        val before = if (hasBraces) "{\n" else ""
//        val after = if (hasBraces) "}\n" else ""

        code = inner
    }

    override fun visit(s: Literal) {
        code = s.number.value
    }

    override fun visit(s: Identifier) {
        code = s.id.value
    }

    override fun visit(s: BinaryExpression) {
        s.left.accept(this)
        val left = code

        s.right.accept(this)
        val right = code

        code = "$left ${binary(s.op.type)} $right"
    }

    private fun binary(t: Type): String =
        when (t) {
            Type.`&&` -> "&&"
            Type.`||` -> "||"
            Type.`+` -> "+"
            Type.`-` -> "-"
            Type.`*` -> "*"
            Type.`%` -> "%"
            Type.DIV -> "/"
            Type.LESS -> "<"
            Type.LESS_EQ -> "<="
            Type.GREATER -> ">"
            Type.GREATER_EQ -> ">="
            Type.`==` -> "=="
            Type.`!=` -> "!="
            else -> ""
        }

    override fun visit(s: FunctionCall) {
        val list = mutableListOf<String>()

        for (arg in s.args) {
            arg.accept(this)
            list.add(code)
        }

        val args = list.joinToString(", ")
        code = "${s.id.value}($args)"
    }

    override fun visit(s: Assignment) {
        s.expr.accept(this)
        val expr = code

        code = "${s.id.value} = $expr"
    }

    override fun visit(s: Binding) {
        code = ""
        s.expr?.let { it.accept(this); code = " = $code" }

        code = "var ${s.id.value}$code"
    }

    override fun visit(s: While) {
        s.clause.accept(this)
        val clause = code

        s.body.accept(this)
        val body = code.prependIndent(indent)

        code = "while ($clause) {\n$body\n}"
    }

    override fun visit(s: If) {
        s.clause.accept(this)
        val clause = code

        s.then.accept(this)
        val then = code.prependIndent(indent)

        var elseBranch = ""
        s.`else`?.let {
            it.accept(this)
            val res = code.prependIndent(indent)
            elseBranch = " else {\n$res\n}"
        }

        code = "if ($clause) {\n$then\n}$elseBranch"
    }

    override fun visit(s: Function) {
        val params = s.params.joinToString(", ") { it.value }

        s.body.accept(this)
        val body = code.prependIndent(indent)

        code = "fun ${s.id.value}($params) {\n$body\n}"
    }

    override fun visit(s: Return) {
        s.expr.accept(this)
        code = "return $code"
    }
}
