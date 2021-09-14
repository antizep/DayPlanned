package ru.ccoders.clay.model

import ProfileModel

data class SearchModel constructor(
    val next:String,
    val previous:String,
    val scheduleAndProfile: List<ScheduleAndProfile>
    ){
}