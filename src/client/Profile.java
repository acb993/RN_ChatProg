package client;

public class Profile {

    private String firstName;
    private String lastName;
    private String eMail;

    public Profile(String firstName,String lastName, String eMail){
        this.firstName = firstName;
        this.lastName = lastName;
        this.eMail = eMail;
    }

    public String getFirstName() {
        return firstName;
    }

    public String geteMail() {
        return eMail;
    }

    public String getLastName() {
        return lastName;
    }
}
