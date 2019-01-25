import java.io.Serializable;
import java.io.*;

public class TestMain {

    public static void main(String[] args) throws FileNotFoundException {


        FileOutputStream fil = new FileOutputStream(new File( "User"), true);

        try(ObjectOutputStream serial = new ObjectOutputStream(new FileOutputStream(new File( "User")))) {
            for (int i = 100; i < 102; i++) {
                serial.writeObject(new User("Login " + i, "Pass "+ i));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        try(ObjectInputStream serial = new ObjectInputStream(new FileInputStream(new File( "User")))) {
            for (int i = 0; i < 4; i++) {
                User u = (User) serial.readObject();
                System.out.println(i + ": pass = " + u.getPassword() + " log = " + u.getLogin());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}

class User implements Serializable {

    private String login = null;
    private String password = null;

    User(){}

    User(String login, String password){
        this.login = login;
        this.password = password;

    }



    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

}
