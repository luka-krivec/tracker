package utils;

/**
 * Created by Luka on 24.2.2015.
 */
public class User {

    private String userName;
    private String idFacebook;
    private boolean isOnline;

    public User(String userName, String idFacebook, boolean isOnline) {
        this.userName = userName;
        this.idFacebook = idFacebook;
        this.isOnline = isOnline;
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

    public boolean isOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }
}
