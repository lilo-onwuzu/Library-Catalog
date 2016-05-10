import java.util.List;
import org.sql2o.*;

public class Title {
  private String name;
  private int id;

  public Title(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public int getId() {
   return id;
  }

  public static List<Title> all(){
    String sql = "SELECT id, name FROM titles";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Title.class);
    }
  }

  @Override
  public boolean equals(Object otherTitle) {
    if (!(otherTitle instanceof Title)) {
      return false;
    } else {
      Title newTitle = (Title) otherTitle;
      return this.getName().equals(newTitle.getName()) &&
             this.getId() == newTitle.getId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO titles (name) VALUES (:name);";
      // collect the primary key assigned through the DB, type-cast it to become an integer object and then assign it to the title_id
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", this.name)
        .executeUpdate()
        .getKey();
    }
  }

  public static Title find(int id) {
    String sql = "SELECT * FROM titles WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      Title title = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Title.class);
      return title;
    }
  }

  public void update(String update) {
    String sql = "UPDATE titles SET name=:name WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("name", update)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public void delete() {
    String sql = "DELETE FROM titles WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }
}
