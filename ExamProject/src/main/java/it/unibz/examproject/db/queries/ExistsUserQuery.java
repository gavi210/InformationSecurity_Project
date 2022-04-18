package it.unibz.examproject.db.queries;

public class ExistsUserQuery extends Query {

    private String email;

    public ExistsUserQuery(String email) {
        this.email = email;
    }

    @Override
    public String getQueryString() {
        return String.format("SELECT * FROM [user] WHERE email='%s'", this.email);
    }
}
