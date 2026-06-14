package com.scholaros.erp.data.model

import com.google.gson.annotations.SerializedName

// ── Generic API wrapper ───────────────────────────────────────────────────────
data class ApiResponse<T>(
    val success: Boolean,
    val message: String = "",
    val data: T? = null
)

// ── Auth ──────────────────────────────────────────────────────────────────────
data class LoginRequest(val email: String, val password: String)

data class LoginData(
    val token: String,
    @SerializedName("expires_at") val expiresAt: String,
    val user: User,
    val student: StudentInfo? = null
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("role_name") val roleName: String,
    @SerializedName("school_id") val schoolId: Int,
    @SerializedName("branch_id") val branchId: Int,
    @SerializedName("photo_url") val photoUrl: String? = null
)

// ── Profile ───────────────────────────────────────────────────────────────────
data class Profile(
    val id: Int,
    val name: String,
    val email: String,
    val mobile: String? = null,
    @SerializedName("photo_url") val photoUrl: String? = null,
    val status: String,
    @SerializedName("last_login") val lastLogin: String? = null,
    @SerializedName("school_name") val schoolName: String? = null,
    @SerializedName("branch_name") val branchName: String? = null,
    val role: String,
    @SerializedName("role_name") val roleName: String
)

data class ProfileData(
    val profile: Profile,
    val student: StudentInfo? = null,
    val employee: EmployeeInfo? = null
)

data class EmployeeInfo(
    val id: Int,
    val name: String,
    val designation: String? = null,
    val department: String? = null,
    val mobile: String? = null,
    val qualification: String? = null
)

// ── Dashboard ─────────────────────────────────────────────────────────────────
data class DashboardData(
    val user: DashboardUser,
    val student: StudentInfo? = null,
    @SerializedName("attendance_today") val attendanceToday: AttendanceToday? = null,
    @SerializedName("pending_fees") val pendingFees: Double? = null,
    @SerializedName("unread_notices") val unreadNotices: Int? = null,
    @SerializedName("classes_assigned") val classesAssigned: Int? = null,
    @SerializedName("total_students") val totalStudents: Int? = null,
    @SerializedName("total_staff") val totalStaff: Int? = null
)

data class DashboardUser(val name: String, val role: String)

data class AttendanceToday(val status: String)

// ── Student ───────────────────────────────────────────────────────────────────
data class StudentInfo(
    val id: Int,
    val name: String,
    @SerializedName("admission_number") val admissionNumber: String? = null,
    @SerializedName("roll_number") val rollNumber: String? = null,
    @SerializedName("class_id") val classId: Int? = null,
    @SerializedName("section_id") val sectionId: Int? = null,
    @SerializedName("class_name") val className: String? = null,
    @SerializedName("section_name") val sectionName: String? = null,
    @SerializedName("photo_url") val photoUrl: String? = null
)

// ── Attendance ────────────────────────────────────────────────────────────────
data class AttendanceData(
    val month: String,
    val records: List<AttendanceRecord>,
    val summary: AttendanceSummary,
    @SerializedName("total_marked") val totalMarked: Int
)

data class AttendanceRecord(
    val date: String,
    val status: String,
    val remarks: String? = null
)

data class AttendanceSummary(
    val present: Int,
    val absent: Int,
    val late: Int,
    @SerializedName("half_day") val halfDay: Int,
    val leave: Int
)

// ── Notices ───────────────────────────────────────────────────────────────────
data class NoticeListData(
    val notices: List<Notice>,
    val page: Int,
    @SerializedName("per_page") val perPage: Int,
    val total: Int,
    @SerializedName("total_pages") val totalPages: Int
)

data class Notice(
    val id: Int,
    val title: String,
    val content: String? = null,
    val category: String,
    @SerializedName("publish_date") val publishDate: String? = null,
    @SerializedName("expiry_date") val expiryDate: String? = null,
    @SerializedName("created_at") val createdAt: String
)

// ── Fees ──────────────────────────────────────────────────────────────────────
data class FeesData(
    val student: FeeStudent,
    val summary: FeeSummary,
    val history: List<FeeRecord>
)

data class FeeStudent(val id: Int, val name: String, @SerializedName("admission_number") val admissionNumber: String)

data class FeeSummary(
    @SerializedName("total_due") val totalDue: Double,
    @SerializedName("total_paid") val totalPaid: Double,
    @SerializedName("total_balance") val totalBalance: Double
)

data class FeeRecord(
    val id: Int,
    @SerializedName("total_amount") val totalAmount: Double,
    val discount: Double,
    @SerializedName("amount_paid") val amountPaid: Double,
    val balance: Double,
    @SerializedName("payment_method") val paymentMethod: String? = null,
    @SerializedName("receipt_number") val receiptNumber: String? = null,
    @SerializedName("payment_date") val paymentDate: String? = null,
    val status: String,
    @SerializedName("fee_head") val feeHead: String? = null
)

// ── Homework ──────────────────────────────────────────────────────────────────
data class HomeworkData(val homework: List<Homework>)

data class Homework(
    val id: Int,
    val title: String,
    val description: String? = null,
    @SerializedName("due_date") val dueDate: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("class_name") val className: String? = null,
    @SerializedName("section_name") val sectionName: String? = null,
    @SerializedName("subject_name") val subjectName: String? = null
)

// ── Exam Results ──────────────────────────────────────────────────────────────
data class ExamListData(val exams: List<Exam>)

data class Exam(
    val id: Int,
    val name: String,
    @SerializedName("type_name") val typeName: String? = null,
    @SerializedName("start_date") val startDate: String? = null,
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("class_name") val className: String? = null
)

data class ResultData(
    val student: ResultStudent,
    val exam: ExamBasic,
    val results: List<SubjectResult>,
    @SerializedName("total_max") val totalMax: Int,
    @SerializedName("total_obtained") val totalObtained: Int,
    val percentage: Double
)

data class ResultStudent(
    val id: Int,
    val name: String,
    @SerializedName("roll_number") val rollNumber: String? = null,
    @SerializedName("class_name") val className: String? = null,
    @SerializedName("section_name") val sectionName: String? = null
)

data class ExamBasic(val id: Int, val name: String)

data class SubjectResult(
    @SerializedName("subject_name") val subjectName: String,
    val code: String? = null,
    @SerializedName("max_marks") val maxMarks: Int,
    @SerializedName("obtained_marks") val obtainedMarks: Double,
    val grade: String? = null,
    @SerializedName("is_absent") val isAbsent: Int
)

// ── Timetable ─────────────────────────────────────────────────────────────────
data class TimetableData(
    val type: String,
    val session: SessionInfo? = null,
    val periods: List<Period>,
    val days: List<TimetableDay>
)

data class SessionInfo(val id: Int, val name: String)

data class Period(
    val id: Int,
    val name: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("is_break") val isBreak: Int
)

data class TimetableDay(
    val day: Int,
    @SerializedName("day_name") val dayName: String,
    val periods: List<TimetablePeriod>
)

data class TimetablePeriod(
    @SerializedName("day_of_week") val dayOfWeek: Int,
    @SerializedName("period_id") val periodId: Int,
    val room: String? = null,
    @SerializedName("subject_name") val subjectName: String? = null,
    @SerializedName("subject_code") val subjectCode: String? = null,
    @SerializedName("teacher_name") val teacherName: String? = null,
    @SerializedName("class_name") val className: String? = null,
    @SerializedName("section_name") val sectionName: String? = null,
    @SerializedName("period_name") val periodName: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    @SerializedName("is_break") val isBreak: Int
)

// ── Learning Materials ────────────────────────────────────────────────────────
data class MaterialsData(
    val materials: List<Material>,
    val total: Int,
    @SerializedName("total_pages") val totalPages: Int
)

data class Material(
    val id: Int,
    val title: String,
    val description: String? = null,
    val type: String,
    @SerializedName("file_url") val fileUrl: String? = null,
    @SerializedName("class_name") val className: String? = null,
    @SerializedName("subject_name") val subjectName: String? = null,
    @SerializedName("subject_code") val subjectCode: String? = null,
    @SerializedName("uploaded_by_name") val uploadedByName: String? = null,
    @SerializedName("created_at") val createdAt: String
)

// ── Leave ─────────────────────────────────────────────────────────────────────
data class LeaveTypesData(val types: List<LeaveType>)

data class LeaveType(
    val id: Int,
    val name: String,
    @SerializedName("days_allowed") val daysAllowed: Int,
    @SerializedName("is_paid") val isPaid: Int
)

data class LeaveListData(
    val applications: List<LeaveApplication>,
    val total: Int
)

data class LeaveApplication(
    val id: Int,
    @SerializedName("from_date") val fromDate: String,
    @SerializedName("to_date") val toDate: String,
    @SerializedName("total_days") val totalDays: Int,
    val reason: String? = null,
    val status: String,
    val remarks: String? = null,
    @SerializedName("approved_at") val approvedAt: String? = null,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("leave_type") val leaveType: String? = null,
    @SerializedName("is_paid") val isPaid: Int? = null,
    @SerializedName("employee_name") val employeeName: String? = null,
    @SerializedName("approved_by_name") val approvedByName: String? = null
)

// ── Complaints ────────────────────────────────────────────────────────────────
data class ComplaintsData(
    val complaints: List<Complaint>,
    val total: Int
)

data class Complaint(
    val id: Int,
    @SerializedName("complaint_no") val complaintNo: String,
    val category: String? = null,
    val subject: String,
    val description: String? = null,
    val priority: String,
    val status: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("resolved_at") val resolvedAt: String? = null,
    @SerializedName("raised_by_name") val raisedByName: String? = null
)

// ── Calendar ──────────────────────────────────────────────────────────────────
data class CalendarData(
    val from: String,
    val to: String,
    val items: List<CalendarItem>,
    val total: Int
)

data class CalendarItem(
    val id: Int,
    val source: String,
    val title: String,
    val type: String,
    val date: String,
    @SerializedName("end_date") val endDate: String? = null,
    @SerializedName("all_day") val allDay: Boolean? = null,
    val description: String? = null,
    val color: String,
    val venue: String? = null,
    val status: String? = null
)
