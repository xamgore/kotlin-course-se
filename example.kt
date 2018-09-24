// example 1
var a = 10
var b = 20
if (a > b) {
    println(1)
} else {
    println(0)
}


// example 2
fun fib(n) {
    if (n <= 1) {
        return 1
    }
    return fib(n - 1) + fib(n - 2)
}

var i = 1
while (i <= 5) {
    println(i, fib(i))
    i = i + 1
}


// example 3
fun foo(n) {
    fun bar(m) {
        return m + n
    }

    return bar(1)
}

println(foo(41)) // prints 42




