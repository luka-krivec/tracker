package si.krivec.tracker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.UsersAdapter;
import utils.User;


public class FriendsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewListUsers;
    private RecyclerView.LayoutManager recyclerViewLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        recyclerViewLinearLayoutManager = new LinearLayoutManager(this);

        recyclerViewListUsers = (RecyclerView) findViewById(R.id.recyclerViewListFriends);
        recyclerViewListUsers.setHasFixedSize(true);
        recyclerViewListUsers.setLayoutManager(recyclerViewLinearLayoutManager);

        getFriends();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends, menu);
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

    private void getFriends() {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            ArrayList<User> friendsList = new ArrayList<>();
                            JSONArray data = (JSONArray) response.getJSONObject().get("data");

                            for(int i = 0; i < data.length(); i++) {
                                JSONObject friend = data.getJSONObject(i);
                                friendsList.add(new User(friend.getString("name"), friend.getString("id")));
                            }

                            UsersAdapter usersAdapter = new UsersAdapter(FriendsActivity.this, R.layout.row_user, friendsList);
                            recyclerViewListUsers.setAdapter(usersAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();

    }

}
