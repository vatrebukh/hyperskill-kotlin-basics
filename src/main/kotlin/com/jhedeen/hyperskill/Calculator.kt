package com.jhedeen.hyperskill

val vars: MutableMap<String, String> = mutableMapOf()
val operators = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2, "(" to 0, ")" to 0)

fun main() {
    while (true) {
        when(val input = readln()) {
            "" -> continue
            "/exit" -> break
            "/help" -> println("The program evaluates: +, -, *, / of numbers")
            else -> evaluate(input)
        }
    }
    println("Bye!")
}

fun evaluate(input: String) {
    val variable = "[a-zA-Z]+".toRegex()
    val expression = prepareExpression(input)
    if (expression == "") {
        return
    }
    val postfix = toPostfix(expression)
    if (postfix.isEmpty()) {
        return
    }

    val q = mutableListOf<String>()

    postfix.forEach {
        if (!operators.containsKey(it)) {
            if (it.matches(variable)) q.add(0, vars.getValue(it)) else q.add(0, it)
        } else {
            val s = q.removeFirst()
            val f = if (q.isEmpty()) "0" else  q.removeFirst()
            val res = when (it) {
                "+" -> f.toBigInteger().plus(s.toBigInteger()).toString()
                "-" -> f.toBigInteger().minus(s.toBigInteger()).toString()
                "*" -> f.toBigInteger().multiply(s.toBigInteger()).toString()
                "/" -> f.toBigInteger().divide(s.toBigInteger()).toString()
                else -> "0"
            }
            q.add(0, res)
        }
    }
    println(q.first())
}

fun toPostfix(input: String): MutableList<String> {
    val q = mutableListOf<String>()
    val result = mutableListOf<String>()

    input.split(" ").forEach {
        if (!operators.containsKey(it)) {
            result.add(it)
        } else if (q.isEmpty() || q.first() == "(") {
            q.add(0, it)
        } else if (it == "(") {
            q.add(0, it)
        } else if (it == ")") {
            if (!q.contains("(")) {
                println("Invalid expression")
                return mutableListOf()
            }
            while (q.first() != "(") {
                result.add(q.removeFirst())
            }
            q.removeFirst()
        } else if (operators.getValue(it) > operators.getValue(q.first())) {
            q.add(0, it)
        } else if (operators.getValue(it) <= operators.getValue(q.first())) {
            var op = q.first()
            while (operators.getValue(op) >= operators.getValue(it)) {
                if (op == "(") {
                    break
                }
                result.add(q.removeFirst())
                if (q.isEmpty()) {
                    break
                }
                op = q.first()
            }
            q.add(0, it)
        }
    }
    if (q.contains("(")) {
        println("Invalid expression")
        return mutableListOf()
    }
    result.addAll(q)
    return result
}

fun prepareExpression(input: String): String {
    val p = "\\+\\+|--".toRegex()
    val m = "\\+-|-\\+".toRegex()
    val variable = "[a-zA-Z]+".toRegex()
    val digit = "-?[0-9]+".toRegex()

    if (input.startsWith("/")) {
        println("Unknown command")
        return ""
    }
    var noSpaces = input.replace(" ", "")

    if (noSpaces.contains("\\*\\*|//|\\*/|/\\*".toRegex())) {
        println("Invalid expression")
        return ""
    }

    if (!noSpaces.contains("-") && !noSpaces.contains("+") && !noSpaces.contains("*") && !noSpaces.contains("/") && !noSpaces.contains("=")) {
        if (!noSpaces.matches(variable)) {
            println("Invalid identifier")
        } else if (vars.containsKey(noSpaces)) {
            println(vars[noSpaces])
        } else {
            println("Unknown variable")
        }
        return ""
    }
    if (noSpaces.contains("=")) {
        if (noSpaces.split("=").size > 2) {
            println("Invalid assignment")
            return ""
        }
        val (k, v) = noSpaces.split("=")

        if (!k.matches(variable)) {
            println("Invalid identifier")
            return ""
        }

        if (v.matches(digit)) {
            vars[k] = v
        } else if (v.matches(variable)) {
            if (vars.containsKey(v)) {
                vars[k] = vars.getValue(v)
            } else {
                println("Unknown variable")
            }
        } else {
            println("Invalid assignment")
        }
        return ""
    }

//    noSpaces = noSpaces.replace("-", " - ")
//    noSpaces = noSpaces.replace("+", " + ")
//    noSpaces = noSpaces.trim().split(" ").joinToString("") { if (vars.containsKey(it)) vars.getValue(it) else it }

    while (noSpaces.contains(p)) {
        noSpaces = noSpaces.replace(p, "+")
    }
    while (noSpaces.contains(m)) {
        noSpaces = noSpaces.replace(m, "-")
    }
    noSpaces = noSpaces.replace("-", " - ")
    noSpaces = noSpaces.replace("+", " + ")
    noSpaces = noSpaces.replace("*", " * ")
    noSpaces = noSpaces.replace("/", " / ")
    noSpaces = noSpaces.replace("(", "( ")
    noSpaces = noSpaces.replace(")", " )")
    return noSpaces.trim()
}

