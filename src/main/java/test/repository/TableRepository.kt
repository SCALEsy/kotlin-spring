package test.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import test.bean.Table

@Repository
interface TableRepository : JpaRepository<Table, Int>