package com.example.data

import kotlinx.coroutines.flow.Flow

class BromaRepository(private val dao: BromaDao) {
    // Users
    val allUsers: Flow<List<UserAccountEntity>> = dao.getAllUsers()
    val allStudents: Flow<List<UserAccountEntity>> = dao.getAllStudents()
    val allCoaches: Flow<List<UserAccountEntity>> = dao.getAllCoaches()

    suspend fun getUserById(userId: String) = dao.getUserById(userId)
    suspend fun getUserByUsernameOrEmail(username: String, email: String) = dao.getUserByUsernameOrEmail(username, email)
    fun getChildrenByParentEmail(parentEmail: String) = dao.getChildrenByParentEmail(parentEmail)
    suspend fun insertUser(user: UserAccountEntity) = dao.insertUser(user)
    suspend fun updateUser(user: UserAccountEntity) = dao.updateUser(user)
    suspend fun deleteUser(userId: String) = dao.deleteUser(userId)

    // Batches
    val allBatches: Flow<List<BatchEntity>> = dao.getAllBatches()
    suspend fun getBatchById(batchId: String) = dao.getBatchById(batchId)
    fun getBatchesForCoach(coachId: String) = dao.getBatchesForCoach(coachId)
    suspend fun insertBatch(batch: BatchEntity) = dao.insertBatch(batch)
    suspend fun updateBatch(batch: BatchEntity) = dao.updateBatch(batch)
    suspend fun deleteBatch(batchId: String) = dao.deleteBatch(batchId)

    // Attendance
    fun getAttendanceForStudent(studentId: String) = dao.getAttendanceForStudent(studentId)
    fun getAttendanceForBatchAndDate(batchId: String, date: String) = dao.getAttendanceForBatchAndDate(batchId, date)
    fun getTodayAttendance(date: String) = dao.getTodayAttendance(date)
    suspend fun insertAttendance(attendance: AttendanceRecordEntity) = dao.insertAttendance(attendance)
    suspend fun insertAttendanceList(list: List<AttendanceRecordEntity>) = dao.insertAttendanceList(list)

    // Fees & Payments
    val allFeeItems: Flow<List<FeeItemEntity>> = dao.getAllFeeItems()
    suspend fun insertFeeItem(feeItem: FeeItemEntity) = dao.insertFeeItem(feeItem)
    suspend fun deleteFeeItem(feeId: String) = dao.deleteFeeItem(feeId)

    val allPaymentRecords: Flow<List<PaymentRecordEntity>> = dao.getAllPaymentRecords()
    fun getPaymentsForStudent(studentId: String) = dao.getPaymentsForStudent(studentId)
    suspend fun insertPaymentRecord(payment: PaymentRecordEntity) = dao.insertPaymentRecord(payment)
    suspend fun updatePaymentRecord(payment: PaymentRecordEntity) = dao.updatePaymentRecord(payment)

    // Certificates
    val allCertificates: Flow<List<CertificateEntity>> = dao.getAllCertificates()
    fun getCertificatesForStudent(studentId: String) = dao.getCertificatesForStudent(studentId)
    suspend fun insertCertificate(certificate: CertificateEntity) = dao.insertCertificate(certificate)
    suspend fun updateCertificate(certificate: CertificateEntity) = dao.updateCertificate(certificate)

    // Calendar
    val allCalendarEvents: Flow<List<CalendarEventEntity>> = dao.getAllCalendarEvents()
    suspend fun insertCalendarEvent(event: CalendarEventEntity) = dao.insertCalendarEvent(event)
    suspend fun deleteCalendarEvent(eventId: String) = dao.deleteCalendarEvent(eventId)

    // Announcements
    val allAnnouncements: Flow<List<AnnouncementEntity>> = dao.getAllAnnouncements()
    suspend fun insertAnnouncement(announcement: AnnouncementEntity) = dao.insertAnnouncement(announcement)
    suspend fun deleteAnnouncement(announcementId: String) = dao.deleteAnnouncement(announcementId)

    // Student Requests
    val allStudentRequests: Flow<List<StudentRequestEntity>> = dao.getAllStudentRequests()
    fun getRequestsForStudent(studentId: String) = dao.getRequestsForStudent(studentId)
    suspend fun insertStudentRequest(request: StudentRequestEntity) = dao.insertStudentRequest(request)
    suspend fun updateStudentRequest(request: StudentRequestEntity) = dao.updateStudentRequest(request)

    // Admin Settings
    val adminSettings: Flow<AdminSettingsEntity?> = dao.getAdminSettings()
    suspend fun updateAdminSettings(settings: AdminSettingsEntity) = dao.insertOrUpdateAdminSettings(settings)

    // Dojo Rules
    val allDojoRules: Flow<List<DojoRuleEntity>> = dao.getAllDojoRules()
    suspend fun insertDojoRule(rule: DojoRuleEntity) = dao.insertDojoRule(rule)
    suspend fun deleteDojoRule(ruleId: String) = dao.deleteDojoRule(ruleId)

    // Achievements
    val allAchievements: Flow<List<AchievementEntity>> = dao.getAllAchievements()
    fun getAchievementsForStudent(studentId: String) = dao.getAchievementsForStudent(studentId)
    suspend fun insertAchievement(achievement: AchievementEntity) = dao.insertAchievement(achievement)
    suspend fun updateAchievement(achievement: AchievementEntity) = dao.updateAchievement(achievement)
    suspend fun deleteAchievement(achievementId: String) = dao.deleteAchievement(achievementId)

    // Tournaments
    val allTournaments: Flow<List<TournamentEntity>> = dao.getAllTournaments()
    val publishedTournaments: Flow<List<TournamentEntity>> = dao.getPublishedTournaments()
    suspend fun getTournamentById(tournamentId: String) = dao.getTournamentById(tournamentId)
    suspend fun insertTournament(tournament: TournamentEntity) = dao.insertTournament(tournament)
    suspend fun updateTournament(tournament: TournamentEntity) = dao.updateTournament(tournament)
    suspend fun deleteTournament(tournamentId: String) = dao.deleteTournament(tournamentId)

    // Academy Leadership
    val allLeadership: Flow<List<AcademyLeadershipEntity>> = dao.getAllLeadership()
    suspend fun insertLeadership(leader: AcademyLeadershipEntity) = dao.insertLeadership(leader)
    suspend fun updateLeadership(leader: AcademyLeadershipEntity) = dao.updateLeadership(leader)
    suspend fun deleteLeadership(leadershipId: String) = dao.deleteLeadership(leadershipId)

    // Academy Standards
    val allStandards: Flow<List<AcademyStandardEntity>> = dao.getAllStandards()
    fun getStandardsByCategory(category: String) = dao.getStandardsByCategory(category)
    suspend fun insertStandard(standard: AcademyStandardEntity) = dao.insertStandard(standard)
    suspend fun updateStandard(standard: AcademyStandardEntity) = dao.updateStandard(standard)
    suspend fun deleteStandard(standardId: String) = dao.deleteStandard(standardId)

    // Chat Messages
    val allChatMessages: Flow<List<ChatMessageEntity>> = dao.getAllChatMessages()
    fun getChatMessagesForUser(userId: String): Flow<List<ChatMessageEntity>> = dao.getChatMessagesForUser(userId)
    suspend fun insertChatMessage(message: ChatMessageEntity) = dao.insertChatMessage(message)
    suspend fun markMessagesAsReadForUser(userId: String) = dao.markMessagesAsReadForUser(userId)
    suspend fun deleteChatMessage(messageId: String) = dao.deleteChatMessage(messageId)

    // Training Programs
    val allTrainingPrograms: Flow<List<TrainingProgramEntity>> = dao.getAllTrainingPrograms()
    val activeTrainingPrograms: Flow<List<TrainingProgramEntity>> = dao.getActiveTrainingPrograms()
    suspend fun insertTrainingProgram(program: TrainingProgramEntity) = dao.insertTrainingProgram(program)
    suspend fun updateTrainingProgram(program: TrainingProgramEntity) = dao.updateTrainingProgram(program)
    suspend fun deleteTrainingProgram(programId: String) = dao.deleteTrainingProgram(programId)

    // Coach Availabilities
    val allCoachAvailabilities: Flow<List<CoachAvailabilityEntity>> = dao.getAllCoachAvailabilities()
    fun getAvailabilityForCoach(coachId: String): Flow<List<CoachAvailabilityEntity>> = dao.getAvailabilityForCoach(coachId)
    suspend fun insertCoachAvailability(availability: CoachAvailabilityEntity) = dao.insertCoachAvailability(availability)
    suspend fun updateCoachAvailability(availability: CoachAvailabilityEntity) = dao.updateCoachAvailability(availability)
    suspend fun deleteCoachAvailability(availabilityId: String) = dao.deleteCoachAvailability(availabilityId)
}

