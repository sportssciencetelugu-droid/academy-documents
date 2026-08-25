package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserAccountEntity::class,
        BatchEntity::class,
        AttendanceRecordEntity::class,
        FeeItemEntity::class,
        PaymentRecordEntity::class,
        CertificateEntity::class,
        CalendarEventEntity::class,
        AnnouncementEntity::class,
        StudentRequestEntity::class,
        AdminSettingsEntity::class,
        DojoRuleEntity::class,
        AchievementEntity::class,
        TournamentEntity::class,
        AcademyLeadershipEntity::class,
        AcademyStandardEntity::class,
        ChatMessageEntity::class,
        TrainingProgramEntity::class,
        CoachAvailabilityEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bromaDao(): BromaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "broma_academy_db"
                )
                    .addCallback(SeedDataCallback(context.applicationContext))
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class SeedDataCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val dao = getDatabase(context).bromaDao()
                seedInitialData(dao)
            }
        }
    }
}

suspend fun seedInitialData(dao: BromaDao) {
    // 1. Seed Users (Admin & 10 Official Academy Coaches/Instructors)
    val adminUser = UserAccountEntity(
        userId = "ADMIN-01",
        email = "modernshitoryukaratedo@gmail.com",
        username = "admin",
        password = "Bromaa@143",
        role = UserRole.ADMIN,
        fullName = "Chief Admin - BROMA",
        phone = "+91 98765 00000",
        address = "BROMA Headquarters, Main Dojo",
        emergencyContact = "+91 98765 00001",
        joiningDate = "2020-01-01",
        currentBelt = "Black Belt 6th Dan",
        designation = "Academy Director"
    )

    val adminUser2 = UserAccountEntity(
        userId = "ADMIN-02",
        email = "admin@broma.com",
        username = "admin_broma",
        password = "admin123",
        role = UserRole.ADMIN,
        fullName = "Academy Director",
        phone = "+91 98765 00002",
        address = "BROMA Headquarters",
        joiningDate = "2020-01-01",
        designation = "Administrator"
    )

    dao.insertUser(adminUser)
    dao.insertUser(adminUser2)

    // Seed 10 Official Academy Instructors & Coaches
    AcademyInstructorsData.OFFICIAL_INSTRUCTORS.forEach { coach ->
        dao.insertUser(coach)
    }

    val student1 = UserAccountEntity(
        userId = "BROMA-0001",
        email = "parent@gmail.com",
        username = "yogindra01",
        password = "student123",
        role = UserRole.STUDENT,
        parentEmail = "parent@gmail.com",
        fullName = "Ch. Yogindra",
        phone = "+91 98765 22222",
        address = "House #42, Green Avenue, City",
        emergencyContact = "+91 98765 33333",
        joiningDate = "2025-06-12",
        currentBelt = "Green Belt",
        beltLevel = 4,
        batchId = "BROM-B2",
        batchName = "Batch 2 (6:00 PM – 8:00 PM)",
        coachName = "Sensei Rajesh Kumar",
        dob = "2012-08-20",
        gender = "Male",
        bloodGroup = "B+",
        trainingPrograms = "Regular Karate Training, Weapon Training",
        fatherName = "Ch. Ramesh",
        motherName = "Ch. Sunita",
        occupation = "Software Engineer",
        status = "ACTIVE",
        beltHistory = "White Belt (Jan 2025 - Master Dojo)|Yellow Belt (Apr 2025 - Sensei Rajesh)|Orange Belt (Aug 2025 - MSKA Exam)|Green Belt (Dec 2025 - Shihan Brucelee Raj)"
    )

    val student2 = UserAccountEntity(
        userId = "BROMA-0002",
        email = "parent@gmail.com",
        username = "rahul02",
        password = "student123",
        role = UserRole.STUDENT,
        parentEmail = "parent@gmail.com",
        fullName = "Ch. Rahul",
        phone = "+91 98765 22222",
        address = "House #42, Green Avenue, City",
        emergencyContact = "+91 98765 33333",
        joiningDate = "2025-08-10",
        currentBelt = "Blue Belt",
        beltLevel = 5,
        batchId = "BROM-B1",
        batchName = "Batch 1 (6:00 AM – 8:00 AM)",
        coachName = "Sensei Rajesh Kumar",
        dob = "2014-04-10",
        gender = "Male",
        bloodGroup = "O+",
        trainingPrograms = "Regular Karate Training",
        fatherName = "Ch. Ramesh",
        motherName = "Ch. Sunita",
        occupation = "Software Engineer",
        status = "ACTIVE",
        beltHistory = "White Belt (Aug 2025 - Master Dojo)|Yellow Belt (Nov 2025 - Sensei Rajesh)|Orange Belt (Feb 2026 - MSKA Exam)|Green Belt (May 2026 - Central Branch)|Blue Belt (Jul 2026 - Shihan Brucelee Raj)"
    )

    dao.insertUser(student1)
    dao.insertUser(student2)

    // 2. Seed Batches (6 Batches from Official Flyer)
    val batch1 = BatchEntity(
        batchId = "BROM-B1",
        batchName = "Batch 1",
        programName = "Kata Training (Black Belts)",
        coachId = "COACH-01",
        coachName = "Shihan.BRUCELEE RAJ",
        location = "BROMAA Dojo, 2nd Floor Geetha Hospital, Gajuwaka",
        startTime = "05:00 AM",
        endTime = "08:00 AM",
        activeDays = "Mon,Tue,Wed,Thu,Fri,Sat",
        studentCount = 28
    )

    val batch2 = BatchEntity(
        batchId = "BROM-B2",
        batchName = "Batch 2",
        programName = "Kata Training (Brown Belts)",
        coachId = "COACH-02",
        coachName = "SHIHAN DAI.A.SOMBABU",
        location = "BROMAA Dojo, 2nd Floor Geetha Hospital, Gajuwaka",
        startTime = "05:00 AM",
        endTime = "06:30 AM",
        activeDays = "Mon,Tue,Wed,Thu,Fri,Sat",
        studentCount = 35
    )

    val batch3 = BatchEntity(
        batchId = "BROM-B3",
        batchName = "Batch 3",
        programName = "Kumite Sparring (Brown Belts)",
        coachId = "COACH-03",
        coachName = "RENSHI.V.SURYA",
        location = "BROMAA Dojo, 2nd Floor Geetha Hospital, Gajuwaka",
        startTime = "06:30 AM",
        endTime = "08:00 AM",
        activeDays = "Mon,Tue,Wed,Thu,Fri,Sat",
        studentCount = 30
    )

    val batch4 = BatchEntity(
        batchId = "BROM-B4",
        batchName = "Batch 4",
        programName = "Kumite Sparring (Black Belts)",
        coachId = "COACH-01",
        coachName = "Shihan.BRUCELEE RAJ",
        location = "BROMAA Dojo, 2nd Floor Geetha Hospital, Gajuwaka",
        startTime = "05:00 PM",
        endTime = "08:00 PM",
        activeDays = "Mon,Tue,Wed,Thu,Fri,Sat",
        studentCount = 32
    )

    val batch5 = BatchEntity(
        batchId = "BROM-B5",
        batchName = "Batch 5",
        programName = "Kata Training (Colour Belts)",
        coachId = "COACH-05",
        coachName = "SENSEI.B.RAVINDHRA",
        location = "BROMAA Dojo, 2nd Floor Geetha Hospital, Gajuwaka",
        startTime = "06:00 PM",
        endTime = "07:00 PM",
        activeDays = "Mon,Tue,Wed,Thu,Fri,Sat",
        studentCount = 42
    )

    val batch6 = BatchEntity(
        batchId = "BROM-B6",
        batchName = "Batch 6",
        programName = "Kumite Sparring (Colour Belts)",
        coachId = "COACH-06",
        coachName = "SENSEI.A.SWATI",
        location = "BROMAA Dojo, 2nd Floor Geetha Hospital, Gajuwaka",
        startTime = "07:00 PM",
        endTime = "08:00 PM",
        activeDays = "Mon,Tue,Wed,Thu,Fri,Sat",
        studentCount = 40
    )

    dao.insertBatch(batch1)
    dao.insertBatch(batch2)
    dao.insertBatch(batch3)
    dao.insertBatch(batch4)
    dao.insertBatch(batch5)
    dao.insertBatch(batch6)

    // 3. Seed Fee Items (Updated based on flyer: Regular Training includes Self Defence, Fitness, Weapons & Karate Training - ₹2000/mo)
    val fees = listOf(
        FeeItemEntity("FEE-ADM", "Admission Fee", 2000.0, "One-Time", "Initial academy enrollment & registration"),
        FeeItemEntity("FEE-DRESS", "Basic Karate Dress (Gi)", 1500.0, "One-Time", "Official BROMAA Shito-Ryu Karate Gi uniform"),
        FeeItemEntity("FEE-REGULAR", "Regular Training (Self Defence, Fitness, Weapons & Karate Training)", 2000.0, "Monthly", "Complete regular martial arts curriculum comprising Karate, Self Defence, Physical Fitness & Conditioning, and Traditional Weapon Training (Nunchaku & Bo)"),
        FeeItemEntity("FEE-SPEC-1D", "Special Training - 1 Day", 5000.0, "Per Course", "6 hours intensive martial arts camp"),
        FeeItemEntity("FEE-SPEC-3D", "Special Training - 3 Days", 10000.0, "Per Course", "4 hours/day 3-day boot camp"),
        FeeItemEntity("FEE-SPEC-1W", "Special Training - 1 Week", 15000.0, "Per Course", "4 hours/day 7-day masterclass")
    )
    fees.forEach { dao.insertFeeItem(it) }

    // 4. Seed Payment Records
    val payments = listOf(
        PaymentRecordEntity("BROM-REC-2026-00124", "BROMA-0001", "Ch. Yogindra", "Regular Training (Self Defence, Fitness, Weapons & Karate Training)", "August 2026", 2000.0, "2026-08-05", "UPI", "UPI-88492019", "PAID"),
        PaymentRecordEntity("BROM-REC-2026-00089", "BROMA-0001", "Ch. Yogindra", "Regular Training (Self Defence, Fitness, Weapons & Karate Training)", "July 2026", 2000.0, "2026-07-08", "UPI", "UPI-77192301", "PAID"),
        PaymentRecordEntity("BROM-REC-2026-00140", "BROMA-0001", "Ch. Yogindra", "Regular Training (Self Defence, Fitness, Weapons & Karate Training)", "August 2026", 2000.0, "2026-08-01", "QR Code", "QR-990182", "DUE"),
        PaymentRecordEntity("BROM-REC-2026-00125", "BROMA-0002", "Ch. Rahul", "Regular Training (Self Defence, Fitness, Weapons & Karate Training)", "August 2026", 2000.0, "2026-08-06", "UPI", "UPI-88492020", "PAID")
    )
    payments.forEach { dao.insertPaymentRecord(it) }

    // 5. Seed Attendance
    val dates = listOf("2026-08-01", "2026-08-02", "2026-08-03", "2026-08-04", "2026-08-05", "2026-08-06", "2026-08-07", "2026-08-08", "2026-08-09", "2026-08-10")
    dates.forEachIndexed { index, dateStr ->
        val status = when (index) {
            2 -> "ABSENT"
            5 -> "LEAVE"
            8 -> "LATE"
            else -> "PRESENT"
        }
        dao.insertAttendance(
            AttendanceRecordEntity(
                studentId = "BROMA-0001",
                studentName = "Ch. Yogindra",
                batchId = "BROM-B2",
                date = dateStr,
                status = status,
                timeSlot = "6:00 PM – 8:00 PM"
            )
        )
    }

    // 6. Seed Certificates
    val certs = listOf(
        CertificateEntity(
            certId = "CERT-2026-G01",
            studentId = "BROMA-0001",
            studentName = "Ch. Yogindra",
            title = "Green Belt Certification",
            category = "Belt Certificates",
            issueDate = "2026-06-12",
            issuingOrg = "BROMA Academy",
            status = "VERIFIED",
            isOfficialBroma = true
        ),
        CertificateEntity(
            certId = "CERT-2026-T04",
            studentId = "BROMA-0001",
            studentName = "Ch. Yogindra",
            title = "State Karate Championship Gold Medalist",
            category = "Tournament Certificates",
            issueDate = "2026-04-18",
            issuingOrg = "State Martial Arts Federation",
            status = "VERIFIED",
            isOfficialBroma = false
        )
    )
    certs.forEach { dao.insertCertificate(it) }

    // 7. Seed Calendar Events
    val events = listOf(
        CalendarEventEntity(
            eventId = "EVT-2026-01",
            title = "State Karate Championship 2026",
            category = "Tournament",
            startDate = "2026-08-24",
            endDate = "2026-08-25",
            time = "09:00 AM",
            endTime = "06:00 PM",
            location = "Indira Gandhi Indoor Stadium, City",
            description = "Annual State Karate Championship featuring Kata and Kumite for all belt categories.",
            isRegistrationEnabled = true,
            registrationFee = 500.0,
            registrationDeadline = "2026-08-20",
            venue = "Indira Gandhi Indoor Stadium",
            city = "State Capital",
            categoriesList = "Sub-Junior, Junior, Senior (Kata & Kumite)"
        ),
        CalendarEventEntity(
            eventId = "EVT-2026-02",
            title = "Advanced Kobudo & Weapon Seminar",
            category = "Seminar",
            startDate = "2026-09-12",
            time = "10:00 AM",
            endTime = "04:00 PM",
            location = "BROMA Main Dojo, Hall A",
            description = "Special masterclass on Nunchaku and Bo staff defense techniques conducted by Grandmaster Sensei.",
            isRegistrationEnabled = true,
            registrationFee = 1200.0,
            registrationDeadline = "2026-09-08"
        ),
        CalendarEventEntity(
            eventId = "EVT-2026-03",
            title = "Quarterly Belt Examination",
            category = "Belt Examination",
            startDate = "2026-10-15",
            time = "08:00 AM",
            endTime = "02:00 PM",
            location = "BROMA Main Dojo",
            description = "Official grading examination for White, Yellow, Orange, Green, Blue, and Purple belt candidates.",
            isRegistrationEnabled = false
        )
    )
    events.forEach { dao.insertCalendarEvent(it) }

    // 8. Seed Announcements
    val announcements = listOf(
        AnnouncementEntity(
            announcementId = "ANN-2026-01",
            title = "Special Training Camp Registration",
            type = "Important",
            message = "Registration for the 1-Day, 3-Day, and 1-Week intensive martial arts boot camp is now open. Contact the admin or register via the My Class tab.",
            priority = "Urgent",
            audience = "All Students",
            publishDate = "2026-08-08",
            isPopupOnLaunch = true
        ),
        AnnouncementEntity(
            announcementId = "ANN-2026-02",
            title = "Independence Day Practice & Ceremony",
            type = "General",
            message = "Special flag hoisting and martial arts demonstration ceremony on 15th August at 7:30 AM at BROMA Main Dojo. All students in full uniform are requested to attend.",
            priority = "Important",
            audience = "All Students",
            publishDate = "2026-08-05"
        )
    )
    announcements.forEach { dao.insertAnnouncement(it) }

    // 9. Seed Admin Settings
    val adminSettings = AdminSettingsEntity(
        key = "MAIN",
        upiId = "bromaacademy@upi",
        qrCodeUri = null,
        logoUri = null,
        academyName = "BRUCELEE RAJ OLYMPIC MARTIALARTS ACADEMY (BROMAA)",
        academyShortName = "BROMAA",
        registrationNumber = "Regd.MP-23570",
        affiliation = "AFFILIATED TO MODERN SHITO-RYU KARATE DO ASSOCIATION (MSKA)",
        academyPhone = "8374632364",
        academyPhone2 = "6309735840",
        academyEmail = "info@bromaacademy.com",
        academyAddress = "2ND FLOOR, GEETHA HOSPITAL, NEETU ENUGU BOMMA, KAKATEEYA ITI JUNCTION, BC ROAD, GAJUWAKA",
        paymentInstructions = "Scan the QR code or send payment directly to UPI ID: bromaacademy@upi. Enter your transaction reference ID after payment for instant verification.",
        paymentPhone = "8374632364",
        paymentReceiverName = "BROMA Martial Arts",
        website = "www.bromaacademy.org",
        tagline1 = "SELF DEFENCE",
        tagline2 = "FITNESS",
        admissionsNote = "ADMISSIONS OPEN FOR BOYS & GIRLS (Age 3 Years & Above)",
        logoUpdatedTimestamp = "2026-08-14 10:30 AM",
        logoUpdatedBy = "Admin"
    )
    dao.insertOrUpdateAdminSettings(adminSettings)

    // 10. Seed Dojo Rules & Standards
    val defaultRules = listOf(
        DojoRuleEntity("RULE-01", 1, "Bow (Rei) upon entering and exiting the Dojo as a sign of respect."),
        DojoRuleEntity("RULE-02", 2, "Address all Black Belt instructors as Sensei and senior students as Sempei."),
        DojoRuleEntity("RULE-03", 3, "Keep Karate-gi clean, pressed, with proper official Academy patches."),
        DojoRuleEntity("RULE-04", 4, "No shoes, food, or gum allowed inside the Dojang tatami training area."),
        DojoRuleEntity("RULE-05", 5, "Maintain strict discipline, silence, and focused attention during training."),
        DojoRuleEntity("RULE-06", 6, "Never use martial arts skills outside the dojo except in genuine self-defense."),
        DojoRuleEntity("RULE-07", 7, "Finger and toe nails must be trimmed short for Kumite sparring safety.")
    )
    defaultRules.forEach { dao.insertDojoRule(it) }

    // 11. Seed Student Achievements
    val initialAchievements = listOf(
        AchievementEntity(
            achievementId = "ACH-2026-001",
            studentId = "BROMA-0001",
            studentName = "Ch. Yogindra",
            title = "1st Place Gold Medal - Kata",
            eventName = "State Martial Arts Championship 2026",
            category = "Tournament Medal",
            date = "2026-07-20",
            description = "Secured Gold in Boys Junior Kata division after 4 decisive match wins.",
            status = "APPROVED"
        ),
        AchievementEntity(
            achievementId = "ACH-2026-002",
            studentId = "BROMA-0001",
            studentName = "Ch. Yogindra",
            title = "Outstanding Discipline & Best Student Award",
            eventName = "BROMA Annual Dojo Excellence Awards",
            category = "Academy Award",
            date = "2026-06-15",
            description = "Awarded for exceptional training attendance, dojo respect, and peer leadership.",
            status = "APPROVED"
        )
    )
    initialAchievements.forEach { dao.insertAchievement(it) }

    // 12. Seed Academy Officials & Leadership Hierarchy (Director & Chairman, Founder & President, General Secretary, Treasurer)
    val initialLeadership = listOf(
        AcademyLeadershipEntity(
            leadershipId = "LEAD-01",
            fullName = "Ch. Sujatamutyalu",
            postTitle = "Director and Chairman",
            rankOrBelt = "Executive Patron & Director",
            contactPhone = "8374632364",
            contactEmail = "director@bromaacademy.com",
            displayOrder = 1,
            messageOrBio = "Director and Chairman guiding institutional leadership, state affiliations, and martial arts excellence at BROMA Academy.",
            isExecutiveBoard = true
        ),
        AcademyLeadershipEntity(
            leadershipId = "LEAD-02",
            fullName = "A. Tatarao",
            postTitle = "Founder and President",
            rankOrBelt = "Grandmaster & Founder",
            contactPhone = "9848012345",
            contactEmail = "president@bromaacademy.com",
            displayOrder = 2,
            messageOrBio = "Founder and President spearheading Olympic Karate-Do training, youth development, and academy vision.",
            isExecutiveBoard = true
        ),
        AcademyLeadershipEntity(
            leadershipId = "LEAD-03",
            fullName = "A. Sombabu",
            postTitle = "General Secretary",
            rankOrBelt = "Black Belt 6th Dan",
            contactPhone = "6309735840",
            contactEmail = "gensecretary@bromaacademy.com",
            displayOrder = 3,
            messageOrBio = "General Secretary managing syllabus enforcement, state tournament approvals, admissions, and coach operations.",
            isExecutiveBoard = true
        ),
        AcademyLeadershipEntity(
            leadershipId = "LEAD-04",
            fullName = "A. Sailjaraj",
            postTitle = "Treasurer",
            rankOrBelt = "Executive Treasurer & Head of Finance",
            contactPhone = "8374632364",
            contactEmail = "treasurer@bromaacademy.com",
            displayOrder = 4,
            messageOrBio = "Treasurer supervising financial transparency, fee administration, student welfare, and tournament funding.",
            isExecutiveBoard = true
        ),
        AcademyLeadershipEntity(
            leadershipId = "LEAD-05",
            fullName = "Shihan. BRUCELEE RAJ",
            postTitle = "Chief Technical Director & Master Sensei",
            rankOrBelt = "Black Belt 8th Dan",
            contactPhone = "8374632364",
            contactEmail = "bruceleeraj@bromaacademy.com",
            displayOrder = 5,
            messageOrBio = "Master Technical Director directing high-performance Kata, Kumite combat squads, and Olympic martial arts programs.",
            isExecutiveBoard = false
        ),
        AcademyLeadershipEntity(
            leadershipId = "LEAD-06",
            fullName = "Renshi. V. SURYA",
            postTitle = "Senior Instructor & Sparring Coach",
            rankOrBelt = "Black Belt 5th Dan",
            contactPhone = "8374632364",
            contactEmail = "surya@bromaacademy.com",
            displayOrder = 6,
            messageOrBio = "Technical director for national championship sparring squads, physical conditioning, and junior athletes.",
            isExecutiveBoard = false
        )
    )
    initialLeadership.forEach { dao.insertLeadership(it) }

    // 13. Seed Tournaments
    val initialTournaments = listOf(
        TournamentEntity(
            tournamentId = "TOURN-2026-001",
            title = "All India Inter-School Karate Championship 2026",
            startDate = "2026-08-24",
            endDate = "2026-08-25",
            startTime = "08:30 AM",
            endTime = "06:00 PM",
            venue = "Swarna Bharathi Indoor Stadium",
            city = "Visakhapatnam",
            organizer = "Modern Shito-Ryu Karate Do Association (MSKA)",
            categoryType = "National",
            description = "National open martial arts competition featuring Kata, Kumite, and Team Weapon divisions for sub-junior, junior, and senior categories.",
            eventsCategories = "Sub-Junior Kata, Cadet Kumite, Junior Kumite, Senior Black Belt Open",
            registrationFee = 850.0,
            registrationDeadline = "2026-08-20",
            eligibility = "Open to all colored and black belts with valid ID and medical fitness declaration.",
            importantInstructions = "Chest guard, shin instep protector, mouth guard, and approved red/blue gloves mandatory.",
            contactPerson = "Shihan Dai. A. Sombabu",
            contactPhone = "6309735840",
            status = "UPCOMING",
            isPublished = true
        ),
        TournamentEntity(
            tournamentId = "TOURN-2026-002",
            title = "Andhra Pradesh State Level Martial Arts Cup 2026",
            startDate = "2026-09-12",
            endDate = "2026-09-13",
            startTime = "09:00 AM",
            endTime = "05:30 PM",
            venue = "Rajiv Gandhi Sports Arena, Gajuwaka",
            city = "Visakhapatnam",
            organizer = "BROMA Academy & District Sports Authority",
            categoryType = "State",
            description = "Prestigious state selection championship for National Games representation.",
            eventsCategories = "Individual Kata, Weight Category Kumite (-50kg, -60kg, +70kg), Team Kata",
            registrationFee = 600.0,
            registrationDeadline = "2026-09-05",
            eligibility = "Yellow Belt and above.",
            importantInstructions = "Participants must submit dojo affiliation and coach consent letter.",
            contactPerson = "Grandmaster Shihan. Brucelee Raj",
            contactPhone = "8374632364",
            status = "UPCOMING",
            isPublished = true
        )
    )
    initialTournaments.forEach { dao.insertTournament(it) }

    // 14. Seed Training Programs
    val initialPrograms = listOf(
        TrainingProgramEntity(
            programId = "PROG-01",
            programTitle = "Regular Karate-Do & Martial Arts",
            feeAmount = 1000.0,
            feeFrequency = "Monthly",
            targetAudience = "Boys & Girls (Age 3 Years & Above)",
            durationText = "Ongoing (Year-round training)",
            syllabusOverview = "Foundational Stances (Dachi), Strikes (Tsuki), Kicks (Geri), Blocks (Uke), Heian Katas & Light Sparring.",
            scheduleSummary = "Batch 1: 5:00 PM – 6:00 PM | Batch 2: 6:00 PM – 8:00 PM (Mon–Sat)",
            isActive = true,
            displayOrder = 1
        ),
        TrainingProgramEntity(
            programId = "PROG-02",
            programTitle = "Black Belt Master Program & Dan Grading",
            feeAmount = 2500.0,
            feeFrequency = "Quarterly",
            targetAudience = "Advanced Belt Holders (Brown Belt 3rd Kyu & Above)",
            durationText = "12 - 24 Months Intensive",
            syllabusOverview = "Advanced Shito-Ryu Katas (Bassai Dai, Seienchin, Kanku Dai), Full Contact Kumite, Bunkai Application & Referee Training.",
            scheduleSummary = "Tue, Thu, Sat (6:00 PM – 8:30 PM) + Sunday Special Masterclass",
            isActive = true,
            displayOrder = 2
        ),
        TrainingProgramEntity(
            programId = "PROG-03",
            programTitle = "Self Defence & High Intensity Fitness Bootcamp",
            feeAmount = 1200.0,
            feeFrequency = "Monthly",
            targetAudience = "Teens, Women, Working Professionals",
            durationText = "Flexible 3 / 6 / 12 Months",
            syllabusOverview = "Practical Street Self-Defense, Anti-Grab Escapes, Joint Locks, Pressure Point Striking, Core Cardio & Flexibility.",
            scheduleSummary = "Morning Batch: 6:00 AM – 7:00 AM | Evening: 7:00 PM – 8:00 PM",
            isActive = true,
            displayOrder = 3
        ),
        TrainingProgramEntity(
            programId = "PROG-04",
            programTitle = "Weapon & Combat Mastery (Kobudo - Nunchaku / Bo / Sai)",
            feeAmount = 1500.0,
            feeFrequency = "Monthly",
            targetAudience = "Intermediate & Senior Karatekas",
            durationText = "6 Months Specialization",
            syllabusOverview = "Classical Okinawan Weapons: Dual Nunchaku Flow, Bo Staff Long Range Combat, Sai Daggers & Tonfa Blocks.",
            scheduleSummary = "Mon, Wed, Fri (6:00 PM – 7:30 PM)",
            isActive = true,
            displayOrder = 4
        )
    )
    initialPrograms.forEach { dao.insertTrainingProgram(it) }

    // 15. Seed Academy Standards
    val initialStandards = listOf(
        AcademyStandardEntity("STD-01", "Dojo Rei (Bowing Etiquette)", "DOJO_CODE", "Always bow 45 degrees upon stepping across the dojo threshold.", 1),
        AcademyStandardEntity("STD-02", "Gi Maintenance & Discipline", "DOJO_CODE", "Uniform must be pristine white with BROMA crest pinned on the left chest.", 2),
        AcademyStandardEntity("STD-03", "Grading Attendance Prerequisite", "BELT_REQUIREMENTS", "Minimum 80% recorded attendance required to qualify for quarterly belt promotion.", 3),
        AcademyStandardEntity("STD-04", "Safe Sparring Equipment", "EXAM_RULES", "Mandatory mouth guard and certified WKF/MSKA hand pads for all free sparring sessions.", 4)
    )
    initialStandards.forEach { dao.insertStandard(it) }

    // 16. Seed Sample Initial Messages
    val initialChat = listOf(
        ChatMessageEntity(
            messageId = "CHAT-001",
            senderId = "BROMA-0001",
            senderName = "Ch. Yogindra",
            senderRole = "STUDENT",
            recipientId = "ADMIN",
            recipientRole = "ADMIN",
            messageText = "Namaste Sensei! When will the new batch timings for tournament training begin?",
            timestamp = System.currentTimeMillis() - 3600000 * 5,
            isFromAdmin = false,
            isRead = true
        ),
        ChatMessageEntity(
            messageId = "CHAT-002",
            senderId = "ADMIN-01",
            senderName = "BROMA Admin",
            senderRole = "ADMIN",
            recipientId = "BROMA-0001",
            recipientRole = "STUDENT",
            messageText = "Oss Yogindra! Tournament training batch runs Mon-Fri from 6:00 PM to 8:00 PM. Keep your gear ready.",
            timestamp = System.currentTimeMillis() - 3600000 * 3,
            isFromAdmin = true,
            isRead = true
        )
    )
    initialChat.forEach { dao.insertChatMessage(it) }
}

