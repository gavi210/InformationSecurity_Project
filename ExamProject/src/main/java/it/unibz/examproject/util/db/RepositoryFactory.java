package it.unibz.examproject.util.db;

public class RepositoryFactory {
    public Repository getPostgresRepository() {
        return new PostgresRepository();
    }

    public Repository getSQLServerRepository() {
        return new PostgresRepository();
    }
}


