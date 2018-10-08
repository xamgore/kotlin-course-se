import java.io.OutputStream

typealias print = String.() -> Unit

enum class ALIGN {
    Left {
        override fun toString(): String = "flushleft"
    },
    Right {
        override fun toString(): String = "flushright"
    },
    Center {
        override fun toString(): String = "flushcenter"
    };
}

@DslMarker
annotation class BindToClosest

@BindToClosest
open class TexBase(private val print: print) {
    fun frame(frameTitle: String, vararg args: Pair<String, String>, body: TexBase.() -> Unit) =
        wrap("frame", params(*args) + "\n\\frametitle{$frameTitle}", body)

    fun math(code: String) = "$$$code$$\n".print()

    fun itemize(body: TexItemCtx.() -> Unit) = items("itemize", body)

    fun enumerate(body: TexItemCtx.() -> Unit) = items("enumerate", body)

    fun alignment(type: ALIGN, body: TexBase.() -> Unit) =
        wrap(type.toString(), body = body)

    fun customTag(name: String, vararg args: Pair<String, String>, body: TexBase.() -> Unit) =
        wrap(name, params(*args), body)

    operator fun String.unaryPlus() = (this.trimIndent() + "\n").print()

    protected fun items(tag: String, body: TexItemCtx.() -> Unit) {
        "\\begin{$tag}\n".print()
        TexItemCtx(print).body()
        "\\end{$tag}\n".print()
    }

    protected fun wrap(tag: String, head: String = "", body: TexBase.() -> Unit) {
        "\\begin{$tag}$head\n".print()
        body()
        "\\end{$tag}\n".print()
    }

    protected fun params(vararg args: String, transform: ((String) -> CharSequence)? = null) =
        args.joinToString(",", "[", "]", transform = transform)

    protected fun params(vararg args: Pair<String, String>) =
        args.joinToString(",", "[", "]") { "${it.first}=${it.second}" }
}


class TexItemCtx(private val print: print) : TexBase(print) {
    fun item(body: TexBase.() -> Unit) {
        "\\item\n".print()
        body()
        "\n".print()
    }
}


class TexDoc(private val print: print) : TexBase(print) {
    fun documentClass(`class`: String) =
        "\\documentclass{$`class`}\n".print()

    fun usepackage(`package`: String, vararg args: String) =
        ("\\usepackage" + params(*args) + "{$`package`}\n").print()
}

class Tex(val source: TexDoc.() -> Unit) {
    fun toOutputStream(stream: OutputStream) =
        TexDoc { stream.write(toByteArray()) }.source()
}

fun document(source: TexDoc.() -> Unit) = Tex(source)
