package utils;

/**
 * Created by Luka on 24.2.2015.
 */
public class User {

    private String userName;
    private String idFacebook;

    public User(String userName, String idFacebook) {
        this.userName = userName;
        this.idFacebook = idFacebook;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdFacebook() {
        return idFacebook;
    }

    public void setIdFacebook(String idFacebook) {
        this.idFacebook = idFacebook;
    }
}
