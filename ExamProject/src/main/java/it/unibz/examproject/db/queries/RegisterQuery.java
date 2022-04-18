package it.unibz.examproject.db.queries;

public class RegisterQuery extends Query {
    private String name;
    private String surname;
    private String email;
    private String password;

    public RegisterQuery(String name, String surname, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
    }

    @Override
    public String getQueryString() {
        return String.format("INSERT INTO [user] ( name, surname, email, password ) "
                + "VALUES ( '%s', '%s', '%s', '%s' )", this.name, this.surname, this.email, this.password);
    }
}
