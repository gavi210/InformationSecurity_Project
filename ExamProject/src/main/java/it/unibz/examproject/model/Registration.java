package it.unibz.examproject.model;

public class Registration {

    private final String name;
    private final String surname;
    private final String mail;
    private final String password;

    public Registration(String name, String surname, String mail, String password) {
        this.name = name;
        this.surname = surname;
        this.mail = mail;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }
}
