package utils;

/**
 * Created by Luka on 24.2.2015.
 */
public class User {

    private int idUser;
    private String userName;

    public User(int idUser, String userName) {
        this.idUser = idUser;
        this.userName = userName;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
