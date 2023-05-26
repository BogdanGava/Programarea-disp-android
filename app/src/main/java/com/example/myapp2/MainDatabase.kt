package com.example.myapp2

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class Expense(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    val title: String,
    val amount: Long,
    val date: Long
)

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense")
    fun getAll(): Flow<List<Expense>>

    @Insert
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun delete(user: Expense)
}

@Database(entities = [Expense::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}