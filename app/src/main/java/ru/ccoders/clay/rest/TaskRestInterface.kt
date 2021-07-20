package ru.ccoders.clay.rest

import ru.ccoders.clay.model.TaskModel

interface TaskRestInterface {
    fun uploadTask(taskModel: TaskModel):Int
}