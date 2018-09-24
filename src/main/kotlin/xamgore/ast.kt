package xamgore

interface Node {
    fun accept(visitor: Visitor)
}


data class Block(val statements: List<Statement>) : Node {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

interface Statement : Node


interface Expression : Statement

data class FunctionCall(val id: Token, val args: List<Expression>) : Expression {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

data class Literal(val number: Token) : Expression {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

data class Identifier(val id: Token) : Expression {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

data class BinaryExpression(val op: Token, val left: Expression, val right: Expression) : Expression {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}


data class Binding(val id: Token, val expr: Expression?) : Statement {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

data class Assignment(val id: Token, val expr: Expression) : Statement {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

data class While(val clause: Expression, val body: Block) : Statement {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

data class If(val clause: Expression, val then: Block, val `else`: Block?) : Statement {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

data class Function(val id: Token, val params: List<Token>, val body: Block) : Statement {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}

data class Return(val expr: Expression) : Statement {
    override fun accept(visitor: Visitor) {
        visitor.visit(this)
    }
}
