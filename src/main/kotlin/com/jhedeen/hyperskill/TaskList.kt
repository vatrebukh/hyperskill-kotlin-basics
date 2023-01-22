package com.jhedeen.hyperskill

import kotlinx.datetime.*
import com.squareup.moshi.*
import java.io.File

var taskList = mutableListOf<Task>()
val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
val type = Types.newParameterizedType(MutableList::class.java, Task::class.java)
val taskAdapter = moshi.adapter<MutableList<Task>>(type)
val filePath = "src/main/resources/tasklist.json"

fun main() {
    println(f(6))

    init()

    while(true) {
        println("Input an action (add, print, edit, delete, end):")
        when(readln()) {
            "add" -> addTask()
            "print" -> printTasks()
            "edit" -> editTask()
            "delete" -> deleteTask()
            "end" -> {
                println("Tasklist exiting!")
                break
            }
            else -> {
                println("The input action is invalid")
            }
        }
    }
    save()
}

fun f(n: Int): Int = if (n > 2) f(n - 1) + f(n - 2) + f(n - 3) else n

fun init() {
    val taskFile = File(filePath)
    if (taskFile.exists()) {
        taskList = taskAdapter.fromJson(taskFile.readText())!!
    }
}

fun save() {
    val taskFile = File(filePath)
    if (!taskFile.exists()) {
        taskFile.createNewFile()
    }
    taskFile.writeText(taskAdapter.toJson(taskList))
}

fun deleteTask() {
    printTasks()
    if (taskList.isEmpty()) {
        return
    }
    val number = getTaskNumber()
    taskList.removeAt(number - 1)
    println("The task is deleted")
}

fun editTask() {
    printTasks()
    if (taskList.isEmpty()) {
        return
    }
    val number = getTaskNumber()
    val task = taskList.get(number - 1)

    while (true) {
        println("Input a field to edit (priority, date, time, task):")
        when(readln()) {
            "priority" -> task.priority = getPriority()
            "date" -> task.date = getDate()
            "time" -> task.time = getTime()
            "task" -> task.tasks = getTasks()
            else -> {
                println("Invalid field")
                continue
            }
        }
        break
    }

    println("The task is changed")
}

fun addTask() {
    val priority = getPriority()
    val date = getDate()
    val time = getTime()
    val tasks = getTasks()
    if (tasks.isEmpty()) {
        return
    }
    val task = Task(date, time, priority, tasks)
    taskList.add(task)
}

fun getTaskNumber(): Int {
    while (true) {
        println("Input the task number (1-${taskList.size}):")
        try {
            val number = readln().toInt()
            if (number in 1..taskList.size) {
                return number
            }
        } catch (_: NumberFormatException) {
        }
        println("Invalid task number")
    }
}

fun getPriority(): String {
    val priorityList = listOf("C", "H", "N", "L")
    while (true) {
        println("Input the task priority (C, H, N, L):")
        val input = readln().uppercase()
        if (priorityList.contains(input)) {
            return input
        }
    }
}

fun getDate(): String {
    while (true) {
        println("Input the date (yyyy-mm-dd):")
        try {
            val input = readln().split("-").joinToString("-") { if (it.length == 1) "0$it" else it }
            return LocalDate.parse(input).toString()
        } catch (ex: IllegalArgumentException) {
            println("The input date is invalid")
        }
    }
}

fun getTime(): String {
    while (true) {
        println("Input the time (hh:mm):")
        try {
            val input = readln().split(":").joinToString(":") { if (it.length == 1) "0$it" else it }
            val (h, m) = input.split(":").map { it.toInt() }
            if (h in 0..23 && m in 0..59) {
                return input
            }
        } catch (_: NumberFormatException) {

        }
        println("The input time is invalid")
    }
}

fun getTasks(): MutableList<String> {
    val task = mutableListOf<String>()
    println("Input a new task (enter a blank line to end):")
    while (true) {
        val input = readln()
        if (input.isBlank()) {
            if (task.isEmpty()) {
                println("The task is blank")
                return mutableListOf()
            }
            break
        }
        task.add(input.trim())
    }
    return task
}

fun printTasks() {
    if (taskList.isEmpty()) {
        println("No tasks have been input")
    } else {
        println("+----+------------+-------+---+---+--------------------------------------------+")
        println("| N  |    Date    | Time  | P | D |                   Task                     |")
        println("+----+------------+-------+---+---+--------------------------------------------+")
        for (l in taskList.withIndex()) {
            val task = l.value
            val ind = l.index + 1
            val lines = toMultiLine(task.tasks)
            print("| ${toIndex(ind.toString())} | ${task.date} | ${task.time} | ")
            task.printPriority()
            print(" | ")
            task.printDue()
            println(" |${lines[0]}|")
            for (i in 1 until lines.size) {
                println("|    |            |       |   |   |${lines[i]}|")
            }
            println("+----+------------+-------+---+---+--------------------------------------------+")
        }
    }
}

fun toIndex(ind: String): String {
    return ind + " ".repeat(2 - ind.length)
}

fun toMultiLine(subTasks: MutableList<String>): MutableList<String> {
    val res = mutableListOf<String>()
    subTasks.forEach {
        run {
            var str = it
            while (str.length > 44) {
                res.add(str.substring(0, 44))
                str = str.substring(44)
            }
            res.add(str + " ".repeat(44 - str.length))
        }
    }
    return res
}

class Task(var date: String, var time: String, var priority: String, var tasks: MutableList<String>) {

    fun printDue() {
        val taskDate = LocalDate.parse(date)
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.of("UTC+0")).date
        val days = currentDate.daysUntil(taskDate)
        return when {
            days == 0 -> print("\u001B[103m \u001B[0m")
            days > 0 -> print("\u001B[102m \u001B[0m")
            else -> print("\u001B[101m \u001B[0m")
        }
    }

    fun printPriority() {
        return when(priority) {
            "H" -> print("\u001B[103m \u001B[0m")
            "N" -> print("\u001B[102m \u001B[0m")
            "C" -> print("\u001B[101m \u001B[0m")
            "L" -> print("\u001B[104m \u001B[0m")
            else -> {}
        }
    }
}