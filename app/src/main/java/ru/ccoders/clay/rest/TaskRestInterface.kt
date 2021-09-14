package ru.ccoders.clay.rest

import ru.ccoders.clay.model.ScheduleModel
import ru.ccoders.clay.model.SearchModel

interface TaskRestInterface {
    fun uploadTask(scheduleModel: ScheduleModel):Int
    fun loadTask(profile:Int):List<ScheduleModel>
    fun loadPage(next:String?): SearchModel
}