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

        HashMap<Integer, User> usersMap = new HashMap<>();

        try {
            JSONArray userArray = users.getJSONArray("users");

            for(int i = 0; i < users.length(); i++){
                try {
                    int idUser = userArray.getJSONObject(i).getInt("idUser");
                    String userName = userArray.getJSONObject(i).getString("userName");
                    String idFacebook = userArray.getJSONObject(i).getString("idFacebook");
                    User user = new User(idUser, userName, idFacebook);
                    usersMap.put(idUser, user);
                    usersList.add(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return usersList;
    }
}
