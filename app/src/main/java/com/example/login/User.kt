package com.example.login

class User {
    var nombre: String? = null
    var apellidos: String? = null
    var edad: Int? = null
    var profesion: String? = null

    override fun toString(): String {
        return "name: $nombre, last name: $apellidos, age: $edad, profession: $profesion"
    }
}
