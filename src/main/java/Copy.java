import java.util.List;
import java.util.ArrayList;
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

  public void addPatron(Patron myPatron) {
    String sql = "INSERT INTO copies_patrons (copy_id, patron_id) VALUES (:copy_id, :patron_id)";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("copy_id", this.getId())
        .addParameter("patron_id", myPatron.getId())
        .executeUpdate();
    }
  }

  public List<Patron> getPatrons() {
    String joinQuery = "SELECT patron_id FROM copies_patrons WHERE copy_id=:copy_id";
    try(Connection con = DB.sql2o.open()) {
      List<Integer> patronIds = con.createQuery(joinQuery)
        .addParameter("copy_id", this.getId())
        .executeAndFetch(Integer.class);

      List<Patron> patronList = new ArrayList<Patron>();

      for (Integer patronId : patronIds) {
        String taskQuery = "SELECT * FROM patrons WHERE id=:patron_id";
        Patron copy_patron = con.createQuery(taskQuery)
          .addParameter("patron_id", patronId)
          .executeAndFetchFirst(Patron.class);
        patronList.add(copy_patron);
      }
      return patronList;
    }
  }

  public void delete() {
    String deleteQuery = "DELETE FROM copies WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(deleteQuery)
        .addParameter("id", this.id)
        .executeUpdate();
    String joinDeleteQuery = "DELETE FROM copies_patrons WHERE copy_id=:copy_id";
      con.createQuery(joinDeleteQuery)
        .addParameter("copy_id", this.getId())
        .executeUpdate();
    }
  }

}
