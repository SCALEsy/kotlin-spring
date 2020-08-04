package test.join

import org.slf4j.LoggerFactory

class test {
    val logger = LoggerFactory.getLogger(this.javaClass)
    private fun advance(list: List<Int>, index: Int): Triple<Int, Int, List<Int>> {
        if (index >= list.size) {
            return Triple(-1, index, emptyList())
        }
        val first = list[index]
        var i = index
        val out = mutableListOf<Int>()
        while (i < list.size) {
            val v = list[i]
            if (v != first) {
                break
            }
            out.add(v)
            i++
        }
        return Triple(first, i, out.toList())
    }
    /**
     * sort merge join 算法实现full join
     * sort merge join 算法实现full join
     * */
    fun join(a: List<Int>, b: List<Int>): List<Pair<Int?, Int?>> {
        val left = a.sorted()
        val right = b.sorted()

        var pl = advance(left, 0)
        var pr = advance(right, 0)
        val out = mutableListOf<Pair<Int?, Int?>>()
        while (pl.third.size > 0 || pr.third.size > 0) {
            val (ka, li, listl) = pl
            val (kb, ri, listr) = pr
            logger.info("{}->{}", ka, kb)
            if (ka == kb) {
                val l = listl.flatMap { a ->
                    val ab = listr.map { b ->
                        Pair(a, b)
                    }
                    ab
                }
                out.addAll(l)
                pl = advance(left, li)
                pr = advance(right, ri)
            }
            if (ka < kb) {
                pl = advance(left, li)
                if (pl.third.size > 0) {
                    out.addAll(listl.map { a -> Pair(a, null) })
                }
                if (ka == -1) {
                    out.addAll(listr.map { a -> Pair(null, a) })
                    pr = advance(right, ri)
                }
            }
            if (ka > kb) {
                pr = advance(right, ri)
                if (pr.third.size > 0) {
                    out.addAll(listr.map { a -> Pair(null, a) })
                }
                if (kb == -1) {
                    out.addAll(listl.map { a -> Pair(a, null) })
                    pl = advance(left, li)
                }
            }
        }
        return out
    }

    companion object {
        @JvmStatic
        fun main(arr: Array<String>) {
            val a = listOf<Int>(1, 2, 3, 4, 5, 5)
            val b = listOf<Int>(1, 3, 3, 5, 6, 7)

            /**
             *  1  2  3    4  5,5
             *  1     3,3      5   6    7
             *
             * */
            val test = test()
            val c = test.join(a, b)
            println(c)
            val d = test.join(b, a)
            println(d)
        }
    }
}