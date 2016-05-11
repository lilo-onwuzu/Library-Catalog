import java.util.List;
import java.util.ArrayList;
import org.sql2o.*;

public class Patron {
  private String name;
  private int id;

  public Patron(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public int getId() {
   return id;
  }

  public static List<Patron> all(){
    String sql = "SELECT id, name FROM patrons";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Patron.class);
    }
  }

  @Override
  public boolean equals(Object otherPatron) {
    if (!(otherPatron instanceof Patron)) {
      return false;
    } else {
      Patron newPatron = (Patron) otherPatron;
      return this.getName().equals(newPatron.getName()) &&
             this.getId() == newPatron.getId();
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO patrons (name) VALUES (:name);";
      // collect the primary key assigned through the DB, type-cast it to become an integer object and then assign it to the patron_id
      this.id = (int) con.createQuery(sql, true)
        .addParameter("name", this.name)
        .executeUpdate()
        .getKey();
    }
  }

  public static Patron find(int id) {
    String sql = "SELECT * FROM patrons WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      Patron patron = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Patron.class);
      return patron;
    }
  }

  public void update(String update) {
    String sql = "UPDATE patrons SET name=:name WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("name", update)
        .addParameter("id", this.id)
        .executeUpdate();
    }
  }

  public void addCopy(Copy myCopy) {
    String sql = "INSERT INTO copies_patrons (copy_id, patron_id) VALUES (:copy_id, :patron_id)";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(sql)
        .addParameter("copy_id", myCopy.getId())
        .addParameter("patron_id", this.getId())
        .executeUpdate();
    }
  }

  public List<Copy> getCopies() {
    String joinQuery = "SELECT copy_id FROM copies_patrons WHERE patron_id=:patron_id";
    try (Connection con = DB.sql2o.open()) {
      List<Integer> copyIds = con.createQuery(joinQuery)
        .addParameter("patron_id", this.getId())
        .executeAndFetch(Integer.class);

      List<Copy> copyList = new ArrayList<Copy>();

      for (Integer copyId : copyIds) {
        String taskQuery = "SELECT * FROM copies WHERE id=:copy_id";
          Copy patron_copy = con.createQuery(taskQuery)
            .addParameter("copy_id", copyId)
            .executeAndFetchFirst(Copy.class);
            copyList.add(patron_copy);
      }
      return copyList;
    }
  }

  public void delete() {
    String deleteQuery = "DELETE FROM patrons WHERE id=:id";
    try(Connection con = DB.sql2o.open()) {
      con.createQuery(deleteQuery)
        .addParameter("id", this.id)
        .executeUpdate();
    String joinDeleteQuery = "DELETE FROM copies_patrons WHERE patron_id=:patron_id";
      con.createQuery(joinDeleteQuery)
        .addParameter("patron_id", this.getId())
        .executeUpdate();
    }
  }

}
