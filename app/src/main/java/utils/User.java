package utils;

/**
 * Created by Luka on 24.2.2015.
 */
public class User {

    private int idUser;
    private String userName;
    private String idFacebook;

    public User(int idUser, String userName, String idFacebook) {
        this.idUser = idUser;
        this.userName = userName;
        this.idFacebook = idFacebook;
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

    public String getIdFacebook() {
        return idFacebook;
    }

    public void setIdFacebook(String idFacebook) {
        this.idFacebook = idFacebook;
    }
}
