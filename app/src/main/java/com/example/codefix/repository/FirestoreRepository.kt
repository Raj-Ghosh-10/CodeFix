package com.example.codefix.repository

import com.example.codefix.model.DebugHistoryItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val debugHistoryCollection = db.collection("debugHistory")

    fun deleteDebugHistory(id: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        debugHistoryCollection.document(id)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun listenToDebugHistory(onUpdate: (List<DebugHistoryItem>) -> Unit): ListenerRegistration {
        return debugHistoryCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) return@addSnapshotListener
            val historyList = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(DebugHistoryItem::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            onUpdate(historyList)
        }
    }

    fun getDebugHistory(onSuccess: (List<DebugHistoryItem>) -> Unit, onFailure: (Exception) -> Unit) {
        debugHistoryCollection.get()
            .addOnSuccessListener { snapshot ->
                val historyList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(DebugHistoryItem::class.java)?.copy(id = doc.id)
                }
                onSuccess(historyList)
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}