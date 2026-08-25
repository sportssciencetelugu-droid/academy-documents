package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BromaDao {
    // User Accounts
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM users WHERE role = 'STUDENT'")
    fun getAllStudents(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM users WHERE role = 'COACH'")
    fun getAllCoaches(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserAccountEntity?

    @Query("SELECT * FROM users WHERE username = :username OR email = :email LIMIT 1")
    suspend fun getUserByUsernameOrEmail(username: String, email: String): UserAccountEntity?

    @Query("SELECT * FROM users WHERE parentEmail = :parentEmail AND role = 'STUDENT'")
    fun getChildrenByParentEmail(parentEmail: String): Flow<List<UserAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccountEntity)

    @Update
    suspend fun updateUser(user: UserAccountEntity)

    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUser(userId: String)

    // Batches
    @Query("SELECT * FROM batches")
    fun getAllBatches(): Flow<List<BatchEntity>>

    @Query("SELECT * FROM batches WHERE batchId = :batchId LIMIT 1")
    suspend fun getBatchById(batchId: String): BatchEntity?

    @Query("SELECT * FROM batches WHERE coachId = :coachId")
    fun getBatchesForCoach(coachId: String): Flow<List<BatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: BatchEntity)

    @Update
    suspend fun updateBatch(batch: BatchEntity)

    @Query("DELETE FROM batches WHERE batchId = :batchId")
    suspend fun deleteBatch(batchId: String)

    // Attendance
    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance WHERE batchId = :batchId AND date = :date")
    fun getAttendanceForBatchAndDate(batchId: String, date: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getTodayAttendance(date: String): Flow<List<AttendanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(list: List<AttendanceRecordEntity>)

    // Fee Items
    @Query("SELECT * FROM fee_items")
    fun getAllFeeItems(): Flow<List<FeeItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeeItem(feeItem: FeeItemEntity)

    @Query("DELETE FROM fee_items WHERE feeId = :feeId")
    suspend fun deleteFeeItem(feeId: String)

    // Payment Records
    @Query("SELECT * FROM payment_records ORDER BY paymentDate DESC")
    fun getAllPaymentRecords(): Flow<List<PaymentRecordEntity>>

    @Query("SELECT * FROM payment_records WHERE studentId = :studentId ORDER BY paymentDate DESC")
    fun getPaymentsForStudent(studentId: String): Flow<List<PaymentRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentRecord(payment: PaymentRecordEntity)

    @Update
    suspend fun updatePaymentRecord(payment: PaymentRecordEntity)

    // Certificates
    @Query("SELECT * FROM certificates ORDER BY issueDate DESC")
    fun getAllCertificates(): Flow<List<CertificateEntity>>

    @Query("SELECT * FROM certificates WHERE studentId = :studentId ORDER BY issueDate DESC")
    fun getCertificatesForStudent(studentId: String): Flow<List<CertificateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertificate(certificate: CertificateEntity)

    @Update
    suspend fun updateCertificate(certificate: CertificateEntity)

    // Calendar Events
    @Query("SELECT * FROM calendar_events ORDER BY startDate ASC")
    fun getAllCalendarEvents(): Flow<List<CalendarEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarEvent(event: CalendarEventEntity)

    @Query("DELETE FROM calendar_events WHERE eventId = :eventId")
    suspend fun deleteCalendarEvent(eventId: String)

    // Announcements
    @Query("SELECT * FROM announcements ORDER BY publishDate DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity)

    @Query("DELETE FROM announcements WHERE announcementId = :announcementId")
    suspend fun deleteAnnouncement(announcementId: String)

    // Student Requests
    @Query("SELECT * FROM student_requests ORDER BY requestDate DESC")
    fun getAllStudentRequests(): Flow<List<StudentRequestEntity>>

    @Query("SELECT * FROM student_requests WHERE studentId = :studentId ORDER BY requestDate DESC")
    fun getRequestsForStudent(studentId: String): Flow<List<StudentRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudentRequest(request: StudentRequestEntity)

    @Update
    suspend fun updateStudentRequest(request: StudentRequestEntity)

    // Admin Settings
    @Query("SELECT * FROM admin_settings WHERE key = 'MAIN' LIMIT 1")
    fun getAdminSettings(): Flow<AdminSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAdminSettings(settings: AdminSettingsEntity)

    // Dojo Rules
    @Query("SELECT * FROM dojo_rules ORDER BY ruleNumber ASC")
    fun getAllDojoRules(): Flow<List<DojoRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDojoRule(rule: DojoRuleEntity)

    @Query("DELETE FROM dojo_rules WHERE ruleId = :ruleId")
    suspend fun deleteDojoRule(ruleId: String)

    // Achievements
    @Query("SELECT * FROM achievements ORDER BY date DESC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE studentId = :studentId ORDER BY date DESC")
    fun getAchievementsForStudent(studentId: String): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)

    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    @Query("DELETE FROM achievements WHERE achievementId = :achievementId")
    suspend fun deleteAchievement(achievementId: String)

    // Tournaments
    @Query("SELECT * FROM tournaments ORDER BY startDate ASC")
    fun getAllTournaments(): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE isPublished = 1 ORDER BY startDate ASC")
    fun getPublishedTournaments(): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE tournamentId = :tournamentId LIMIT 1")
    suspend fun getTournamentById(tournamentId: String): TournamentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: TournamentEntity)

    @Update
    suspend fun updateTournament(tournament: TournamentEntity)

    @Query("DELETE FROM tournaments WHERE tournamentId = :tournamentId")
    suspend fun deleteTournament(tournamentId: String)

    // Academy Leadership
    @Query("SELECT * FROM academy_leadership ORDER BY displayOrder ASC")
    fun getAllLeadership(): Flow<List<AcademyLeadershipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeadership(leader: AcademyLeadershipEntity)

    @Update
    suspend fun updateLeadership(leader: AcademyLeadershipEntity)

    @Query("DELETE FROM academy_leadership WHERE leadershipId = :leadershipId")
    suspend fun deleteLeadership(leadershipId: String)

    // Academy Standards
    @Query("SELECT * FROM academy_standards ORDER BY orderNumber ASC")
    fun getAllStandards(): Flow<List<AcademyStandardEntity>>

    @Query("SELECT * FROM academy_standards WHERE category = :category ORDER BY orderNumber ASC")
    fun getStandardsByCategory(category: String): Flow<List<AcademyStandardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStandard(standard: AcademyStandardEntity)

    @Update
    suspend fun updateStandard(standard: AcademyStandardEntity)

    @Query("DELETE FROM academy_standards WHERE standardId = :standardId")
    suspend fun deleteStandard(standardId: String)

    // Chat Messages (Direct communication between Students/Coaches and Admin)
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE senderId = :userId OR recipientId = :userId ORDER BY timestamp ASC")
    fun getChatMessagesForUser(userId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("UPDATE chat_messages SET isRead = 1 WHERE senderId = :senderId AND recipientId = 'ADMIN'")
    suspend fun markMessagesAsReadForUser(senderId: String)

    @Query("DELETE FROM chat_messages WHERE messageId = :messageId")
    suspend fun deleteChatMessage(messageId: String)

    // Training Programs
    @Query("SELECT * FROM training_programs ORDER BY displayOrder ASC")
    fun getAllTrainingPrograms(): Flow<List<TrainingProgramEntity>>

    @Query("SELECT * FROM training_programs WHERE isActive = 1 ORDER BY displayOrder ASC")
    fun getActiveTrainingPrograms(): Flow<List<TrainingProgramEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingProgram(program: TrainingProgramEntity)

    @Update
    suspend fun updateTrainingProgram(program: TrainingProgramEntity)

    @Query("DELETE FROM training_programs WHERE programId = :programId")
    suspend fun deleteTrainingProgram(programId: String)

    // Coach Availabilities
    @Query("SELECT * FROM coach_availabilities ORDER BY date DESC")
    fun getAllCoachAvailabilities(): Flow<List<CoachAvailabilityEntity>>

    @Query("SELECT * FROM coach_availabilities WHERE coachId = :coachId ORDER BY date DESC")
    fun getAvailabilityForCoach(coachId: String): Flow<List<CoachAvailabilityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoachAvailability(availability: CoachAvailabilityEntity)

    @Update
    suspend fun updateCoachAvailability(availability: CoachAvailabilityEntity)

    @Query("DELETE FROM coach_availabilities WHERE availabilityId = :availabilityId")
    suspend fun deleteCoachAvailability(availabilityId: String)
}

