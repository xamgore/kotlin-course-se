package xamgore

interface Visitor {
    fun visit(s: Block)
    fun visit(s: Literal)
    fun visit(s: Identifier)
    fun visit(s: BinaryExpression)
    fun visit(s: FunctionCall)
    fun visit(s: Assignment)
    fun visit(s: Binding)
    fun visit(s: While)
    fun visit(s: If)
    fun visit(s: Function)
    fun visit(s: Return)
}
