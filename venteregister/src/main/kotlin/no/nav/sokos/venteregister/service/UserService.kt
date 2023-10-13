package no.nav.sokos.venteregister.service

import no.nav.sokos.venteregister.domain.User

class UserService {

    fun hello(): User {
        return User("Hello from Venteregister module!")
    }
}