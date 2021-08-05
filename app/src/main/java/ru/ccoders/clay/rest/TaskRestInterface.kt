package ru.ccoders.clay.rest

import ru.ccoders.clay.model.ScheduleModel

interface TaskRestInterface {
    fun uploadTask(scheduleModel: ScheduleModel):Int
}