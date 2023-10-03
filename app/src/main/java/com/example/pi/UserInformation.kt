package com.example.pi

data class UserInformation (
    var id: Int,
    var isAnonymous: Boolean
) {
    constructor() : this(0, false)
}