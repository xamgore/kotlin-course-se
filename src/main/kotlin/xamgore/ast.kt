package xamgore

sealed class Node {
    open fun accept(visitor: Visitor) {}
}


class Block(val statements: List<Statement>) : Node() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

open class Statement : Node()


open class Expression : Statement()

class FunctionCall(val id: Token, val args: List<Expression>) : Expression() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

class Literal(val number: Token) : Expression() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

class Identifier(val id: Token) : Expression() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

class BinaryExpression(val op: Token, val left: Expression, val right: Expression) : Expression() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}


class Binding(val id: Token, val expr: Expression?) : Statement() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

class Assignment(val id: Token, val expr: Expression) : Statement() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

class While(val clause: Expression, val body: Block) : Statement() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

class If(val clause: Expression, val then: Block, val `else`: Block?) : Statement() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

class Function(val id: Token, val params: List<Token>, val body: Block) : Statement() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

class Return(val expr: Expression) : Statement() {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}
