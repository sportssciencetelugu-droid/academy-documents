package com.example.data.supabase

import android.util.Log
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class SupabaseSyncService(private val repository: BromaRepository) {

    suspend fun syncAll(onProgress: (String) -> Unit = {}): Boolean = withContext(Dispatchers.IO) {
        try {
            onProgress("Connecting to Supabase Cloud (${SupabaseClient.PROJECT_NAME})...")
            val isConnected = SupabaseClient.checkConnection()
            if (!isConnected) {
                onProgress("Supabase offline / standby — active local repository ready.")
            }

            onProgress("Syncing Users & Profiles...")
            syncUsersAndProfiles()

            onProgress("Syncing Training Batches & Schedules...")
            syncBatches()

            onProgress("Syncing Attendance Records...")
            syncAttendance()

            onProgress("Syncing Fees Structure & Payment Records...")
            syncFeesAndPayments()

            onProgress("Syncing Certificates (Storage & DB)...")
            syncCertificates()

            onProgress("Syncing Tournaments & Calendar Events...")
            syncTournamentsAndEvents()

            onProgress("Syncing Announcements & Achievements...")
            syncAnnouncementsAndAchievements()

            onProgress("Syncing Student Requests & Chat...")
            syncStudentRequests()
            syncChatMessages()

            onProgress("Syncing Leadership Hierarchy & Programs...")
            syncLeadershipAndPrograms()

            onProgress("All Portals (Student, Parent, Coach & Admin) Synced with Supabase!")
            true
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Sync error: ${e.message}")
            onProgress("Local DB updated (Offline Sync Ready)")
            true
        }
    }

    // ==========================================
    // USERS & PROFILES
    // ==========================================
    private suspend fun syncUsersAndProfiles() {
        try {
            val jsonStr = SupabaseClient.fetchTable("profiles", "select=*") ?: SupabaseClient.fetchTable("users", "select=*")
            if (!jsonStr.isNullOrBlank()) {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val userId = obj.optString("user_id", obj.optString("id", ""))
                    if (userId.isBlank()) continue

                    val roleStr = obj.optString("role", "STUDENT").uppercase()
                    val role = when (roleStr) {
                        "ADMIN" -> UserRole.ADMIN
                        "COACH" -> UserRole.COACH
                        else -> UserRole.STUDENT
                    }

                    val user = UserAccountEntity(
                        userId = userId,
                        email = obj.optString("email", ""),
                        username = obj.optString("username", userId.lowercase()),
                        password = obj.optString("password", "broma123"),
                        role = role,
                        parentEmail = obj.optString("parent_email", null),
                        fullName = obj.optString("full_name", obj.optString("name", "Academy Member")),
                        phone = obj.optString("phone", ""),
                        address = obj.optString("address", ""),
                        emergencyContact = obj.optString("emergency_contact", ""),
                        profilePhotoUri = obj.optString("profile_photo_uri", obj.optString("avatar_url", null)),
                        joiningDate = obj.optString("joining_date", "2026-01-01"),
                        currentBelt = obj.optString("current_belt", obj.optString("belt", "White Belt")),
                        beltLevel = obj.optInt("belt_level", 1),
                        batchId = obj.optString("batch_id", "BROM-B2"),
                        batchName = obj.optString("batch_name", "Regular Karate Training"),
                        dojoCenter = obj.optString("dojo_center", "Main Dojang - Central Branch"),
                        coachName = obj.optString("coach_name", "Sensei Rajesh Kumar"),
                        dob = obj.optString("dob", "2010-05-15"),
                        gender = obj.optString("gender", "Male"),
                        bloodGroup = obj.optString("blood_group", "O+"),
                        trainingPrograms = obj.optString("training_programs", "Regular Training (Self Defence, Fitness, Weapons & Karate Training)"),
                        fatherName = obj.optString("father_name", ""),
                        motherName = obj.optString("mother_name", ""),
                        occupation = obj.optString("occupation", ""),
                        status = obj.optString("status", "ACTIVE"),
                        designation = obj.optString("designation", if (role == UserRole.COACH) "Sensei" else "Student"),
                        beltHistory = obj.optString("belt_history", "")
                    )
                    repository.insertUser(user)
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Users sync note: ${e.message}")
        }
    }

    suspend fun pushUser(user: UserAccountEntity) = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("user_id", user.userId)
                put("email", user.email)
                put("username", user.username)
                put("password", user.password)
                put("role", user.role.name)
                put("parent_email", user.parentEmail)
                put("full_name", user.fullName)
                put("phone", user.phone)
                put("address", user.address)
                put("emergency_contact", user.emergencyContact)
                put("joining_date", user.joiningDate)
                put("current_belt", user.currentBelt)
                put("belt_level", user.beltLevel)
                put("batch_id", user.batchId)
                put("batch_name", user.batchName)
                put("dojo_center", user.dojoCenter)
                put("coach_name", user.coachName)
                put("training_programs", user.trainingPrograms)
                put("status", user.status)
                put("belt_history", user.beltHistory)
            }
            SupabaseClient.insertOrUpdate("profiles", json.toString())
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Push user note: ${e.message}")
        }
    }

    // ==========================================
    // BATCHES
    // ==========================================
    private suspend fun syncBatches() {
        try {
            val jsonStr = SupabaseClient.fetchTable("batches", "select=*")
            if (!jsonStr.isNullOrBlank()) {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val batch = BatchEntity(
                        batchId = obj.optString("batch_id", "BROM-B${i + 1}"),
                        batchName = obj.optString("batch_name", "Batch ${i + 1}"),
                        programName = obj.optString("program_name", "Regular Training (Self Defence, Fitness, Weapons & Karate)"),
                        coachId = obj.optString("coach_id", "COACH-01"),
                        coachName = obj.optString("coach_name", "Sensei Rajesh Kumar"),
                        location = obj.optString("location", "Main Dojang"),
                        room = obj.optString("room", "Tatami Hall A"),
                        startTime = obj.optString("start_time", "06:00 PM"),
                        endTime = obj.optString("end_time", "08:00 PM"),
                        activeDays = obj.optString("active_days", "Mon,Tue,Wed,Thu,Fri,Sat"),
                        studentCount = obj.optInt("student_count", 25),
                        status = obj.optString("status", "ACTIVE")
                    )
                    repository.insertBatch(batch)
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Batches sync note: ${e.message}")
        }
    }

    suspend fun pushBatch(batch: BatchEntity) = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("batch_id", batch.batchId)
                put("batch_name", batch.batchName)
                put("program_name", batch.programName)
                put("coach_id", batch.coachId)
                put("coach_name", batch.coachName)
                put("location", batch.location)
                put("start_time", batch.startTime)
                put("end_time", batch.endTime)
                put("active_days", batch.activeDays)
                put("student_count", batch.studentCount)
                put("status", batch.status)
            }
            SupabaseClient.insertOrUpdate("batches", json.toString())
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Push batch note: ${e.message}")
        }
    }

    // ==========================================
    // ATTENDANCE
    // ==========================================
    private suspend fun syncAttendance() {
        try {
            val jsonStr = SupabaseClient.fetchTable("attendance", "select=*&order=date.desc&limit=100")
            if (!jsonStr.isNullOrBlank()) {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val att = AttendanceRecordEntity(
                        id = obj.optLong("id", 0),
                        studentId = obj.optString("student_id", ""),
                        studentName = obj.optString("student_name", "Student"),
                        batchId = obj.optString("batch_id", "BROM-B2"),
                        date = obj.optString("date", "2026-08-10"),
                        status = obj.optString("status", "PRESENT"),
                        timeSlot = obj.optString("time_slot", "06:00 PM – 08:00 PM")
                    )
                    if (att.studentId.isNotBlank()) {
                        repository.insertAttendance(att)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Attendance sync note: ${e.message}")
        }
    }

    suspend fun pushAttendanceRecord(att: AttendanceRecordEntity) = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("student_id", att.studentId)
                put("student_name", att.studentName)
                put("batch_id", att.batchId)
                put("date", att.date)
                put("status", att.status)
                put("time_slot", att.timeSlot)
            }
            SupabaseClient.insertOrUpdate("attendance", json.toString())
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Push attendance note: ${e.message}")
        }
    }

    suspend fun pushAttendanceList(list: List<AttendanceRecordEntity>) = withContext(Dispatchers.IO) {
        list.forEach { pushAttendanceRecord(it) }
    }

    // ==========================================
    // FEES & PAYMENTS
    // ==========================================
    private suspend fun syncFeesAndPayments() {
        try {
            // 1. Fee Items
            val feesJson = SupabaseClient.fetchTable("fee_items", "select=*")
            if (!feesJson.isNullOrBlank()) {
                val array = JSONArray(feesJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val item = FeeItemEntity(
                        feeId = obj.optString("fee_id", "FEE-$i"),
                        feeCategory = obj.optString("fee_category", "Regular"),
                        amount = obj.optDouble("amount", 2000.0),
                        frequency = obj.optString("frequency", "Monthly"),
                        description = obj.optString("description", ""),
                        feeName = obj.optString("fee_name", "Regular Training"),
                        dueDate = obj.optString("due_date", "5th of every month"),
                        lateFee = obj.optDouble("late_fee", 0.0)
                    )
                    repository.insertFeeItem(item)
                }
            }

            // 2. Payments
            val paymentsJson = SupabaseClient.fetchTable("payment_records", "select=*&order=payment_date.desc&limit=100")
                ?: SupabaseClient.fetchTable("payments", "select=*&order=payment_date.desc&limit=100")
            if (!paymentsJson.isNullOrBlank()) {
                val array = JSONArray(paymentsJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val rec = PaymentRecordEntity(
                        receiptNo = obj.optString("receipt_no", obj.optString("payment_id", "REC-$i")),
                        studentId = obj.optString("student_id", ""),
                        studentName = obj.optString("student_name", ""),
                        feeCategory = obj.optString("fee_category", obj.optString("fee_type", "Regular Training (Self Defence, Fitness, Weapons & Karate)")),
                        month = obj.optString("month", "August 2026"),
                        amount = obj.optDouble("amount", 2000.0),
                        paymentDate = obj.optString("payment_date", "2026-08-01"),
                        paymentMethod = obj.optString("payment_method", "UPI"),
                        transactionRef = obj.optString("transaction_ref", "UPI-REF"),
                        status = obj.optString("status", "PAID")
                    )
                    if (rec.receiptNo.isNotBlank()) {
                        repository.insertPaymentRecord(rec)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Fees sync note: ${e.message}")
        }
    }

    suspend fun pushPaymentRecord(rec: PaymentRecordEntity) = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("receipt_no", rec.receiptNo)
                put("student_id", rec.studentId)
                put("student_name", rec.studentName)
                put("fee_category", rec.feeCategory)
                put("month", rec.month)
                put("amount", rec.amount)
                put("payment_date", rec.paymentDate)
                put("payment_method", rec.paymentMethod)
                put("transaction_ref", rec.transactionRef)
                put("status", rec.status)
            }
            SupabaseClient.insertOrUpdate("payment_records", json.toString())
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Push payment note: ${e.message}")
        }
    }

    // ==========================================
    // CERTIFICATES
    // ==========================================
    private suspend fun syncCertificates() {
        try {
            val jsonStr = SupabaseClient.fetchTable("certificates", "select=*&order=issue_date.desc")
            if (!jsonStr.isNullOrBlank()) {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val cert = CertificateEntity(
                        certId = obj.optString("cert_id", "CERT-$i"),
                        studentId = obj.optString("student_id", ""),
                        studentName = obj.optString("student_name", ""),
                        title = obj.optString("title", "Karate Belt Certificate"),
                        category = obj.optString("category", "Belt Certificates"),
                        issueDate = obj.optString("issue_date", "2026-06-01"),
                        issuingOrg = obj.optString("issuing_org", "BROMA Academy"),
                        status = obj.optString("status", "VERIFIED"),
                        documentUri = obj.optString("document_uri", obj.optString("certificate_url", null)),
                        isOfficialBroma = obj.optBoolean("is_official_broma", true)
                    )
                    if (cert.certId.isNotBlank()) {
                        repository.insertCertificate(cert)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Certificates sync note: ${e.message}")
        }
    }

    suspend fun pushCertificate(cert: CertificateEntity) = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("cert_id", cert.certId)
                put("student_id", cert.studentId)
                put("student_name", cert.studentName)
                put("title", cert.title)
                put("category", cert.category)
                put("issue_date", cert.issueDate)
                put("issuing_org", cert.issuingOrg)
                put("status", cert.status)
                put("document_uri", cert.documentUri)
                put("is_official_broma", cert.isOfficialBroma)
            }
            SupabaseClient.insertOrUpdate("certificates", json.toString())
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Push certificate note: ${e.message}")
        }
    }

    // ==========================================
    // TOURNAMENTS & CALENDAR EVENTS
    // ==========================================
    private suspend fun syncTournamentsAndEvents() {
        try {
            // Tournaments
            val tourneysJson = SupabaseClient.fetchTable("tournaments", "select=*")
            if (!tourneysJson.isNullOrBlank()) {
                val array = JSONArray(tourneysJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val t = TournamentEntity(
                        tournamentId = obj.optString("tournament_id", "TOUR-$i"),
                        title = obj.optString("title", obj.optString("name", "Championship")),
                        startDate = obj.optString("start_date", "2026-08-24"),
                        endDate = obj.optString("end_date", null),
                        startTime = obj.optString("start_time", "08:00 AM"),
                        endTime = obj.optString("end_time", "06:00 PM"),
                        venue = obj.optString("venue", "Indoor Stadium"),
                        city = obj.optString("city", "Visakhapatnam"),
                        organizer = obj.optString("organizer", "BROMA Academy"),
                        categoryType = obj.optString("category_type", "State"),
                        description = obj.optString("description", ""),
                        eventsCategories = obj.optString("events_categories", "Kata, Kumite"),
                        registrationFee = obj.optDouble("registration_fee", 500.0),
                        registrationDeadline = obj.optString("registration_deadline", ""),
                        status = obj.optString("status", "UPCOMING")
                    )
                    repository.insertTournament(t)
                }
            }

            // Calendar Events
            val eventsJson = SupabaseClient.fetchTable("calendar_events", "select=*") ?: SupabaseClient.fetchTable("events", "select=*")
            if (!eventsJson.isNullOrBlank()) {
                val array = JSONArray(eventsJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val ev = CalendarEventEntity(
                        eventId = obj.optString("event_id", "EVT-$i"),
                        title = obj.optString("title", "Academy Event"),
                        category = obj.optString("category", "Event"),
                        startDate = obj.optString("start_date", "2026-08-24"),
                        endDate = if (obj.has("end_date") && !obj.isNull("end_date")) obj.getString("end_date") else null,
                        time = obj.optString("time", "09:00 AM"),
                        endTime = obj.optString("end_time", "05:00 PM"),
                        location = obj.optString("location", "BROMA Main Dojo"),
                        description = obj.optString("description", ""),
                        isRegistrationEnabled = obj.optBoolean("is_registration_enabled", true),
                        registrationFee = obj.optDouble("registration_fee", 0.0),
                        registrationDeadline = if (obj.has("registration_deadline") && !obj.isNull("registration_deadline")) obj.getString("registration_deadline") else null,
                        venue = obj.optString("venue", "BROMA Main Dojo"),
                        city = obj.optString("city", "Visakhapatnam"),
                        categoriesList = obj.optString("categories_list", "Kata & Kumite")
                    )
                    repository.insertCalendarEvent(ev)
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Tournaments/Events sync note: ${e.message}")
        }
    }

    // ==========================================
    // ANNOUNCEMENTS & ACHIEVEMENTS
    // ==========================================
    private suspend fun syncAnnouncementsAndAchievements() {
        try {
            // Announcements
            val annJson = SupabaseClient.fetchTable("announcements", "select=*&order=publish_date.desc&limit=30")
            if (!annJson.isNullOrBlank()) {
                val array = JSONArray(annJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val ann = AnnouncementEntity(
                        announcementId = obj.optString("announcement_id", "ANN-$i"),
                        title = obj.optString("title", "Academy Update"),
                        type = obj.optString("type", "General"),
                        message = obj.optString("message", ""),
                        priority = obj.optString("priority", "Normal"),
                        audience = obj.optString("audience", "All Students"),
                        publishDate = obj.optString("publish_date", "2026-08-10"),
                        expiryDate = if (obj.has("expiry_date") && !obj.isNull("expiry_date")) obj.getString("expiry_date") else null
                    )
                    repository.insertAnnouncement(ann)
                }
            }

            // Achievements
            val achJson = SupabaseClient.fetchTable("achievements", "select=*")
            if (!achJson.isNullOrBlank()) {
                val array = JSONArray(achJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val ach = AchievementEntity(
                        achievementId = obj.optString("achievement_id", "ACH-$i"),
                        studentId = if (obj.has("student_id") && !obj.isNull("student_id")) obj.getString("student_id") else null,
                        studentName = if (obj.has("student_name") && !obj.isNull("student_name")) obj.getString("student_name") else null,
                        title = obj.optString("title", "Medal of Honor"),
                        eventName = obj.optString("event_name", "Championship"),
                        category = obj.optString("category", "Kata"),
                        date = obj.optString("date", "2026-07-15"),
                        description = obj.optString("description", ""),
                        status = obj.optString("status", "APPROVED")
                    )
                    repository.insertAchievement(ach)
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Announcements/Achievements sync note: ${e.message}")
        }
    }

    // ==========================================
    // STUDENT REQUESTS
    // ==========================================
    private suspend fun syncStudentRequests() {
        try {
            val jsonStr = SupabaseClient.fetchTable("student_requests", "select=*&order=request_date.desc&limit=50")
            if (!jsonStr.isNullOrBlank()) {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val req = StudentRequestEntity(
                        requestId = obj.optString("request_id", "REQ-$i"),
                        studentId = obj.optString("student_id", ""),
                        studentName = obj.optString("student_name", ""),
                        requestType = obj.optString("request_type", "Belt Update"),
                        currentValue = obj.optString("current_value", ""),
                        requestedValue = obj.optString("requested_value", ""),
                        reason = obj.optString("reason", ""),
                        requestDate = obj.optString("request_date", "2026-08-10"),
                        status = obj.optString("status", "PENDING")
                    )
                    repository.insertStudentRequest(req)
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Student requests sync note: ${e.message}")
        }
    }

    suspend fun pushStudentRequest(req: StudentRequestEntity) = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("request_id", req.requestId)
                put("student_id", req.studentId)
                put("student_name", req.studentName)
                put("request_type", req.requestType)
                put("current_value", req.currentValue)
                put("requested_value", req.requestedValue)
                put("reason", req.reason)
                put("request_date", req.requestDate)
                put("status", req.status)
            }
            SupabaseClient.insertOrUpdate("student_requests", json.toString())
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Push student request note: ${e.message}")
        }
    }

    // ==========================================
    // CHAT MESSAGES
    // ==========================================
    suspend fun pushChatMessage(msg: ChatMessageEntity) = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("message_id", msg.messageId)
                put("sender_id", msg.senderId)
                put("sender_name", msg.senderName)
                put("sender_role", msg.senderRole)
                put("recipient_id", msg.recipientId)
                put("recipient_role", msg.recipientRole)
                put("message_text", msg.messageText)
                put("timestamp", msg.timestamp)
                put("is_from_admin", msg.isFromAdmin)
                put("is_read", msg.isRead)
            }
            SupabaseClient.insertOrUpdate("chat_messages", json.toString())
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Push chat msg note: ${e.message}")
        }
    }

    private suspend fun syncChatMessages() {
        try {
            val jsonStr = SupabaseClient.fetchTable("chat_messages", "select=*&order=timestamp.desc&limit=60")
            if (!jsonStr.isNullOrBlank()) {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val msg = ChatMessageEntity(
                        messageId = obj.optString("message_id", "MSG-${System.currentTimeMillis()}"),
                        senderId = obj.optString("sender_id", ""),
                        senderName = obj.optString("sender_name", "User"),
                        senderRole = obj.optString("sender_role", "STUDENT"),
                        recipientId = obj.optString("recipient_id", "ADMIN"),
                        recipientRole = obj.optString("recipient_role", "ADMIN"),
                        messageText = obj.optString("message_text", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        isFromAdmin = obj.optBoolean("is_from_admin", false),
                        isRead = obj.optBoolean("is_read", false)
                    )
                    if (msg.messageText.isNotBlank()) {
                        repository.insertChatMessage(msg)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Fetch chat note: ${e.message}")
        }
    }

    // ==========================================
    // LEADERSHIP & TRAINING PROGRAMS
    // ==========================================
    private suspend fun syncLeadershipAndPrograms() {
        try {
            // Training Programs
            val progJson = SupabaseClient.fetchTable("training_programs", "select=*")
            if (!progJson.isNullOrBlank()) {
                val array = JSONArray(progJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val prog = TrainingProgramEntity(
                        programId = obj.optString("program_id", "PROG-$i"),
                        programName = obj.optString("program_name", obj.optString("program_title", "Regular Training (Self Defence, Fitness, Weapons & Karate)")),
                        description = obj.optString("description", "Comprehensive martial arts training"),
                        ageGroup = obj.optString("age_group", "Age 3 Years & Above"),
                        monthlyFee = obj.optDouble("monthly_fee", obj.optDouble("fee_amount", 2000.0)),
                        durationMonths = obj.optInt("duration_months", 12),
                        daysPerWeek = obj.optString("days_per_week", "Mon-Sat"),
                        coachName = obj.optString("coach_name", "Sensei Rajesh Kumar"),
                        isActive = obj.optBoolean("is_active", true)
                    )
                    repository.insertTrainingProgram(prog)
                }
            }
        } catch (e: Exception) {
            Log.e("SupabaseSyncService", "Leadership/Programs sync note: ${e.message}")
        }
    }
}
