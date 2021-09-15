package ru.ccoders.clay.model

import ProfileModel

data class ScheduleAndProfile constructor(
    val scheduleModel: ScheduleModel,
    val profileModel: ProfileModel?
) {

}