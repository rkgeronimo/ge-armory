package hr.gearmory.app.remote

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

internal object StaffSession {
    var credentials: StaffCredentials? = null
        private set

    private val io = Executors.newSingleThreadExecutor()
    private val main = Handler(Looper.getMainLooper())

    fun signIn(username: String, password: String, onResult: (String?) -> Unit) {
        val next = StaffCredentials(username.trim(), password.replace(" ", ""))
        io.execute {
            val error = try {
                ReservationClient.listActive(next)
                null
            } catch (thrown: Throwable) {
                reservationNotice(thrown)
            }
            main.post {
                if (error == null) credentials = next
                onResult(error)
            }
        }
    }

    fun signOut() {
        credentials = null
    }

    fun <T> request(block: (StaffCredentials) -> T, onResult: (Result<T>) -> Unit) {
        val current = credentials
        if (current == null) {
            onResult(Result.failure(ReservationException(401, "rkg_forbidden")))
            return
        }
        io.execute {
            val result = runCatching { block(current) }
            main.post { onResult(result) }
        }
    }
}
