import java.util.List;
import org.sql2o.*;

public class Copy {
  private int copy;
  private int id;

  public Copy(int copy) {
    this.copy = copy;
  }

  public int getCopy() {
    return copy;
  }

  public int getId() {
   return id;
  }

  public static List<Copy> all(){
    String sql = "SELECT id, copy FROM copies";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Copy.class);
    }
  }

  @Override
  public boolean equals(Object otherCopy) {
    if (!(otherCopy instanceof Copy)) {
      return false;
    } else {
      Copy newCopy = (Copy) otherCopy;
      return this.getCopy() == newCopy.getCopy() &&
             this.getId() == newCopy.getId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO copies (copy) VALUES (:copy);";
      // collect the primary key assigned through the DB, type-cast it to become an integer object and then assign it to the copy_id
      this.id = (int) con.createQuery(sql, true)
        .addParameter("copy", this.copy)
        .executeUpdate()
        .getKey();
    }
  }

  public static Copy find(int id) {
    String sql = "SELECT * FROM copies WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      Copy copy = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Copy.class);
      return copy;
    }
  }

  public void update(int update) {
    String sql = "UPDATE copies SET copy=:copy WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("copy", update)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public void delete() {
    String sql = "DELETE FROM copies WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }
}
