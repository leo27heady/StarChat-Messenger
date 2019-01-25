package muc;

import java.io.Serializable;

class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username = null;
    private String name = null;
    private String surname = null;
    private String password = null;
    private String status = null;
    private String image = null;
    private boolean remember = false;

    public User clone(){
        return new User(username, name, surname, null, status, image, remember);
    }


    public User() {}

    public User(String username, String name, String surname, String password, String status, String image, boolean remember) {
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.status = status;
        this.image = image;
        this.remember = remember;
    }


    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPassword() {
        return password;
    }

    public String getStatus() {
        return status;
    }

    public String getImage(){return image;}

    public boolean isRemember (){return remember;}


    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImage(String image){this.image = image;}

    public void setRemember(boolean remember){this.remember = remember;}

}
