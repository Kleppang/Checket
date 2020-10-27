package no.checket.checket;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {
    private LinkedList<Task> mTaskList;
    private LayoutInflater mInflater;
    private Context context;

    public TaskListAdapter (Context context, LinkedList<Task> taskList) {
        mInflater = LayoutInflater.from(context);
        this.mTaskList = taskList;
        this.context = context;
    }

    public TaskListAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.tasklist_item, parent, false);
        return new TaskViewHolder(mItemView, this);
    }

    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task mCurrent = mTaskList.get(position);
        holder.wordItemView.setText(mCurrent.getHeader());
        holder.detailItemView.setText(mCurrent.getDetails());
        // The holder will not accept a Date object for its setText() method
        // Parse to String
        // Starting with specifying a date format
        DateFormat df = new SimpleDateFormat("dd-MM-yy HH:mm");
        // Parse
        String strDate = df.format(mCurrent.getDate());
        holder.dateItemView.setText(strDate);
        Resources res = context.getResources();
        String mDrawable = mCurrent.getIcon();
        int resID = res.getIdentifier(mDrawable , "drawable", context.getPackageName());
        holder.imageItemView.setImageResource(resID);
    }

    public int getItemCount() {
        final int i = 6;
        return i;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        public final TextView wordItemView;
        public final TextView detailItemView;
        public final TextView dateItemView;
        public final ImageView imageItemView;
        final TaskListAdapter mAdapter;

        public TaskViewHolder(View itemView, TaskListAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.header);
            detailItemView = itemView.findViewById(R.id.details);
            dateItemView = itemView.findViewById(R.id.date);
            imageItemView = itemView.findViewById(R.id.icon);
            this.mAdapter = adapter;
        }
    }
}
