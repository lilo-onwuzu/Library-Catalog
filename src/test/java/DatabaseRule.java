import org.junit.rules.ExternalResource;
import org.sql2o.*;

public class DatabaseRule extends ExternalResource {

  @Override
  protected void before() {
    DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/library_test", null, null);
  }

  @Override
  protected void after() {
    try(Connection con = DB.sql2o.open()) {
      String deleteAuthorQuery = "DELETE FROM authors *;";
      String deleteCopyQuery = "DELETE FROM copies *;";
      String deleteTitleQuery = "DELETE FROM titles *;";
      String deletePatronQuery = "DELETE FROM patrons *;";
      con.createQuery(deleteAuthorQuery).executeUpdate();
      con.createQuery(deleteCopyQuery).executeUpdate();
      con.createQuery(deleteTitleQuery).executeUpdate();
      con.createQuery(deletePatronQuery).executeUpdate();
    }
  }
}
