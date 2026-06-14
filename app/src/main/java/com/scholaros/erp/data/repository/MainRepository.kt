package com.scholaros.erp.data.repository

import com.scholaros.erp.api.ApiClient
import com.scholaros.erp.api.ApiService
import com.scholaros.erp.data.local.SessionManager
import com.scholaros.erp.data.model.*
import com.scholaros.erp.utils.Resource

class MainRepository(private val session: SessionManager) {

    private fun api(): ApiService = ApiClient.getService(session)

    // ── Helpers ───────────────────────────────────────────────────────────────
    private suspend fun <T> safeCall(call: suspend () -> retrofit2.Response<ApiResponse<T>>): Resource<T> {
        return try {
            val response = call()
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Resource.Success(body.data)
            } else {
                Resource.Error(body?.message ?: "Request failed (${response.code()})")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error. Check your connection.")
        }
    }

    // ── Dashboard ─────────────────────────────────────────────────────────────
    suspend fun getDashboard() = safeCall { api().getDashboard() }

    // ── Profile ───────────────────────────────────────────────────────────────
    suspend fun getProfile() = safeCall { api().getProfile() }

    suspend fun updateProfile(name: String, mobile: String) = safeCall {
        api().updateProfile(mapOf("name" to name, "mobile" to mobile))
    }

    // ── Attendance ────────────────────────────────────────────────────────────
    suspend fun getAttendance(month: String) = safeCall { api().getAttendance(month) }

    // ── Notices ───────────────────────────────────────────────────────────────
    suspend fun getNotices(page: Int = 1, category: String = "") = safeCall {
        api().getNotices(page, category)
    }

    // ── Fees ──────────────────────────────────────────────────────────────────
    suspend fun getFees() = safeCall { api().getFees() }

    // ── Homework ──────────────────────────────────────────────────────────────
    suspend fun getHomework() = safeCall { api().getHomework() }

    // ── Results ───────────────────────────────────────────────────────────────
    suspend fun getExams() = safeCall { api().getExams() }
    suspend fun getResult(examId: Int) = safeCall { api().getResult(examId) }

    // ── Timetable ─────────────────────────────────────────────────────────────
    suspend fun getTimetable() = safeCall { api().getTimetable() }

    // ── Materials ─────────────────────────────────────────────────────────────
    suspend fun getMaterials(page: Int = 1) = safeCall { api().getMaterials(page) }

    // ── Leave ─────────────────────────────────────────────────────────────────
    suspend fun getLeaveTypes() = safeCall { api().getLeaveTypes() }
    suspend fun getLeaveApplications(page: Int = 1) = safeCall { api().getLeaveApplications(page) }

    suspend fun applyLeave(leaveTypeId: Int, fromDate: String, toDate: String, reason: String) = safeCall {
        api().applyLeave(mapOf(
            "leave_type_id" to leaveTypeId,
            "from_date" to fromDate,
            "to_date" to toDate,
            "reason" to reason
        ))
    }

    // ── Complaints ────────────────────────────────────────────────────────────
    suspend fun getComplaints(page: Int = 1) = safeCall { api().getComplaints(page) }

    suspend fun submitComplaint(category: String, subject: String, description: String, priority: String) = safeCall {
        api().submitComplaint(mapOf(
            "category" to category,
            "subject" to subject,
            "description" to description,
            "priority" to priority
        ))
    }

    // ── Calendar ──────────────────────────────────────────────────────────────
    suspend fun getCalendar(month: String) = safeCall { api().getCalendar(month) }
    suspend fun getUpcoming() = safeCall { api().getUpcoming() }
}
