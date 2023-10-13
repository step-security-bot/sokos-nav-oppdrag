package no.nav.sokos.oppdragsinfo.service

import no.nav.sokos.oppdragsinfo.domain.User

class UserService {

    fun hello(): User {
        return User("Hello from Oppdragsinfo module!")
    }
}