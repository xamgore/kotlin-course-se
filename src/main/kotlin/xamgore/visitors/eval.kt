package xamgore.visitors

import xamgore.*
import xamgore.Function
import java.util.LinkedHashMap

class Context<T>(private val parent: Context<T>?) {
    private val funs = LinkedHashMap<String, Function>()
    private val vars = LinkedHashMap<String, T?>()

    fun setFun(f: Function) {
        val id = f.id.value

        if (id in funs)
            throw Exception("Overloading is not allowed." +
                "Function $id on line ${f.id.row} rewrites ${funs[id]?.id}")

        funs[id] = f
    }

    fun setVar(id: String, value: T?) {
        vars[id] = value
    }

    fun getFun(id: String): Function =
        funs.getOrElse(id) { parent?.getFun(id) ?: throw Exception("Call of undefined function $id") }

    fun getVar(id: String): T? =
        vars.getOrElse(id) { parent?.getVar(id) ?: throw Exception("Usage of undefined variable $id") }
}

class Eval(val printLn: (Any?) -> Unit = ::println) : Visitor {
    private val println = Function(Token(Type.ID, "println"), emptyList(), Block(emptyList()))
    private var ctx = newCtx(null)
    private var result: Int? = null


    private fun newCtx(parent: Context<Int>?): Context<Int> {
        val ctx = Context(parent)
        ctx.setFun(println)
        return ctx
    }

    override fun visit(s: Block) {
        for (st in s.statements) {
            st.accept(this)

            val isBreakableCode = st is If || st is While || st is Return
            val hasReturnInside = result != null
            if (isBreakableCode && hasReturnInside)
                break
        }
    }

    override fun visit(s: Literal) {
        result = s.number.value.toInt()
    }

    override fun visit(s: Identifier) {
        result = ctx.getVar(s.id.value)
    }

    private fun Int.toBool() = this != 0
    private fun Boolean.toInt() = if (this) 1 else 0

    override fun visit(s: BinaryExpression) {
        s.left.accept(this)
        val left = result

        s.right.accept(this)
        val right = result

        result = null
        if (left == null || right == null)
            return

        result = when (s.op.type) {
            Type.`&&` -> (left.toBool() && right.toBool()).toInt()
            Type.`||` -> (left.toBool() || right.toBool()).toInt()
            Type.`==` -> (left == right).toInt()
            Type.`!=` -> (left != right).toInt()
            Type.`*` -> left * right
            Type.`+` -> left + right
            Type.`-` -> left - right
            Type.`%` -> left % right
            Type.DIV -> left / right
            Type.LESS -> (left < right).toInt()
            Type.LESS_EQ -> (left <= right).toInt()
            Type.GREATER -> (left > right).toInt()
            Type.GREATER_EQ -> (left >= right).toInt()
            else -> null
        }
    }

    override fun visit(s: FunctionCall) {
        val id = s.id.value
        val func = ctx.getFun(id)

        if (func === this.println)
            return this.printExpressions(s.args)

        if (func.params.size != s.args.size)
            throw Exception("Number of actual arguments differs from defined in function $id")

        val outerCtx = ctx
        val innerCtx = newCtx(parent = outerCtx)

        func.params.zip(s.args) { param, arg ->
            arg.accept(this)
            innerCtx.setVar(param.value, result)
            result = null
        }

        ctx = innerCtx
        func.body.accept(this)
        result = result ?: 0
        ctx = outerCtx
    }

    private fun printExpressions(args: List<Expression>) {
        printLn(args.map {
            it.accept(this)
            val res = result
            result = null
            res
        }.joinToString(" "))
    }

    override fun visit(s: Assignment) {
        s.expr.accept(this)
        // check there was a declaration before
        ctx.getVar(s.id.value)
        ctx.setVar(s.id.value, result)
        result = null
    }

    override fun visit(s: Binding) {
        val id = s.id.value
        ctx.setVar(id, null)

        s.expr?.let {
            it.accept(this)
            ctx.setVar(id, result)
        }

        result = null
    }

    override fun visit(s: While) {
        while (true) {
            s.clause.accept(this)
            if (result?.toBool() == false)
                break

            s.body.accept(this)
            if (result != null) // has return inside
                return
        }

        result = null
    }

    override fun visit(s: If) {
        s.clause.accept(this)
        val isTrue = (result ?: 0).toBool()
        result = null
        val branch = if (isTrue) s.then else s.`else`
        branch?.accept(this)
    }

    override fun visit(s: Function) {
        ctx.setFun(s)
        result = null
    }

    override fun visit(s: Return) {
        s.expr.accept(this)
    }

}
