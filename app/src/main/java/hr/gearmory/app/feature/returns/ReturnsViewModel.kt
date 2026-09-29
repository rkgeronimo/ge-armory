package hr.gearmory.app.feature.returns

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

internal data class HeldPiece(
    val id: String,
    val type: String,
    val code: String,
)

internal class MemberHoldings(
    val member: String,
    val pieces: SnapshotStateList<HeldPiece>,
)

internal class ReturnsViewModel : ViewModel() {
    val holdings = mutableStateListOf(
        MemberHoldings(
            member = "Ana Kovač",
            pieces = listOf(
                HeldPiece("ana-odijelo", "Odijelo", "0512"),
                HeldPiece("ana-maska", "Maska", "14"),
                HeldPiece("ana-regulator", "Regulator", "7"),
            ).toMutableStateList(),
        ),
        MemberHoldings(
            member = "Marko Marić",
            pieces = listOf(
                HeldPiece("marko-cizmice", "Čizmice", "B0811"),
                HeldPiece("marko-odijelo", "Odijelo", "0603"),
            ).toMutableStateList(),
        ),
        MemberHoldings(
            member = "Petra Babić",
            pieces = listOf(
                HeldPiece("petra-regulator", "Regulator", "3"),
                HeldPiece("petra-maska", "Maska", "22"),
            ).toMutableStateList(),
        ),
    )

    val checkedIds = mutableStateListOf<String>()

    fun toggle(id: String) {
        if (id in checkedIds) {
            checkedIds.remove(id)
        } else {
            checkedIds.add(id)
        }
    }

    fun clearChecks() {
        checkedIds.clear()
    }

    fun returnMarked(member: String): Boolean {
        val holding = holdings.firstOrNull { it.member == member } ?: return false
        if (holding.pieces.none { it.id in checkedIds }) return false
        holding.pieces.removeAll { it.id in checkedIds }
        checkedIds.clear()
        val emptied = holding.pieces.isEmpty()
        if (emptied) holdings.remove(holding)
        return emptied
    }
}
