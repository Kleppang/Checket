package no.checket.checket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AchievementRecAdapter extends RecyclerView.Adapter<AchievementRecAdapter.AchievementViewHolder> {


    /***************** First member variables and the constructor *********/
    private List<Achievement> mAchievements;

    public AchievementRecAdapter(List<Achievement> mAchievements) {
        this.mAchievements = mAchievements;
    }

    /************************ Second the view holder ************************/
    class AchievementViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable for any view that will be set as you render a row
        TextView tvName;
        TextView tvDesc;

        public AchievementViewHolder(View itemView) {
            super(itemView);
            // Viewholder gets the handles for each view items in a row
            tvName = (TextView)itemView.findViewById(R.id.txtName);
            tvDesc = (TextView)itemView.findViewById(R.id.txtDesc);
        }
    }

    /***************** Third, the implementation methods *************************/

    @Override
    public AchievementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View row_view = inflater.inflate(R.layout.achievement_row_layout, parent, false);
        AchievementViewHolder thisViewHolder = new AchievementViewHolder(row_view);
        return thisViewHolder;
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the ViewHolder to
     * reflect the item at the given position.
     @param holder      The ViewHolder which should be updated to represent the contents
     of the item at the given position in the  data set.
     @param position    The position of the item within the adapter's data set.
     **/

    @Override
    public void onBindViewHolder(AchievementViewHolder holder, int position) {

        Achievement currentAchievement = mAchievements.get(position);
        //  show the data in the views
        holder.tvName.setText(currentAchievement.getName());
        holder.tvDesc.setText(currentAchievement.getDesc());
    }

    /**Returns the total number of items in the data set held by the adapter. */
    @Override
    public int getItemCount() {
        return mAchievements.size();
    }
}
