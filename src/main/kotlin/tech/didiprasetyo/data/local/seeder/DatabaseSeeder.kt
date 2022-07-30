package tech.didiprasetyo.data.local.seeder

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import tech.didiprasetyo.data.local.dao.*
import tech.didiprasetyo.data.local.entity.*
import java.util.*
import kotlin.random.Random

class DatabaseSeeder {
    private val fake: Faker = Faker()
    private lateinit var listUserId: List<UUID>
    private lateinit var listBoardingHouseId: List<UUID>

    init {
        val amountUser = (1..10).toList()
        listUserId = amountUser.map { UUID.randomUUID() }
        listBoardingHouseId = amountUser.map { UUID.randomUUID() }
    }

    fun appDataFactory() {
        CoroutineScope(Dispatchers.IO).launch {
            transaction {
                listUserId.forEach { userId ->
                    userSeeder(userId)
                }
                listBoardingHouseId.forEach { id ->
                    val randomId = listUserId[Random.nextInt(0, 9)]
                    boardingHouseSeeder(id, randomId)
                }
                listBoardingHouseId.forEach { id ->
                    ruleSeeder(id, 11)
                    reminderSeeder(id, 7)
                    (0..2).forEach {
                        val randomId = listUserId[Random.nextInt(0, 9)]
                        roomSeeder(id, randomId)
                    }
                }

                for (i in 0..10){
                    val now = System.currentTimeMillis()/1000
                    val userEntity = UserEntity(
                        id = UUID.randomUUID(),
                        name = fake.name.nameWithMiddle(),
                        email = fake.internet.safeEmail(),
                        password = BCrypt.hashpw("password", BCrypt.gensalt()),
                        noTelp = fake.phoneNumber.phoneNumber(),
                        avatarUrl = fake.random.randomString(),
                        verifiedAt = now + 60000,
                        createdAt = now,
                        updatedAt = now
                    )

                    userFactory(userEntity)
//                    boardingHouseSeeder(userEntity.id, 10)
                }
            }
        }
    }

    fun userSeeder(userId: UUID){
        val now: Long = System.currentTimeMillis()/1000
        val userEntity = UserEntity(
            id = userId,
            name = fake.name.nameWithMiddle(),
            email = fake.internet.safeEmail(),
            password = BCrypt.hashpw("password", BCrypt.gensalt()),
            noTelp = fake.phoneNumber.phoneNumber(),
            avatarUrl = fake.random.randomString(),
            verifiedAt = now + 60000,
            createdAt = now,
            updatedAt = now
        )
        userFactory(userEntity)
    }

    fun boardingHouseSeeder(boardingHouseId: UUID, ownerId: UUID){
        val now = System.currentTimeMillis()/1000
        val boardingHouseData = BoardingHouseEntity(
            id = boardingHouseId,
            idOwner = ownerId,
            name = fake.computer.windows(),
            address = fake.address.fullAddress(),
            mapUrl = fake.address.community(),
            imageUrl = fake.company.bs(),
            createdAt = now,
            updatedAt = now
        )
        boardingHouseFactory(boardingHouseData)
    }

    fun roomSeeder(boardingHouseId: UUID, idTenant: UUID){
        val now = System.currentTimeMillis()/1000
        val roomData = RoomEntity(
            id = UUID.randomUUID(),
            idBoardingHouse = boardingHouseId,
            idTenant = idTenant,
            name = fake.animal.name(),
            imageUrl = fake.random.randomString(60),
            dateEntry = now + 2629743,
            dateOut = now + 2629743 * 2,
            createdAt = now,
            updatedAt = now
        )
        roomfactory(roomData)
    }

    fun reminderSeeder(boardingHouseId: UUID, amount: Int){
        for (i in 0..amount){
            val now = System.currentTimeMillis()/1000
            val reminderData = ReminderEntity(
                id = UUID.randomUUID(),
                boardingHouseId = boardingHouseId,
                value = fake.lorem.words(),
                imageUrl = fake.lorem.punctuation(),
                createdAt = now,
                updatedAt = now
            )
            reminderFactory(reminderData)
        }
    }

    fun ruleSeeder(boardingHouseId: UUID, amount: Int){
        for (i in 0..amount){
            val now = System.currentTimeMillis()/1000
            val ruleData = RuleEntity(
                id = UUID.randomUUID(),
                boardingHouseId = boardingHouseId,
                value = fake.lorem.words(),
                createdAt = now,
                updatedAt = now
            )
            ruleFactory(ruleData)
        }
    }

    fun userFactory(user: UserEntity) {
        Users.insert {
            it[id] = user.id
            it[name] = user.name
            it[email] = user.email
//            it[password] = BCrypt.hashpw("password", BCrypt.gensalt())
            it[password] = user.password
            it[verifiedAt] = user.verifiedAt
            it[createdAt] = user.createdAt
            it[updatedAt] = user.updatedAt
        }
    }

    fun boardingHouseFactory(boardingHouse: BoardingHouseEntity) {
        BoardingHouses.insert {
            it[id] = boardingHouse.id
            it[idOwner] = boardingHouse.idOwner
            it[name] = boardingHouse.name
            it[address] = boardingHouse.address
            it[mapUrl] = boardingHouse.mapUrl
            it[imageUrl] = boardingHouse.imageUrl
            it[createdAt] = boardingHouse.createdAt
            it[updatedAt] = boardingHouse.updatedAt
        }
    }

    fun roomfactory(room: RoomEntity){
        Rooms.insert {
            it[id] = room.id
            it[idBoardingHouse] = room.idBoardingHouse
            it[idTenant] = room.idTenant
            it[name] = room.name
            it[imageUrl] = room.imageUrl
            it[dateEntry] = room.dateEntry
            it[dateOut] = room.dateOut
            it[createdAt] = room.createdAt
            it[updatedAt] = room.updatedAt
        }
    }

    fun ruleFactory(ruledData: RuleEntity) {
        Rules.insert {
            it[id] = ruledData.id
            it[idBoardingHouse] = ruledData.boardingHouseId
            it[rule] = ruledData.value
            it[createdAt] = ruledData.createdAt
            it[updatedAt] = ruledData.updatedAt
        }
    }

    fun reminderFactory(reminderData: ReminderEntity){
        Reminders.insert {
            it[id] = reminderData.id
            it[idBoardingHouse] = reminderData.boardingHouseId
            it[reminder] = reminderData.value
            it[imageUrl] = reminderData.imageUrl
            it[createdAt] = reminderData.createdAt
            it[updatedAt] = reminderData.updatedAt
        }
    }
}



