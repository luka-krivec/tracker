package adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import asynctasks.GetLastPoint;
import gpslogger.CyclingRoute;
import si.krivec.tracker.LiveTrackerActivity;
import si.krivec.tracker.MapActivity;
import si.krivec.tracker.R;
import utils.DateUtilities;
import utils.TrackerPoint;
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
        holder.user = user;
    }

    @Override
    public int getItemCount() {
        return objects == null ? 0 : objects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView userName;
        public User user;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            userName = (TextView) v.findViewById(R.id.txtRowUsersUserName);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), user.getIdFacebook(), Toast.LENGTH_SHORT).show();
        }
    }

}


