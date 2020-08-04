package test.bean

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.annotations.ApiModelProperty
import java.sql.Timestamp

data class Bean(var id: Int, var name: String)

/*
@Entity
@Table(name = "sdb_table_relation")
data class Table(
    @Id @Column(name = "id", nullable = false)
    val id: Int,

    val main_table: String,

    val main_key: String,

    val join_table: String,

    val join_key: String,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val create_time: Timestamp?,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var modify_time: Timestamp?
)
*/


data class BaseParam(
    @ApiModelProperty(value = "page", example = "1") var page: Int?,
    @ApiModelProperty(value = "size", example = "1") var size: Int?,
    @ApiModelProperty(value = "id", example = "1") var id: Int?

)


open class Father(val id: Int, var name: String) {

    protected val test: Int = 111

    constructor(id: Int) : this(id, "") {

    }

    open fun dowork(a: Int, sum: (b: Int) -> Int): Int {
        var sum = a + sum(a)
        return sum
    }
}

class Son(id: Int, name: String) : Father(id, name) {
    fun <T> map(run: (name: String) -> T): T {
        return run(this.name)
    }

    override fun dowork(a: Int, sum: (b: Int) -> Int): Int {
        return 1;
    }

}
