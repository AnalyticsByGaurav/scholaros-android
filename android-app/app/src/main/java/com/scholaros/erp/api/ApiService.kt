package com.scholaros.erp.api

import com.scholaros.erp.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────────
    @POST("api/auth/login.php")
    suspend fun login(@Body body: LoginRequest): Response<ApiResponse<LoginData>>

    @POST("api/auth/logout.php")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @POST("api/fcm-token.php")
    suspend fun registerFcmToken(@Body body: Map<String, String>): Response<ApiResponse<Unit>>

    @DELETE("api/fcm-token.php")
    suspend fun removeFcmToken(@Body body: Map<String, String>): Response<ApiResponse<Unit>>

    // ── Dashboard ─────────────────────────────────────────────────────────────
    @GET("api/dashboard.php")
    suspend fun getDashboard(): Response<ApiResponse<DashboardData>>

    // ── Profile ───────────────────────────────────────────────────────────────
    @GET("api/profile.php")
    suspend fun getProfile(): Response<ApiResponse<ProfileData>>

    @POST("api/profile.php")
    suspend fun updateProfile(@Body body: Map<String, String>): Response<ApiResponse<Map<String, String>>>

    @Multipart
    @POST("api/profile.php")
    suspend fun uploadProfilePhoto(
        @Query("action") action: String = "photo",
        @Part photo: MultipartBody.Part
    ): Response<ApiResponse<Map<String, String>>>

    @POST("api/profile.php")
    suspend fun changePassword(
        @Query("action") action: String = "password",
        @Body body: Map<String, String>
    ): Response<ApiResponse<Unit>>

    // ── Attendance ────────────────────────────────────────────────────────────
    @GET("api/attendance.php")
    suspend fun getAttendance(@Query("month") month: String): Response<ApiResponse<AttendanceData>>

    @GET("api/attendance.php")
    suspend fun getClassRoster(
        @Query("class_id") classId: Int,
        @Query("section_id") sectionId: Int,
        @Query("date") date: String
    ): Response<ApiResponse<Map<String, Any>>>

    @POST("api/attendance.php")
    suspend fun markAttendance(@Body body: Map<String, Any>): Response<ApiResponse<Map<String, Int>>>

    // ── Notices ───────────────────────────────────────────────────────────────
    @GET("api/notices.php")
    suspend fun getNotices(
        @Query("page") page: Int = 1,
        @Query("category") category: String = ""
    ): Response<ApiResponse<NoticeListData>>

    @POST("api/notices.php")
    suspend fun postNotice(@Body body: Map<String, Any>): Response<ApiResponse<Map<String, Int>>>

    // ── Fees ──────────────────────────────────────────────────────────────────
    @GET("api/fees.php")
    suspend fun getFees(): Response<ApiResponse<FeesData>>

    @GET("api/fees.php")
    suspend fun getFeesForStudent(@Query("student_id") studentId: Int): Response<ApiResponse<FeesData>>

    // ── Homework ──────────────────────────────────────────────────────────────
    @GET("api/homework.php")
    suspend fun getHomework(
        @Query("class_id") classId: Int = 0,
        @Query("section_id") sectionId: Int = 0
    ): Response<ApiResponse<HomeworkData>>

    @POST("api/homework.php")
    suspend fun postHomework(@Body body: Map<String, Any>): Response<ApiResponse<Map<String, Int>>>

    // ── Results ───────────────────────────────────────────────────────────────
    @GET("api/results.php")
    suspend fun getExams(@Query("exams") exams: Int = 1): Response<ApiResponse<ExamListData>>

    @GET("api/results.php")
    suspend fun getResult(@Query("exam_id") examId: Int): Response<ApiResponse<ResultData>>

    @GET("api/results.php")
    suspend fun getResultForStudent(
        @Query("exam_id") examId: Int,
        @Query("student_id") studentId: Int
    ): Response<ApiResponse<ResultData>>

    // ── Timetable ─────────────────────────────────────────────────────────────
    @GET("api/timetable.php")
    suspend fun getTimetable(@Query("day") day: Int = 0): Response<ApiResponse<TimetableData>>

    // ── Materials ─────────────────────────────────────────────────────────────
    @GET("api/materials.php")
    suspend fun getMaterials(
        @Query("page") page: Int = 1,
        @Query("subject_id") subjectId: Int = 0,
        @Query("type") type: String = ""
    ): Response<ApiResponse<MaterialsData>>

    // ── Leave ─────────────────────────────────────────────────────────────────
    @GET("api/leave.php")
    suspend fun getLeaveTypes(@Query("types") types: Int = 1): Response<ApiResponse<LeaveTypesData>>

    @GET("api/leave.php")
    suspend fun getLeaveApplications(
        @Query("page") page: Int = 1,
        @Query("status") status: String = ""
    ): Response<ApiResponse<LeaveListData>>

    @POST("api/leave.php")
    suspend fun applyLeave(@Body body: Map<String, Any>): Response<ApiResponse<Map<String, Any>>>

    @POST("api/leave.php")
    suspend fun updateLeave(
        @Query("action") action: String = "approve",
        @Body body: Map<String, Any>
    ): Response<ApiResponse<Map<String, Any>>>

    // ── Complaints ────────────────────────────────────────────────────────────
    @GET("api/complaints.php")
    suspend fun getComplaints(@Query("page") page: Int = 1): Response<ApiResponse<ComplaintsData>>

    @GET("api/complaints.php")
    suspend fun getComplaintDetail(@Query("id") id: Int): Response<ApiResponse<Map<String, Any>>>

    @POST("api/complaints.php")
    suspend fun submitComplaint(@Body body: Map<String, String>): Response<ApiResponse<Map<String, Any>>>

    // ── Calendar ──────────────────────────────────────────────────────────────
    @GET("api/calendar.php")
    suspend fun getCalendar(@Query("month") month: String): Response<ApiResponse<CalendarData>>

    @GET("api/calendar.php")
    suspend fun getUpcoming(@Query("upcoming") upcoming: Int = 1): Response<ApiResponse<CalendarData>>
}
