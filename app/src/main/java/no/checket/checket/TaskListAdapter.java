package no.checket.checket;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {
    private List<Task> mTaskList;
    private LayoutInflater mInflater;
    private Context context;
    private int length;

    public TaskListAdapter (Context context, List<Task> taskList, int length) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mTaskList = taskList;
        this.length = length;
    }

    public TaskListAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.tasklist_item, parent, false);
        return new TaskViewHolder(mItemView, this);
    }

    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task mCurrent = mTaskList.get(position);
        holder.wordItemView.setText(mCurrent.getHeader());
        if (!mCurrent.getDetails().isEmpty()) {
            holder.detailItemView.setText(mCurrent.getDetails());
        } else {
            holder.detailItemView.setText(R.string.noDetailsText);
        }
        // The holder will not accept a Date object for its setText() method
        // Parse to String
        // Starting by specifying a date format
        DateFormat df = new SimpleDateFormat("dd-MM-yy HH:mm");
        // Parse
        String strDate = df.format(mCurrent.getDate());
        holder.dateItemView.setText(strDate);
        Resources res = context.getResources();
        String mDrawable = mCurrent.getIcon();
        int resID = res.getIdentifier(mDrawable , "drawable", context.getPackageName());
        holder.imageItemView.setImageResource(resID);
        holder.checkBox.setContentDescription(strDate);
        if (mCurrent.getCompleted()) {
            holder.checkBox.setVisibility(View.GONE);
        }
    }

    public int getItemCount() {
        int i = length;
        return i;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        public final TextView wordItemView;
        public final TextView detailItemView;
        public final TextView dateItemView;
        public final ImageView imageItemView;
        public final CheckBox checkBox;
        final TaskListAdapter mAdapter;

        public TaskViewHolder(View itemView, TaskListAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.header);
            detailItemView = itemView.findViewById(R.id.details);
            dateItemView = itemView.findViewById(R.id.date);
            imageItemView = itemView.findViewById(R.id.icon);
            checkBox = itemView.findViewById(R.id.checkBox);
            this.mAdapter = adapter;
        }
    }
}
