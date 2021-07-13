package ru.ccoders.clay.rest

import ProfileModel

interface ProfileRestInterface {
    fun loadProfile(id:Int): ProfileModel?
}