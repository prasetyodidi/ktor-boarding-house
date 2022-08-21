package tech.didiprasetyo.domain.service

import io.github.serpro69.kfaker.Faker
import io.ktor.server.auth.jwt.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.mindrot.jbcrypt.BCrypt
import org.mockito.Mockito
import tech.didiprasetyo.data.local.entity.SessionEntity
import tech.didiprasetyo.data.local.entity.UserEntity
import tech.didiprasetyo.domain.model.UserLogin
import tech.didiprasetyo.domain.repository.SessionRepository
import tech.didiprasetyo.domain.repository.UserRepository
import java.util.UUID

class AuthServiceTest {
    private lateinit var userData: UserEntity
    private lateinit var userId: UUID
    private val fake = Faker()
    private var now: Long = 0
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var fakeUserRepository: UserRepository

    @BeforeEach
    fun beforeEach() {
        userId = UUID.randomUUID()
        email = fake.internet.safeEmail()
        password = BCrypt.hashpw("password", BCrypt.gensalt())
        now = System.currentTimeMillis()/1000
        userData  = UserEntity(
            id = userId,
            name = fake.name.nameWithMiddle(),
            email = email,
            password = password,
            noTelp = fake.phoneNumber.phoneNumber(),
            avatarUrl = fake.random.randomString(),
            verifiedAt = now + 60000,
            createdAt = now,
            updatedAt = now
        )
        fakeUserRepository = Mockito.mock(UserRepository::class.java)
    }

    @Test
    fun login() = runBlocking {
        Mockito.`when`(fakeUserRepository.getByEmail(email)).thenReturn(userData)
        val userLogin = UserLogin(
            email,
            password
        )
        val user = fakeUserRepository.getByEmail(userLogin.email)

        assertNotNull(user)
        assertEquals(userData.password, user?.password)
    }

    @Test
    fun `login invalid password`() = runBlocking{
        Mockito.`when`(fakeUserRepository.getByEmail(email)).thenReturn(userData)
        val userLogin = UserLogin(
            email,
            "invalidPassword"
        )
        Mockito.`when`(fakeUserRepository.getByEmail(email)).thenReturn(userData)
        val user = fakeUserRepository.getByEmail(userLogin.email)

        assertNotNull(user)
        assertNotEquals(userLogin.password, user?.password)
    }

    @Test
    fun `login invalid email and password`() = runBlocking{
        Mockito.`when`(fakeUserRepository.getByEmail(email)).thenReturn(userData)
        val userLogin = UserLogin(
            email,
            "invalidPassword"
        )
        val user = fakeUserRepository.getByEmail(userLogin.email)

        assertNotNull(user)
    }

    @Test
    @Disabled("development")
    fun register() {

    }

    @Test
    @Disabled("development")
    fun logout(): Unit = runBlocking{

    }

    @Test
    @Disabled("development")
    fun resetPassword() {
    }

    @Test
    @Disabled("development")
    fun getSessionByUserId() {
    }

    @Test
    @Disabled("development")
    fun getSessionById() {
    }

    @Test
    @Disabled("development")
    fun verifyEmail() {
    }

    @Test
    @Disabled("development")
    fun deleteEmail() {
    }

}