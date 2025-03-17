package com.example.codefix.repository

import android.util.Log
import com.example.codefix.model.DebugHistoryItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val debugHistoryCollection = db.collection("debugHistory")

    // 🔹 Save a new debug history entry with a consistent ID
    fun saveDebugHistory(item: DebugHistoryItem, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val documentRef = if (item.id.isEmpty()) debugHistoryCollection.document() else debugHistoryCollection.document(item.id)
        val newItem = item.copy(id = documentRef.id) // Assign ID before saving

        documentRef.set(newItem)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error saving debug history: ${exception.message}")
                onFailure(exception)
            }
    }

    // 🔹 Get all debug history (One-time fetch)
    fun getDebugHistory(onSuccess: (List<DebugHistoryItem>) -> Unit, onFailure: (Exception) -> Unit) {
        debugHistoryCollection.get()
            .addOnSuccessListener { snapshot ->
                val historyList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(DebugHistoryItem::class.java)?.copy(id = doc.id)
                }
                onSuccess(historyList)
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching debug history: ${exception.message}")
                onFailure(exception)
            }
    }

    // 🔹 Listen for live debug history updates (Real-time Firestore listener)
    fun listenToDebugHistory(onUpdate: (List<DebugHistoryItem>) -> Unit): ListenerRegistration {
        return debugHistoryCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e("Firestore", "Error listening to debug history: ${exception.message}")
                return@addSnapshotListener
            }

            val historyList = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(DebugHistoryItem::class.java)?.copy(id = doc.id)
            } ?: emptyList()

            onUpdate(historyList)
        }
    }

    // 🔹 Delete a debug history entry
    fun deleteDebugHistory(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        debugHistoryCollection.document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error deleting debug history: ${exception.message}")
                onFailure(exception)
            }
    }
}
