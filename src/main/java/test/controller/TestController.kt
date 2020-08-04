package test.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.async.DeferredResult
import test.bean.BaseParam
import test.bean.Bean
import test.bean.Father
import test.bean.Son
import java.util.concurrent.Callable
import java.util.concurrent.atomic.AtomicInteger
import javax.servlet.AsyncEvent
import javax.servlet.AsyncListener
import javax.servlet.http.HttpServletRequest


@RestController
@Api
open class TestController {
    val logger = LoggerFactory.getLogger(this.javaClass)
    /*@Autowired
    private lateinit var table: TableRepository*/
    @Autowired
    private lateinit var pool: ThreadPoolTaskExecutor


    private val ids = AtomicInteger()

    /* @GetMapping("/bean")
     @ApiOperation("bean")
     fun bean(param: BaseParam): Bean {
         val b = Bean(1, "asd")
         val list = table.findAll()
         logger.info(list.filter { a -> a.id == param.id }.first().toString())
         return b
     }
 */
    @PostMapping("/compute")
    @ApiOperation("计算")
    fun complute(param: BaseParam): DeferredResult<Int> {
        val result = DeferredResult<Int>()
        result.onCompletion {
            logger.info("ok")
        }
        result.onError { e -> logger.error(e.message) }
        pool.submit {
            var sum = 1
            for (i in 1..param.id!!) {
                sum *= i
            }
            result.setResult(sum)

            logger.info("finish,{}", sum)
        }


        return result
    }

    @PostMapping("/test")
    @ApiOperation("计算")
    fun test(param: BaseParam): DeferredResult<Int> {
        val result = DeferredResult<Int>()
        result.onCompletion {
            logger.info("ok")
        }
        result.onError { e -> logger.error(e.message) }
        pool.submit {
            val father = Father(param.id ?: 1, "father")
            father.dowork(2) { a -> return@dowork a + 10 }
            father
            val son = Son(param.id ?: 2, "son")
            son.dowork(4) { x -> x * 2 }
            son.map { str -> "i am $str" }
            logger.info(father.toString())
            logger.info(son.toString())
            result.setResult(param.id ?: 10)
        }

        test.test()
        return result
    }


    @PostMapping("/test2")
    @ApiOperation("计算")
    fun test3(param: BaseParam): DeferredResult<Int> {
        val result = DeferredResult<Int>()
//        result.onCompletion {
//            logger.info("ok")
//        }
//        result.onError { e -> logger.error(e.message) }

        val id = ids.getAndIncrement()

        logger.info("submit task {}", id)
        val future = pool.submit(Callable<Int> {
            logger.info("task start {}", id)
            Thread.sleep(20000)
            logger.info("finish task {}", id)
            result.setResult(100)
            return@Callable 1
        })

        result.onTimeout {
            logger.info("cancel task {}", id)
            future.cancel(true)
        }

        return result
    }


    @PostMapping("/callback")
    @ApiOperation("计算")
    fun callback(): Callable<Int> {

        val id = ids.getAndIncrement()

        logger.info("submit task {}", id)

        val callable = Callable<Int> {
            logger.info("task start {}", id)
            Thread.sleep(20000)
            logger.info("finish task {}", id)
            //result.setResult(100)
            return@Callable 1
        }

        /*result.onTimeout {
            logger.info("cancel task {}", id)
            future.cancel(true)
        }*/

        return callable
    }

    @PostMapping("/http")
    @ApiOperation("计算")
    fun http(request: HttpServletRequest) {

        val id = ids.getAndIncrement()

        logger.info("submit task {}\n", id)

        val context = request.startAsync()
        context.timeout = 3000

        val future = pool.submit(Callable<Int> {
            logger.info("---------------------task start {}", id)
            Thread.sleep(20000)
            logger.info("---------------------finish task {}", id)
            //result.setResult(100)
            context.complete()
            return@Callable 1
        })
        context.addListener(object : AsyncListener {
            override fun onComplete(p0: AsyncEvent?) {
                logger.info("finish complete {}", id)
            }

            override fun onStartAsync(p0: AsyncEvent?) {
                logger.info("start task {}", id)
            }

            override fun onTimeout(p0: AsyncEvent?) {
                logger.info("timeout task {}", id)
                //future.cancel(true)
            }

            override fun onError(p0: AsyncEvent?) {
                logger.info("error task {}", id)
            }
        })
    }
}