package ru.ccoders.clay.searsh_fragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import ru.ccoders.clay.model.SearchModel
import ru.ccoders.clay.rest.TaskRest

class SearchFragmentViewModel (application: Application) : AndroidViewModel(application){
    val rest = TaskRest()
    val profileLiveData = MutableLiveData<SearchModel>()
    fun loadTask(){
        val scope = CoroutineScope(Dispatchers.IO)
        scope.async {
            val  searchRest = rest.loadPage(null)
            profileLiveData.postValue(searchRest)
            profileLiveData.value= searchRest
        }

    }

}