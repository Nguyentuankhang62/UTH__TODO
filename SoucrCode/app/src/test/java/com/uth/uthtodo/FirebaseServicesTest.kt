package com.uth.uthtodo

import com.uth.uthtodo.firebase.FirebaseServiceItem
import com.uth.uthtodo.firebase.FirebaseServiceStatus
import com.uth.uthtodo.firebase.FirebaseServicesState
import com.uth.uthtodo.navigation.AppRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Kiểm thử đơn vị cho mô hình OOP các dịch vụ Firebase của Bài 4
class FirebaseServicesTest {

    @Test
    fun firebaseServicesState_hasDefaultProjectAndBanner() {
        val state = FirebaseServicesState()

        assertEquals("uth-3845a", state.projectId)
        assertEquals(FirebaseServicesState.DEFAULT_WELCOME_BANNER, state.remoteBannerText)
        assertFalse(state.isBusy)
        assertFalse(state.isError)
    }

    @Test
    fun firebaseServiceItem_andRouteAreConfiguredCorrectly() {
        val item = FirebaseServiceItem(
            id = "analytics",
            name = "Firebase Analytics",
            category = "Analytics",
            status = FirebaseServiceStatus.ACTIVE,
            detail = "Active"
        )

        assertEquals("Đang hoạt động", item.status.label)
        assertTrue(AppRoute.FirebaseServices.route == "firebase_services")
    }
}
