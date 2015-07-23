package adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import si.krivec.tracker.LiveTrackerActivity;
import si.krivec.tracker.R;
import utils.User;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>  {

    private Context context;
    private int rowLayout;
    ArrayList<User> objects;

    public UsersAdapter(Context context, int rowLayout, ArrayList<User> objects) {
        this.context = context;
        this.rowLayout = rowLayout;
        this.objects = objects;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = objects.get(position);
        holder.userName.setText(user.getUserName());
        Drawable icStatus = user.isOnline() ? holder.view.getResources().getDrawable(R.drawable.ic_online) :
                holder.view.getResources().getDrawable(R.drawable.ic_offline);
        holder.imgStatus.setImageDrawable(icStatus);
        holder.user = user;
    }

    @Override
    public int getItemCount() {
        return objects == null ? 0 : objects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView userName;
        public ImageView imgStatus;
        public View view;
        public User user;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            userName = (TextView) v.findViewById(R.id.txtRowUsersUserName);
            imgStatus = (ImageView) v.findViewById(R.id.imgStatus);
            view = v;
        }

        @Override
        public void onClick(View v) {
            if(user.isOnline()) {
                Intent trackingActivity = new Intent(v.getContext(), LiveTrackerActivity.class);
                trackingActivity.putExtra("userFbId", user.getIdFacebook());
                v.getContext().startActivity(trackingActivity);
            } else {
                Toast.makeText(v.getContext(), v.getResources().getString(R.string.user_not_online), Toast.LENGTH_SHORT).show();
            }
        }
    }

}


