import android.content.Context
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.ccoders.clay.R
import ru.ccoders.clay.adapter.ScheduleCastomAdapter
import ru.ccoders.clay.model.ScheduleAndProfile
import ru.ccoders.clay.model.ScheduleModel

class PagerAdapterSchedule(
    private val context: Context,
    private val scheduleModels: List<ScheduleAndProfile>,
    private val day: Calendar,
    private val isPublic:Boolean
) :
    RecyclerView.Adapter<PagerAdapterSchedule.PageHolder>() {
    class PageHolder(view: View) : RecyclerView.ViewHolder(view) {
        val scheduleLayout: RecyclerView = view.findViewById(R.id.SV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
        val holder = PageHolder(
            LayoutInflater.from(context).inflate(R.layout.recycle_fragment, parent, false)
        )
        val scheduleLayout = holder.scheduleLayout
        if (scheduleLayout.layoutManager == null) {
            scheduleLayout.layoutManager = LinearLayoutManager(context)
        }

        return holder
    }

    override fun onBindViewHolder(holder: PageHolder, position: Int) {


        val adapter = ScheduleCastomAdapter(scheduleModels, context,day,position==1)
        holder.scheduleLayout.adapter = adapter

    }

    override fun getItemCount(): Int = 2
}

