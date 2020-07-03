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
import test.repository.TableRepository


@RestController
@Api
open class TestController {
    val logger = LoggerFactory.getLogger(this.javaClass)
    @Autowired
    private lateinit var table: TableRepository
    @Autowired
    private lateinit var pool: ThreadPoolTaskExecutor


    @GetMapping("/bean")
    @ApiOperation("bean")
    fun bean(param: BaseParam): Bean {
        val b = Bean(1, "asd")
        val list = table.findAll()
        logger.info(list.filter { a -> a.id == param.id }.first().toString())
        return b
    }

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
}