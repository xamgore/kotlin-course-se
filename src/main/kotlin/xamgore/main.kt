package xamgore

import xamgore.visitors.Eval
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


fun main(args: Array<String>) {
    val fileName = if (args.isNotEmpty()) args[0] else return
    val input = File(fileName).readText()

    val evaluator = Eval()
    Parser(Lexer(input)).parse().accept(evaluator)
}
