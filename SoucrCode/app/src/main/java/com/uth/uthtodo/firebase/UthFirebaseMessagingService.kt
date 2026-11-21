package com.uth.uthtodo.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// Dịch vụ nhận thông báo đẩy và cập nhật FCM Token từ Firebase Cloud Messaging
class UthFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM token refreshed: ${token.take(12)}...")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title ?: "UTH Todo"
        val body = message.notification?.body ?: ""
        Log.d(TAG, "FCM message received: $title - $body")
    }

    companion object {
        private const val TAG = "UthFcmService"
    }
}
