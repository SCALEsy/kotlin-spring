package test

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class Application {

}

fun main(vararg args: String) {
    SpringApplication.run(Application::class.java, *args)
    //test()
}

fun test() {
    val a = listOf<Pair<Int, String>>(Pair(1, "a"), Pair(2, "a"))
    val b = listOf<Pair<Int, String>>(Pair(1, "b"), Pair(2, "b"))
    val l = a.union(b).groupBy { a -> a.first }.map tag@{ m ->
        val v = m.value.map { a -> a.second }
        return@tag Pair(m.key, v)
    }
    l.forEach { a -> println(a) }
}
