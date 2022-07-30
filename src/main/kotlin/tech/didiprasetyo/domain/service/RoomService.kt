package tech.didiprasetyo.domain.service

import tech.didiprasetyo.domain.model.DateRange
import tech.didiprasetyo.domain.model.Reminder
import tech.didiprasetyo.domain.model.Room
import tech.didiprasetyo.domain.model.RoomInfo
import tech.didiprasetyo.domain.repository.ReminderRepository
import tech.didiprasetyo.domain.repository.RoomRepository
import tech.didiprasetyo.domain.repository.RuleRepository
import java.util.UUID

class RoomService(
    private val roomRepository: RoomRepository,
    private val ruleRepository: RuleRepository,
    private val reminderRepository: ReminderRepository
) {

    suspend fun getRoomInfo(roomId: UUID): RoomInfo{
        val rules = getRulesOfRoom(roomId)
        val reminders = getRemindersOfRoom(roomId)
        val reminderDate = getReminderDate(roomId)
        return RoomInfo(
            rules = rules,
            reminders = reminders,
            reminderDate = reminderDate
        )
    }

    suspend fun getRoomByBoardingHouseOwner(ownerId: UUID) : List<Room> {
        TODO("Not implemented")
    }

    suspend fun getRoomsByTenant(tenantId: UUID): List<Room> {
        return roomRepository
            .getByTenantId(tenantId)
            .map { it.intoRoom() }
    }

    suspend fun getRulesOfRoom(roomId: UUID): List<String> {
        val room = roomRepository.getById(roomId) ?: return emptyList()
        val boardingHouseId = room.idBoardingHouse
        val rules = ruleRepository.getByBoardingHouseId(boardingHouseId)
        return rules.map { it.value }
    }


    suspend fun getRemindersOfRoom(roomId: UUID): List<Reminder> {
        val room = roomRepository.getById(roomId) ?: return emptyList()
        val boardingHouseId = room.idBoardingHouse
        val reminders = reminderRepository.getByBoardingHouseId(boardingHouseId)
        return reminders.map { it.intoReminder() }
    }

    suspend fun getReminderDate(roomId: UUID): Long {
        val room = roomRepository.getById(roomId)
        if (room != null){
            if (room.dateEntry != null && room.dateOut != null) {
                return room.dateOut - (System.currentTimeMillis() / 1000)
            }
        }
        return 0
    }

    suspend fun getReminderDateRange(roomId: UUID): DateRange {
        val room = roomRepository.getById(roomId) ?: throw java.lang.IllegalArgumentException("Tenant not found")
        if (room.dateOut != null && room.dateEntry != null) {
            return DateRange(
                entry = room.dateEntry,
                out = room.dateOut
            )
        }
        throw java.lang.IllegalArgumentException("Tenant not found")
    }

}