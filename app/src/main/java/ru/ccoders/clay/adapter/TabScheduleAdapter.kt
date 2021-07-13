import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.ccoders.clay.R
import ru.ccoders.clay.adapter.ScheduleCaustomAdapter
import ru.ccoders.clay.model.Schedule

class PagerAdapterSchedule(private val context: Context, private val schedules: MutableList<Schedule>) :
    RecyclerView.Adapter<PagerAdapterSchedule.PageHolder>() {
    class PageHolder(view: View) : RecyclerView.ViewHolder(view) {
        val scheduleLayout: RecyclerView = view.findViewById(R.id.SV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
        val holder = PageHolder(LayoutInflater.from(context).inflate(R.layout.recycle_fragment, parent, false))
        val scheduleLayout = holder.scheduleLayout
        if(scheduleLayout.layoutManager == null) {
            scheduleLayout.layoutManager = LinearLayoutManager(context)
        }
        val adapter = ScheduleCaustomAdapter(schedules,context)
        scheduleLayout.adapter  = adapter
        return holder
    }

    override fun onBindViewHolder(holder: PageHolder, position: Int) {


    }

    override fun getItemCount(): Int = 2
}

