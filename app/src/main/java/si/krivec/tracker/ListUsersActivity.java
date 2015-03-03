package si.krivec.tracker;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import adapters.UsersAdapter;
import asynctasks.GetAllUsers;
import utils.User;


public class ListUsersActivity extends ActionBarActivity {

    private RecyclerView recyclerViewListUsers;
    private RecyclerView.LayoutManager recyclerViewLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        recyclerViewLinearLayoutManager = new LinearLayoutManager(this);

        recyclerViewListUsers = (RecyclerView) findViewById(R.id.recyclerViewListUsers);
        recyclerViewListUsers.setHasFixedSize(true);
        recyclerViewListUsers.setLayoutManager(recyclerViewLinearLayoutManager);

        loadUsers();
    }

    private void loadUsers() {

        GetAllUsers getAllUsers = new GetAllUsers();

        try {
            ArrayList<User> usersList = getAllUsers.execute().get();
            UsersAdapter usersAdapter = new UsersAdapter(this, R.layout.row_user, usersList);
            recyclerViewListUsers.setAdapter(usersAdapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
