package ru.ccoders.clay.searsh_fragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ru.ccoders.clay.rest.TaskRest

class SearchFragmentViewModel (application: Application) : AndroidViewModel(application){
    val rest = TaskRest()

    fun loadTask(){
        val scope = CoroutineScope(Dispatchers.IO)
        scope.async {
            rest.loadPage(null)
        }

    }

}