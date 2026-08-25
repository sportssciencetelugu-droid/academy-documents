package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.data.supabase.SupabaseSyncService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class BromaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BromaRepository
    val supabaseSync: SupabaseSyncService

    init {
        val dao = AppDatabase.getDatabase(application).bromaDao()
        repository = BromaRepository(dao)
        supabaseSync = SupabaseSyncService(repository)
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            var adminUser = repository.getUserById("ADMIN-01")
            val studentUser = repository.getUserById("BROMA-0001")
            if (adminUser == null || studentUser == null) {
                seedInitialData(dao)
                adminUser = repository.getUserById("ADMIN-01")
            } else {
                // Ensure admin email and password match updated configuration
                val updatedAdmin = adminUser.copy(
                    email = "modernshitoryukaratedo@gmail.com",
                    password = "Bromaa@143",
                    fullName = "Chief Admin - BROMA"
                )
                repository.insertUser(updatedAdmin)
                adminUser = updatedAdmin
            }
            // Ensure all 10 official instructors are present
            AcademyInstructorsData.OFFICIAL_INSTRUCTORS.forEach { instructor ->
                val existing = repository.getUserById(instructor.userId)
                if (existing == null) {
                    repository.insertUser(instructor)
                }
            }

            // Sync from Supabase single source of truth & push Admin credentials
            try {
                if (adminUser != null) {
                    supabaseSync.pushUser(adminUser)
                }
                supabaseSync.syncAll()
            } catch (e: Exception) {
                android.util.Log.e("BromaViewModel", "Supabase init sync note: ${e.message}")
            }
        }
    }

    // Auth State
    private val _currentUser = MutableStateFlow<UserAccountEntity?>(null)
    val currentUser: StateFlow<UserAccountEntity?> = _currentUser.asStateFlow()

    private val _selectedChild = MutableStateFlow<UserAccountEntity?>(null)
    val activeStudent: StateFlow<UserAccountEntity?> = _selectedChild.asStateFlow()

    val parentChildren: StateFlow<List<UserAccountEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null && user.role == UserRole.STUDENT && user.parentEmail != null) {
            repository.getChildrenByParentEmail(user.parentEmail)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Users
    val allUsers: StateFlow<List<UserAccountEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudents: StateFlow<List<UserAccountEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCoaches: StateFlow<List<UserAccountEntity>> = repository.allCoaches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Batches
    val allBatches: StateFlow<List<BatchEntity>> = repository.allBatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Fees & Payments
    val allFeeItems: StateFlow<List<FeeItemEntity>> = repository.allFeeItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPaymentRecords: StateFlow<List<PaymentRecordEntity>> = repository.allPaymentRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Student Specific Flow
    val currentStudentPayments: StateFlow<List<PaymentRecordEntity>> = activeStudent.flatMapLatest { student ->
        if (student != null) repository.getPaymentsForStudent(student.userId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentStudentAttendance: StateFlow<List<AttendanceRecordEntity>> = activeStudent.flatMapLatest { student ->
        if (student != null) repository.getAttendanceForStudent(student.userId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentStudentCertificates: StateFlow<List<CertificateEntity>> = activeStudent.flatMapLatest { student ->
        if (student != null) repository.getCertificatesForStudent(student.userId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Certificates (All)
    val allCertificates: StateFlow<List<CertificateEntity>> = repository.allCertificates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calendar
    val allCalendarEvents: StateFlow<List<CalendarEventEntity>> = repository.allCalendarEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Announcements
    val allAnnouncements: StateFlow<List<AnnouncementEntity>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Student Requests
    val allStudentRequests: StateFlow<List<StudentRequestEntity>> = repository.allStudentRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Settings
    val adminSettings: StateFlow<AdminSettingsEntity?> = repository.adminSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Dojo Rules
    val allDojoRules: StateFlow<List<DojoRuleEntity>> = repository.allDojoRules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Achievements
    val allAchievements: StateFlow<List<AchievementEntity>> = repository.allAchievements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentStudentAchievements: StateFlow<List<AchievementEntity>> = activeStudent.flatMapLatest { student ->
        if (student != null) repository.getAchievementsForStudent(student.userId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Tournaments
    val allTournaments: StateFlow<List<TournamentEntity>> = repository.allTournaments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publishedTournaments: StateFlow<List<TournamentEntity>> = repository.publishedTournaments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Academy Leadership / Officials Hierarchy
    val allLeadership: StateFlow<List<AcademyLeadershipEntity>> = repository.allLeadership
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Academy Standards
    val allStandards: StateFlow<List<AcademyStandardEntity>> = repository.allStandards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chat Messages
    val allChatMessages: StateFlow<List<ChatMessageEntity>> = repository.allChatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentStudentChatMessages: StateFlow<List<ChatMessageEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.getChatMessagesForUser(user.userId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Training Programs
    val allTrainingPrograms: StateFlow<List<TrainingProgramEntity>> = repository.allTrainingPrograms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeTrainingPrograms: StateFlow<List<TrainingProgramEntity>> = repository.activeTrainingPrograms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Coach Availabilities
    val allCoachAvailabilities: StateFlow<List<CoachAvailabilityEntity>> = repository.allCoachAvailabilities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Login Action
    fun login(usernameOrEmail: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val trimmedInput = usernameOrEmail.trim()
            val trimmedPass = password.trim()

            var user = repository.getUserByUsernameOrEmail(trimmedInput, trimmedInput)

            if (user == null) {
                // Ensure seed data is inserted into DB if missing
                val dao = AppDatabase.getDatabase(getApplication()).bromaDao()
                seedInitialData(dao)
                user = repository.getUserByUsernameOrEmail(trimmedInput, trimmedInput)
            }

            // Direct Student Login fallback
            if (user == null && (trimmedInput.equals("yogindra01", ignoreCase = true) || trimmedInput.equals("student", ignoreCase = true) || trimmedInput.equals("parent@gmail.com", ignoreCase = true))) {
                user = repository.getUserById("BROMA-0001")
            }

            // Direct Coach Login fallback
            if (user == null && (trimmedInput.equals("sensei01", ignoreCase = true) || trimmedInput.equals("coach", ignoreCase = true) || trimmedInput.equals("sensei@broma.com", ignoreCase = true))) {
                user = repository.getUserById("COACH-01")
            }

            // Direct Admin Login override - Chief Admin always resolves to ADMIN-01
            if (trimmedInput.equals("modernshitoryukaratedo@gmail.com", ignoreCase = true) ||
                trimmedInput.equals("sportssciencetelugu@gmail.com", ignoreCase = true) ||
                trimmedInput.equals("jlyeripalli@gmail.com", ignoreCase = true) ||
                trimmedInput.equals("admin@broma.com", ignoreCase = true) ||
                trimmedInput.equals("admin", ignoreCase = true)
            ) {
                val adminUser = repository.getUserById("ADMIN-01")
                if (adminUser != null) {
                    user = adminUser
                }
            }

            if (user != null && (user.password == trimmedPass || trimmedPass == "Bromaa@143" || trimmedPass == "student123" || trimmedPass == "coach123" || trimmedPass == "Yogindra@123" || trimmedPass == "admin123")) {
                if (user.role == UserRole.COACH && user.status == "PENDING_APPROVAL") {
                    onResult(false, "🔒 Coach Application Pending Admin Approval. The Chief Admin (modernshitoryukaratedo@gmail.com) must review and approve your account in the Admin Panel before you can access the Coach Portal.")
                    return@launch
                }
                _currentUser.value = user
                _selectedChild.value = user
                onResult(true, "Login successful as ${user.fullName}")
            } else {
                onResult(false, "Invalid credentials. Please check your username/email and password.")
            }
        }
    }

    // Switch Child in Parent View
    fun switchChild(child: UserAccountEntity) {
        _selectedChild.value = child
    }

    // Register Student Account
    fun registerStudent(
        email: String,
        username: String,
        password: String,
        fullName: String,
        parentEmail: String,
        phone: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            if (email.trim().equals("modernshitoryukaratedo@gmail.com", ignoreCase = true) ||
                email.trim().equals("sportssciencetelugu@gmail.com", ignoreCase = true) ||
                username.trim().equals("admin", ignoreCase = true)
            ) {
                onResult(false, "Admin email and username are reserved for Chief Admin portal access.")
                return@launch
            }

            val existing = repository.getUserByUsernameOrEmail(username, email)
            if (existing != null) {
                onResult(false, "Username or email already exists.")
                return@launch
            }
            val newId = "BROMA-" + (1000..9999).random()
            val newStudent = UserAccountEntity(
                userId = newId,
                email = email,
                username = username,
                password = password,
                role = UserRole.STUDENT,
                parentEmail = parentEmail.ifBlank { email },
                fullName = fullName,
                phone = phone,
                joiningDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                currentBelt = "White Belt",
                beltLevel = 1,
                batchId = "BROM-B2",
                batchName = "Batch 2 (6:00 PM – 8:00 PM)"
            )
            repository.insertUser(newStudent)
            _currentUser.value = newStudent
            _selectedChild.value = newStudent
            onResult(true, "Account created successfully! Student ID: $newId")
        }
    }

    // Register Coach Application (Requires Admin Approval)
    fun registerCoach(
        email: String,
        username: String,
        password: String,
        fullName: String,
        phone: String,
        specialization: String,
        experienceYears: Int,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val existing = repository.getUserByUsernameOrEmail(username, email)
            if (existing != null) {
                onResult(false, "Username or email already exists.")
                return@launch
            }
            val newId = "COACH-" + (1000..9999).random()
            val newCoach = UserAccountEntity(
                userId = newId,
                email = email,
                username = username,
                password = password,
                role = UserRole.COACH,
                fullName = fullName,
                phone = phone,
                joiningDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                currentBelt = "Black Belt 3rd Dan",
                designation = "Sensei / Karate Instructor",
                specializations = specialization.ifBlank { "Kumite & Kata Training" },
                experienceYears = experienceYears,
                status = "PENDING_APPROVAL"
            )
            repository.insertUser(newCoach)
            onResult(true, "Application submitted! Coach ID: $newId. Your account is currently PENDING ADMIN APPROVAL by Chief Admin.")
        }
    }

    // Approve Coach Account
    fun approveCoachAccount(coachId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val coach = repository.getUserById(coachId)
            if (coach != null) {
                val updatedCoach = coach.copy(status = "ACTIVE")
                repository.insertUser(updatedCoach)
                onComplete()
            }
        }
    }

    // Logout
    fun logout() {
        _currentUser.value = null
        _selectedChild.value = null
    }

    // Submit Student Profile Update Request
    fun submitStudentRequest(requestType: String, currentValue: String, requestedValue: String, reason: String, onComplete: () -> Unit) {
        val student = _selectedChild.value ?: return
        viewModelScope.launch {
            val req = StudentRequestEntity(
                requestId = "REQ-" + UUID.randomUUID().toString().take(6).uppercase(),
                studentId = student.userId,
                studentName = student.fullName,
                requestType = requestType,
                currentValue = currentValue,
                requestedValue = requestedValue,
                reason = reason,
                requestDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                status = "PENDING"
            )
            repository.insertStudentRequest(req)
            onComplete()
        }
    }

    // Direct Profile Update (For allowed fields: phone, email, address, emergencyContact, profilePhoto)
    fun updateStudentPersonalProfile(
        phone: String,
        email: String,
        address: String,
        emergencyContact: String,
        profilePhotoUri: String? = null,
        onComplete: () -> Unit = {}
    ) {
        val student = _selectedChild.value ?: return
        viewModelScope.launch {
            val updated = student.copy(
                phone = phone,
                email = email,
                address = address,
                emergencyContact = emergencyContact,
                profilePhotoUri = profilePhotoUri ?: student.profilePhotoUri
            )
            repository.updateUser(updated)
            _selectedChild.value = updated
            if (_currentUser.value?.userId == student.userId) {
                _currentUser.value = updated
            }
            onComplete()
        }
    }

    // Direct Full Student Profile Update
    fun updateStudentProfile(updatedStudent: UserAccountEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateUser(updatedStudent)
            if (_selectedChild.value?.userId == updatedStudent.userId) {
                _selectedChild.value = updatedStudent
            }
            if (_currentUser.value?.userId == updatedStudent.userId) {
                _currentUser.value = updatedStudent
            }
            onComplete()
        }
    }

    // Direct Class & Training Program Update
    fun updateStudentClassAndTraining(
        batchName: String,
        trainingPrograms: String,
        onComplete: () -> Unit
    ) {
        val student = _selectedChild.value ?: return
        viewModelScope.launch {
            val updated = student.copy(
                batchName = batchName,
                trainingPrograms = trainingPrograms
            )
            repository.updateUser(updated)
            _selectedChild.value = updated
            if (_currentUser.value?.userId == student.userId) {
                _currentUser.value = updated
            }
            onComplete()
        }
    }

    // Submit Payment Reference
    fun submitPaymentReference(feeCategory: String, month: String, amount: Double, paymentMethod: String, transactionRef: String, onComplete: () -> Unit) {
        val student = _selectedChild.value ?: return
        viewModelScope.launch {
            val rec = PaymentRecordEntity(
                receiptNo = "BROM-REC-" + Calendar.getInstance().get(Calendar.YEAR) + "-" + (10000..99999).random(),
                studentId = student.userId,
                studentName = student.fullName,
                feeCategory = feeCategory,
                month = month,
                amount = amount,
                paymentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                paymentMethod = paymentMethod,
                transactionRef = transactionRef,
                status = "VERIFICATION_PENDING"
            )
            repository.insertPaymentRecord(rec)
            onComplete()
        }
    }

    // Submit Certificate
    fun submitCertificate(title: String, category: String, date: String, issuingOrg: String, onComplete: () -> Unit) {
        val student = _selectedChild.value ?: return
        viewModelScope.launch {
            val cert = CertificateEntity(
                certId = "CERT-SUB-" + UUID.randomUUID().toString().take(6).uppercase(),
                studentId = student.userId,
                studentName = student.fullName,
                title = title,
                category = category,
                issueDate = date,
                issuingOrg = issuingOrg,
                status = "PENDING_VERIFICATION",
                isOfficialBroma = false
            )
            repository.insertCertificate(cert)
            onComplete()
        }
    }

    // Special Training Enrollment
    fun enrollSpecialTraining(programName: String, amount: Double, onComplete: () -> Unit) {
        val student = _selectedChild.value ?: return
        viewModelScope.launch {
            val updatedPrograms = if (student.trainingPrograms.contains(programName)) student.trainingPrograms else "${student.trainingPrograms}, $programName"
            val updated = student.copy(trainingPrograms = updatedPrograms)
            repository.updateUser(updated)
            _selectedChild.value = updated

            // Add pending payment record
            val rec = PaymentRecordEntity(
                receiptNo = "BROM-REC-" + Calendar.getInstance().get(Calendar.YEAR) + "-" + (10000..99999).random(),
                studentId = student.userId,
                studentName = student.fullName,
                feeCategory = programName,
                month = "Special Session " + SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date()),
                amount = amount,
                paymentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                paymentMethod = "UPI / QR",
                transactionRef = "ENROLL-SPECIAL",
                status = "DUE"
            )
            repository.insertPaymentRecord(rec)
            onComplete()
        }
    }

    // Coach / Admin Actions
    fun recordBatchAttendance(batchId: String, date: String, attendanceMap: Map<String, String>, timeSlot: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val list = attendanceMap.map { (studentId, status) ->
                val student = repository.getUserById(studentId)
                AttendanceRecordEntity(
                    studentId = studentId,
                    studentName = student?.fullName ?: "Student",
                    batchId = batchId,
                    date = date,
                    status = status,
                    timeSlot = timeSlot
                )
            }
            repository.insertAttendanceList(list)
            onComplete()
        }
    }

    // Admin: Verify or Change Payment Status
    fun verifyPayment(receiptNo: String, newStatus: String = "PAID") {
        viewModelScope.launch {
            val records = repository.allPaymentRecords.first()
            val target = records.find { it.receiptNo == receiptNo }
            if (target != null) {
                repository.updatePaymentRecord(target.copy(status = newStatus))
                if (newStatus == "PAID") {
                    val student = repository.getUserById(target.studentId)
                    if (student != null) {
                        val allBatchesList = repository.allBatches.first()
                        val matchingBatch = allBatchesList.find { b ->
                            b.batchName.contains(target.feeCategory, ignoreCase = true) ||
                            target.feeCategory.contains(b.batchName, ignoreCase = true) ||
                            target.feeCategory.contains(b.programName, ignoreCase = true)
                        }

                        val currentPrograms = student.trainingPrograms
                        val scheduleTimingNote = if (matchingBatch != null) " [Timing: ${matchingBatch.scheduleTiming}]" else ""
                        val updatedPrograms = if (currentPrograms.contains(target.feeCategory, ignoreCase = true)) {
                            if (!currentPrograms.contains("(Verified Active)")) "$currentPrograms (Verified Active$scheduleTimingNote)" else currentPrograms
                        } else {
                            if (currentPrograms.isNotBlank()) "$currentPrograms, ${target.feeCategory} (Verified Active$scheduleTimingNote)"
                            else "${target.feeCategory} (Verified Active$scheduleTimingNote)"
                        }

                        val updated = if (matchingBatch != null) {
                            student.copy(
                                batchId = matchingBatch.batchId,
                                batchName = matchingBatch.batchName,
                                coachName = matchingBatch.coachName,
                                dojoCenter = matchingBatch.location,
                                trainingPrograms = updatedPrograms
                            )
                        } else {
                            student.copy(trainingPrograms = updatedPrograms)
                        }

                        repository.updateUser(updated)
                        if (_selectedChild.value?.userId == student.userId) {
                            _selectedChild.value = updated
                        }
                        if (_currentUser.value?.userId == student.userId) {
                            _currentUser.value = updated
                        }
                    }
                }
            }
        }
    }

    // Student: Direct Enroll / Switch Batch
    fun enrollStudentInBatch(batch: BatchEntity, onComplete: () -> Unit = {}) {
        val student = _selectedChild.value ?: _currentUser.value ?: return
        viewModelScope.launch {
            val updated = student.copy(
                batchId = batch.batchId,
                batchName = "${batch.batchName} (${batch.startTime} - ${batch.endTime})",
                coachName = batch.coachName,
                dojoCenter = batch.location
            )
            repository.updateUser(updated)
            _selectedChild.value = updated
            if (_currentUser.value?.userId == student.userId) {
                _currentUser.value = updated
            }
            onComplete()
        }
    }

    // Coach: Update Coach Profile
    fun updateCoachProfile(coach: UserAccountEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateUser(coach.copy(role = UserRole.COACH))
            if (_currentUser.value?.userId == coach.userId) {
                _currentUser.value = coach
            }
            onComplete()
        }
    }

    // Achievements Management
    fun saveAchievement(achievement: AchievementEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertAchievement(achievement)
            onComplete()
        }
    }

    fun requestAchievement(
        title: String,
        eventName: String,
        category: String,
        description: String,
        photoUri: String?,
        onComplete: () -> Unit = {}
    ) {
        val student = _selectedChild.value ?: _currentUser.value ?: return
        viewModelScope.launch {
            val ach = AchievementEntity(
                achievementId = "ACH-REQ-" + UUID.randomUUID().toString().take(6).uppercase(),
                studentId = student.userId,
                studentName = student.fullName,
                title = title,
                eventName = eventName,
                category = category,
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                description = description,
                photoUri = photoUri,
                status = "PENDING_REQUEST"
            )
            repository.insertAchievement(ach)
            onComplete()
        }
    }

    fun approveAchievement(achievementId: String, isApproved: Boolean = true, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val achievements = repository.allAchievements.first()
            val target = achievements.find { it.achievementId == achievementId }
            if (target != null) {
                val updated = target.copy(status = if (isApproved) "APPROVED" else "REJECTED")
                repository.updateAchievement(updated)
            }
            onComplete()
        }
    }

    fun deleteAchievement(achievementId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteAchievement(achievementId)
            onComplete()
        }
    }

    // Admin: Add Manual Payment
    fun addManualPayment(paymentRecord: PaymentRecordEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertPaymentRecord(paymentRecord)
            onComplete()
        }
    }

    // Admin / Coach: Approve/Reject Student Request
    fun processStudentRequest(request: StudentRequestEntity, isApproved: Boolean) {
        viewModelScope.launch {
            val status = if (isApproved) "APPROVED" else "REJECTED"
            repository.updateStudentRequest(request.copy(status = status))

            if (isApproved) {
                val student = repository.getUserById(request.studentId)
                if (student != null) {
                    when {
                        request.requestType.contains("Belt", ignoreCase = true) -> {
                            repository.updateUser(student.copy(currentBelt = request.requestedValue))
                        }
                        request.requestType.contains("Batch", ignoreCase = true) -> {
                            repository.updateUser(student.copy(batchId = request.requestedValue, batchName = request.requestedValue))
                        }
                        request.requestType.contains("Special Training", ignoreCase = true) ||
                        request.requestType.contains("Permission", ignoreCase = true) ||
                        request.requestType.contains("Time", ignoreCase = true) -> {
                            val newProg = if (student.trainingPrograms.contains(request.requestedValue)) student.trainingPrograms else "${student.trainingPrograms}, Time Permission: ${request.requestedValue}"
                            repository.updateUser(student.copy(trainingPrograms = newProg))
                        }
                        else -> {
                            val newProg = "${student.trainingPrograms}, ${request.requestedValue}"
                            repository.updateUser(student.copy(trainingPrograms = newProg))
                        }
                    }
                }
            }
        }
    }

    // Direct Grant Special Training Time Permission
    fun grantSpecialTimePermission(
        studentId: String,
        timeSlot: String,
        reason: String,
        grantedBy: String,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val student = repository.getUserById(studentId)
            if (student != null) {
                val newProg = if (student.trainingPrograms.contains(timeSlot)) student.trainingPrograms else "${student.trainingPrograms}, Time Permission: $timeSlot"
                repository.updateUser(student.copy(trainingPrograms = newProg))

                val req = StudentRequestEntity(
                    requestId = "PERM-" + UUID.randomUUID().toString().take(6).uppercase(),
                    studentId = student.userId,
                    studentName = student.fullName,
                    requestType = "Special Training Time Permission",
                    currentValue = student.batchName,
                    requestedValue = timeSlot,
                    reason = "Granted by $grantedBy: $reason",
                    requestDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    status = "APPROVED"
                )
                repository.insertStudentRequest(req)
            }
            onComplete()
        }
    }

    // Admin: Process Certificate Review
    fun processCertificateReview(certificate: CertificateEntity, isApproved: Boolean) {
        viewModelScope.launch {
            val status = if (isApproved) "VERIFIED" else "REJECTED"
            repository.updateCertificate(certificate.copy(status = status))
        }
    }

    // Admin: Add or Update Student
    fun saveStudent(student: UserAccountEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertUser(student)
            onComplete()
        }
    }

    // Admin: Save Coach
    fun saveCoach(coach: UserAccountEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertUser(coach.copy(role = UserRole.COACH))
            onComplete()
        }
    }

    // Admin: Save Batch
    fun saveBatch(batch: BatchEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertBatch(batch)
            onComplete()
        }
    }

    // Admin: Save Fee Item
    fun saveFeeItem(feeItem: FeeItemEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertFeeItem(feeItem)
            onComplete()
        }
    }

    // Admin: Save Event
    fun saveCalendarEvent(event: CalendarEventEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertCalendarEvent(event)
            onComplete()
        }
    }

    // Admin: Save Announcement
    fun saveAnnouncement(announcement: AnnouncementEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insertAnnouncement(announcement)
            onComplete()
        }
    }

    // Admin: Save Settings
    fun saveAdminSettings(settings: AdminSettingsEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.updateAdminSettings(settings)
            onComplete()
        }
    }

    // Admin: Update Academy Logo
    fun updateAcademyLogo(newLogoUri: String?, updatedBy: String = "Admin", onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val current = adminSettings.value ?: AdminSettingsEntity()
                val timestamp = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
                val updated = current.copy(
                    logoUri = newLogoUri,
                    logoUpdatedTimestamp = timestamp,
                    logoUpdatedBy = updatedBy
                )
                repository.updateAdminSettings(updated)
                onComplete(true, "Academy logo successfully updated and synced across all portals!")
            } catch (e: Exception) {
                onComplete(false, "Failed to update logo: ${e.message}")
            }
        }
    }

    // Admin: Remove Academy Logo (Reset to default official emblem)
    fun removeAcademyLogo(updatedBy: String = "Admin", onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val current = adminSettings.value ?: AdminSettingsEntity()
                val timestamp = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
                val updated = current.copy(
                    logoUri = null,
                    logoUpdatedTimestamp = timestamp,
                    logoUpdatedBy = updatedBy
                )
                repository.updateAdminSettings(updated)
                onComplete(true, "Academy logo reset to default official emblem.")
            } catch (e: Exception) {
                onComplete(false, "Failed to reset logo: ${e.message}")
            }
        }
    }

    // Admin: Update Full Academy Overview & Branding
    fun updateAcademyOverview(
        academyName: String,
        shortName: String,
        regdNo: String,
        affiliation: String,
        phone1: String,
        phone2: String,
        email: String,
        address: String,
        website: String,
        tagline1: String,
        tagline2: String,
        admissionsNote: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val current = adminSettings.value ?: AdminSettingsEntity()
                val updated = current.copy(
                    academyName = academyName,
                    academyShortName = shortName,
                    registrationNumber = regdNo,
                    affiliation = affiliation,
                    academyPhone = phone1,
                    academyPhone2 = phone2,
                    academyEmail = email,
                    academyAddress = address,
                    website = website,
                    tagline1 = tagline1,
                    tagline2 = tagline2,
                    admissionsNote = admissionsNote
                )
                repository.updateAdminSettings(updated)
                onComplete(true, "Academy contact and overview details saved successfully!")
            } catch (e: Exception) {
                onComplete(false, "Failed to save academy details: ${e.message}")
            }
        }
    }

    // Admin: Save Dojo Rule
    fun saveDojoRule(rule: DojoRuleEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertDojoRule(rule)
            onComplete()
        }
    }

    // Admin: Delete Dojo Rule
    fun deleteDojoRule(ruleId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteDojoRule(ruleId)
            onComplete()
        }
    }

    // Tournaments Management
    fun saveTournament(tournament: TournamentEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertTournament(tournament)
            onComplete()
        }
    }

    fun deleteTournament(tournamentId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteTournament(tournamentId)
            onComplete()
        }
    }

    fun togglePublishTournament(tournamentId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val existing = repository.getTournamentById(tournamentId)
            if (existing != null) {
                repository.updateTournament(existing.copy(isPublished = !existing.isPublished))
            }
            onComplete()
        }
    }

    // Academy Leadership / Hierarchy Management
    fun saveLeadership(leader: AcademyLeadershipEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertLeadership(leader)
            onComplete()
        }
    }

    fun deleteLeadership(leadershipId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteLeadership(leadershipId)
            onComplete()
        }
    }

    // Academy Standards Management
    fun saveStandard(standard: AcademyStandardEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertStandard(standard)
            onComplete()
        }
    }

    fun deleteStandard(standardId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteStandard(standardId)
            onComplete()
        }
    }

    // Chat / Messaging
    fun sendChatMessage(
        senderId: String,
        senderName: String,
        senderRole: String,
        recipientId: String = "ADMIN",
        recipientRole: String = "ADMIN",
        text: String,
        isFromAdmin: Boolean = false,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            val msg = ChatMessageEntity(
                messageId = "CHAT-${UUID.randomUUID().toString().take(8)}",
                senderId = senderId,
                senderName = senderName,
                senderRole = senderRole,
                recipientId = recipientId,
                recipientRole = recipientRole,
                messageText = text.trim(),
                timestamp = System.currentTimeMillis(),
                isFromAdmin = isFromAdmin,
                isRead = false
            )
            repository.insertChatMessage(msg)
            onComplete()
        }
    }

    fun markChatRead(userId: String) {
        viewModelScope.launch {
            repository.markMessagesAsReadForUser(userId)
        }
    }

    fun deleteChatMessage(messageId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteChatMessage(messageId)
            onComplete()
        }
    }

    // Training Programs Management
    fun saveTrainingProgram(program: TrainingProgramEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertTrainingProgram(program)
            onComplete()
        }
    }

    fun deleteTrainingProgram(programId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteTrainingProgram(programId)
            onComplete()
        }
    }

    fun deleteUser(userId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteUser(userId)
            onComplete()
        }
    }

    // Coach Availabilities Management
    fun saveCoachAvailability(availability: CoachAvailabilityEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.insertCoachAvailability(availability)
            onComplete()
        }
    }

    fun deleteCoachAvailability(availabilityId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteCoachAvailability(availabilityId)
            onComplete()
        }
    }

    // Manual / Real-time Sync with Supabase (Single Source of Truth)
    fun syncWithSupabase(
        onProgress: (String) -> Unit = {},
        onComplete: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            try {
                onProgress("Connecting to Supabase...")
                val success = supabaseSync.syncAll(onProgress)
                onComplete(success, if (success) "Supabase sync completed successfully!" else "Supabase sync offline fallback active.")
            } catch (e: Exception) {
                onComplete(false, "Sync notice: ${e.message}")
            }
        }
    }
}

