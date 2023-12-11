package com.example.pi

//old!! don't use in project (usages to don't using namespace)
data class UserInformation (
    var id: Int,
    var isAnonymous: Boolean
) {
    constructor() : this(0, false)
}