package ru.ccoders.clay.rest

import ru.ccoders.clay.model.ScheduleModel
import ru.ccoders.clay.model.SearchModel

interface TaskRestInterface {
    fun uploadTask(scheduleModel: ScheduleModel):Int
    fun loadTask(profile:Int):SearchModel
    fun loadPage(next:String?): SearchModel
    fun taskDone(id:Int);
    fun taskReject(id:Int)
    fun taskDelete(id:Int)
}