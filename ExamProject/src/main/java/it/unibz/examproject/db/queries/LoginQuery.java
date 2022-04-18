package it.unibz.examproject.db.queries;

public class LoginQuery extends Query{

    private String email;
    private String password;

    public LoginQuery(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public String getQueryString() {
        return String.format("SELECT * FROM [user] WHERE email='%s' AND password='%s'", this.email, this.password);
    }
}
