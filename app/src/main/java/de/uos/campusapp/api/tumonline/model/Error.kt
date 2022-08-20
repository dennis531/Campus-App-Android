package de.uos.campusapp.api.tumonline.model

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import de.uos.campusapp.api.general.exception.*

@Xml(name = "error")
data class Error(@PropertyElement var message: String = "") {

    val exception: ApiException
        get() = errorMessageToException
                .filter { message.contains(it.first) }
                .map { it.second }
                .firstOrNull() ?: UnknownErrorException()

    companion object {

        private val errorMessageToException = listOf(
                Pair("Keine Rechte für Funktion", ForbiddenException()),
                Pair("Token ist ungültig!", UnauthorizedException()),
                Pair("ungültiges Benutzertoken", UnauthorizedException()),
                Pair("Token ist nicht bestätigt!", UnauthorizedException()),
        )
    }
}
