package com.fairuz.finance.data.repository

import com.fairuz.finance.domain.model.Category
import com.fairuz.finance.domain.repository.CategoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CategoryRepositoryImpl : CategoryRepository {

    private val db = FirebaseFirestore.getInstance()
    private val categoryCollection = db.collection("categories")

    override fun getCategories(type: String): Flow<Result<List<Category>>> {
        return categoryCollection.whereEqualTo("type", type)
            .snapshots()
            .map { snapshot ->
                try {
                    val categories = snapshot.toObjects(Category::class.java)
                    Result.success(categories)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
    }

    override suspend fun addCategory(category: Category): Result<Unit> {
        return try {
            categoryCollection.add(category).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            categoryCollection.document(categoryId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}