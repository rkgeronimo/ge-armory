package hr.gearmory.app.feature.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import hr.gearmory.app.remote.Reservation
import hr.gearmory.app.remote.ReservationClient
import hr.gearmory.app.remote.StaffSession
import hr.gearmory.app.remote.reservationNotice

internal class HomeViewModel : ViewModel() {
    var pending by mutableStateOf<List<Reservation>?>(null)
    var active by mutableStateOf<List<Reservation>?>(null)
    var notice by mutableStateOf<String?>(null)

    fun reload() {
        if (active == null) active = StaffSession.cachedActive
        if (pending == null) pending = StaffSession.cachedPending
        notice = null
        StaffSession.request(ReservationClient::listPending) { result ->
            result.fold(
                onSuccess = {
                    pending = it
                    StaffSession.rememberPending(it)
                },
                onFailure = { if (pending == null) notice = reservationNotice(it) },
            )
        }
        StaffSession.request(ReservationClient::listActive) { result ->
            result.fold(
                onSuccess = {
                    active = it
                    StaffSession.rememberActive(it)
                },
                onFailure = { if (active == null) notice = reservationNotice(it) },
            )
        }
    }
}

internal data class ExcursionGroup(
    val title: String,
    val rows: List<HomeRow>,
)

internal data class HomeRow(
    val name: String,
    val returning: Boolean,
)

internal fun excursionGroups(
    pending: List<Reservation>,
    active: List<Reservation>,
): List<ExcursionGroup> {
    val rows = pending.map { it to false } + active.map { it to true }
    return rows
        .groupBy { (reservation, _) ->
            reservation.excursion?.takeIf { it.isNotBlank() } ?: "Bez izleta"
        }
        .map { (title, items) ->
            ExcursionGroup(
                title = title,
                rows = items
                    .map { (reservation, returning) -> HomeRow(reservation.userName, returning) }
                    .sortedBy { it.name },
            )
        }
        .sortedBy { it.title }
}
