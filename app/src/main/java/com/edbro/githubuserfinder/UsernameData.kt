package com.edbro.githubuserfinder

class UsernameData {
    var userImageUrl: String? = null
    var usernameText: String? = null

    constructor(userImageUrl: String?, usernameText: String?) {
        this.userImageUrl = userImageUrl
        this.usernameText = usernameText
    }

    constructor() {}
}