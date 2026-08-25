package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    STUDENT, COACH, ADMIN
}

@Entity(tableName = "users")
data class UserAccountEntity(
    @PrimaryKey val userId: String, // e.g. "BROMA-0001", "COACH-01", "ADMIN-01"
    val email: String,
    val username: String,
    val password: String,
    val role: UserRole,
    val parentEmail: String? = null,
    val fullName: String,
    val phone: String = "",
    val address: String = "",
    val emergencyContact: String = "",
    val profilePhotoUri: String? = null,
    val joiningDate: String = "2026-01-01",
    val currentBelt: String = "White Belt",
    val beltLevel: Int = 1,
    val batchId: String = "BROM-B2",
    val batchName: String = "Batch 2 (6:00 PM – 8:00 PM)",
    val dojoCenter: String = "Main Dojang - Central Branch",
    val coachName: String = "Sensei Rajesh Kumar",
    val dob: String = "2010-05-15",
    val gender: String = "Male",
    val bloodGroup: String = "O+",
    val trainingPrograms: String = "Regular Karate Training",
    val fatherName: String = "",
    val motherName: String = "",
    val occupation: String = "",
    val status: String = "ACTIVE", // ACTIVE, SUSPENDED, DEACTIVATED
    val designation: String = "Student",
    val specializations: String? = null,
    val experienceYears: Int? = null,
    val bio: String? = null,
    val beltHistory: String = ""
) {
    val avatarUrl: String? get() = profilePhotoUri
}

@Entity(tableName = "batches")
data class BatchEntity(
    @PrimaryKey val batchId: String, // "BROM-B1", "BROM-B2"
    val batchName: String,
    val programName: String = "Regular Karate Training",
    val coachId: String = "COACH-01",
    val coachName: String = "Sensei Rajesh Kumar",
    val location: String = "Main Dojang - Central Branch",
    val room: String = "Tatami Hall A",
    val startTime: String = "06:00 PM",
    val endTime: String = "08:00 PM",
    val activeDays: String = "Mon,Tue,Wed,Thu,Fri,Sat",
    val studentCount: Int = 20,
    val effectiveStartDate: String = "2026-01-01",
    val effectiveEndDate: String = "2026-12-31",
    val status: String = "ACTIVE"
) {
    val scheduleTiming: String get() = "$startTime – $endTime"
}

@Entity(tableName = "attendance")
data class AttendanceRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: String,
    val studentName: String,
    val batchId: String,
    val date: String, // "YYYY-MM-DD" e.g., "2026-08-10"
    val status: String, // "PRESENT", "ABSENT", "LATE", "LEAVE"
    val timeSlot: String
)

@Entity(tableName = "fee_items")
data class FeeItemEntity(
    @PrimaryKey val feeId: String,
    val feeCategory: String = "Monthly",
    val amount: Double = 0.0,
    val frequency: String = "Monthly", // "Monthly", "One-Time", "Per Course"
    val description: String = "",
    val feeName: String = feeCategory,
    val dueDate: String = "5th of every month",
    val lateFee: Double = 0.0
) {
    val feeItemId: String get() = feeId
    val category: String get() = feeCategory
}

@Entity(tableName = "payment_records")
data class PaymentRecordEntity(
    @PrimaryKey val receiptNo: String, // e.g. "BROM-REC-2026-00124"
    val studentId: String,
    val studentName: String,
    val feeCategory: String,
    val month: String, // "August 2026"
    val amount: Double,
    val paymentDate: String,
    val paymentMethod: String, // "UPI", "QR Code", "Cash"
    val transactionRef: String,
    val status: String // "PAID", "DUE", "OVERDUE", "VERIFICATION_PENDING"
)

@Entity(tableName = "certificates")
data class CertificateEntity(
    @PrimaryKey val certId: String,
    val studentId: String,
    val studentName: String,
    val title: String,
    val category: String, // "Belt Certificates", "Tournament Certificates", "Seminar Certificates", "Achievement Certificates", "Course Certificates"
    val issueDate: String,
    val issuingOrg: String,
    val status: String, // "VERIFIED", "PENDING_VERIFICATION", "REJECTED"
    val documentUri: String? = null,
    val isOfficialBroma: Boolean = true
)

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey val eventId: String,
    val title: String,
    val category: String, // "Tournament", "Seminar", "Belt Examination", "Ceremony", "Holiday", "Special Training", "Training", "Academy Event", "Other"
    val startDate: String, // "2026-08-24"
    val endDate: String? = null,
    val time: String, // "06:00 PM"
    val endTime: String? = "08:00 PM",
    val location: String,
    val description: String,
    val posterUrl: String? = null,
    val pdfUrl: String? = null,
    val isRegistrationEnabled: Boolean = false,
    val registrationFee: Double = 0.0,
    val registrationDeadline: String? = null,
    val venue: String? = null,
    val city: String? = null,
    val categoriesList: String? = null // "Kata, Kumite, Weight Categories"
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey val announcementId: String,
    val title: String,
    val type: String, // "General", "Important", "Urgent"
    val message: String,
    val priority: String, // "Normal", "Important", "Urgent"
    val audience: String, // "All Students", "Parents", "Batch 2", "Coaches"
    val publishDate: String,
    val expiryDate: String? = null,
    val posterUri: String? = null,
    val pdfUri: String? = null,
    val isPopupOnLaunch: Boolean = false
)

@Entity(tableName = "student_requests")
data class StudentRequestEntity(
    @PrimaryKey val requestId: String,
    val studentId: String,
    val studentName: String,
    val requestType: String, // "Belt Update", "Batch Transfer", "Profile Detail"
    val currentValue: String,
    val requestedValue: String,
    val reason: String,
    val requestDate: String,
    val status: String // "PENDING", "APPROVED", "REJECTED"
)

@Entity(tableName = "admin_settings")
data class AdminSettingsEntity(
    @PrimaryKey val key: String = "MAIN",
    val upiId: String = "bromaacademy@upi",
    val qrCodeUri: String? = null,
    val logoUri: String? = null,
    val academyName: String = "BRUCELEE RAJ OLYMPIC MARTIALARTS ACADEMY (BROMAA)",
    val academyShortName: String = "BROMAA",
    val registrationNumber: String = "Regd.MP-23570",
    val affiliation: String = "AFFILIATED TO MODERN SHITO-RYU KARATE DO ASSOCIATION (MSKA)",
    val academyPhone: String = "8374632364",
    val academyPhone2: String = "6309735840",
    val academyEmail: String = "info@bromaacademy.com",
    val academyAddress: String = "2ND FLOOR, GEETHA HOSPITAL, NEETU ENUGU BOMMA, KAKATEEYA ITI JUNCTION, BC ROAD, GAJUWAKA",
    val paymentInstructions: String = "Scan QR Code or pay via UPI ID. Enter reference number after payment.",
    val paymentPhone: String = "8374632364",
    val paymentReceiverName: String = "BROMA Martial Arts",
    val website: String = "www.bromaacademy.org",
    val tagline1: String = "SELF DEFENCE",
    val tagline2: String = "FITNESS",
    val admissionsNote: String = "ADMISSIONS OPEN FOR BOYS & GIRLS (Age 3 Years & Above)",
    val logoUpdatedTimestamp: String? = "2026-08-14 10:30 AM",
    val logoUpdatedBy: String? = "Admin"
)

@Entity(tableName = "dojo_rules")
data class DojoRuleEntity(
    @PrimaryKey val ruleId: String,
    val ruleNumber: Int,
    val ruleText: String,
    val category: String = "GENERAL"
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val achievementId: String,
    val studentId: String? = null,
    val studentName: String? = null,
    val title: String,
    val eventName: String,
    val category: String,
    val date: String,
    val description: String = "",
    val photoUri: String? = null,
    val status: String = "APPROVED" // "PENDING_REQUEST", "APPROVED", "REJECTED"
)

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey val tournamentId: String,
    val title: String,
    val startDate: String,
    val endDate: String? = null,
    val startTime: String = "08:00 AM",
    val endTime: String? = "06:00 PM",
    val venue: String,
    val city: String = "Visakhapatnam",
    val organizer: String = "BROMA Academy",
    val categoryType: String = "State", // "District", "State", "National", "International", "Inter-Dojo"
    val description: String = "",
    val eventsCategories: String = "Kata, Kumite, Team Kata, Weapon Kata",
    val registrationFee: Double = 500.0,
    val registrationDeadline: String = "",
    val eligibility: String = "All Belts & Age Groups",
    val importantInstructions: String = "Arrive in full official Gi with protective gear.",
    val contactPerson: String? = "Sensei Rajesh Kumar",
    val contactPhone: String? = "8374632364",
    val posterUrl: String? = null,
    val prizeDetails: String? = null,
    val circularPdfUrl: String? = null,
    val status: String = "UPCOMING", // "UPCOMING", "ONGOING", "COMPLETED", "CANCELLED"
    val isPublished: Boolean = true
)

@Entity(tableName = "academy_leadership")
data class AcademyLeadershipEntity(
    @PrimaryKey val leadershipId: String,
    val fullName: String,
    val postTitle: String, // "Director & Founder", "Chairman", "President", "General Secretary", "Treasurer", "Chief Coach"
    val rankOrBelt: String = "Black Belt 8th Dan",
    val photoUri: String? = null,
    val contactPhone: String = "",
    val contactEmail: String = "",
    val displayOrder: Int = 1,
    val messageOrBio: String = "",
    val isExecutiveBoard: Boolean = true
) {
    val name: String get() = fullName
    val designation: String get() = postTitle
}

@Entity(tableName = "academy_standards")
data class AcademyStandardEntity(
    @PrimaryKey val standardId: String,
    val title: String,
    val category: String = "DOJO_CODE", // "DOJO_CODE", "BELT_REQUIREMENTS", "EXAM_RULES"
    val description: String,
    val orderNumber: Int = 1
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val messageId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: String, // "STUDENT", "COACH", "ADMIN"
    val recipientId: String = "ADMIN", // "ADMIN" or specific user ID
    val recipientRole: String = "ADMIN",
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromAdmin: Boolean = false,
    val isRead: Boolean = false
)

@Entity(tableName = "training_programs")
data class TrainingProgramEntity(
    @PrimaryKey val programId: String,
    val programTitle: String = "Regular Karate Training", // "Regular Karate Training", "Black Belt Master Course", "Self Defence & Fitness", "Weapon & Combat Mastery (Nunchaku/Bo/Sai)"
    val feeAmount: Double = 2000.0,
    val feeFrequency: String = "Monthly", // "Monthly", "Quarterly", "Annual", "Course Fee"
    val targetAudience: String = "Boys & Girls (Age 3+)",
    val durationText: String = "Regular / Continuous",
    val syllabusOverview: String = "",
    val scheduleSummary: String = "Mon-Sat (Batch 1: 5-6 PM, Batch 2: 6-8 PM)",
    val isActive: Boolean = true,
    val displayOrder: Int = 1,
    val programName: String = programTitle,
    val description: String = syllabusOverview,
    val ageGroup: String = targetAudience,
    val monthlyFee: Double = feeAmount,
    val durationMonths: Int = 12,
    val daysPerWeek: String = scheduleSummary,
    val coachName: String = "Sensei Rajesh Kumar"
)

@Entity(tableName = "coach_availabilities")
data class CoachAvailabilityEntity(
    @PrimaryKey val availabilityId: String,
    val coachId: String,
    val coachName: String,
    val date: String = "Weekly", // "YYYY-MM-DD" or "Weekly"
    val dayOfWeek: String = "Monday, Wednesday, Friday",
    val startTime: String = "06:00 PM",
    val endTime: String = "08:00 PM",
    val location: String = "Main Dojang - Tatami Hall A",
    val status: String = "AVAILABLE", // "AVAILABLE", "ON_LEAVE", "BUSY", "SPECIAL_DUTY"
    val shiftTiming: String = "5:00 PM – 8:00 PM",
    val notes: String = "",
    val updatedBy: String = "COACH" // "COACH" or "ADMIN"
)



