package asynctasks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import utils.Constants;
import utils.User;
import utils.WebUtils;


public class GetAllUsers extends AsyncTask<Void, Integer, ArrayList<User>> {

    final String url = Constants.BACKEND_URL + "/users";
    final String paramsSelect = "getAllUsers=true";

    @Override
    protected ArrayList<User> doInBackground(Void... params) {
        String res = WebUtils.executePost(url, paramsSelect);

        try {
            JSONObject resJson = new JSONObject(res);
            ArrayList<User> users = addUsersToList(resJson);
            return  users;
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private ArrayList<User> addUsersToList(JSONObject users) {
        ArrayList<User> usersList = new ArrayList<>();
        JSONArray usersArray;

        try {
            usersArray = new JSONArray(users);
        } catch (JSONException e) {
            e.printStackTrace();
            return usersList;
        }

        HashMap<Integer, User> usersMap = new HashMap<Integer, User>();
        for(int i = 0; i < users.length(); i++){
            try {
                int idUser = usersArray.getJSONObject(i).getInt("idUser");
                String userName = usersArray.getJSONObject(i).getString("userName");
                User user = new User(idUser, userName);
                usersMap.put(idUser, user);
                usersList.add(user);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return usersList;
    }
}
