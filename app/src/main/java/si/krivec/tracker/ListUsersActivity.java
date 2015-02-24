package si.krivec.tracker;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import adapters.RoutesAdapter;
import adapters.UsersAdapter;
import dialogs.ImportGpxDialogFragment;
import gpslogger.CyclingRoute;
import gpslogger.GpxWriter;
import utils.DateUtilities;
import utils.FileComparator;
import utils.FileUtilities;
import utils.GpxParser;
import utils.User;
import utils.Utils;


public class ListUsersActivity extends ActionBarActivity {

    private RecyclerView recyclerViewListUsers;
    private RecyclerView.LayoutManager recyclerViewLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        recyclerViewLinearLayoutManager = new LinearLayoutManager(this);

        recyclerViewListUsers = (RecyclerView) findViewById(R.id.recyclerViewListRoutes);
        recyclerViewListUsers.setHasFixedSize(true);
        recyclerViewListUsers.setLayoutManager(recyclerViewLinearLayoutManager);

        loadUsers();
    }

    private void loadUsers() {
        // TODO: Load users from database
        User[] user = new User[5];
        UsersAdapter usersAdapter = new UsersAdapter(this, R.layout.row_user, user);
        recyclerViewListUsers.setAdapter(usersAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_users, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
