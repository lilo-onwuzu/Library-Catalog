import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;
import java.util.List;

public class PatronTest {

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void Patron_InstantiatesWithString_true() {
    Patron testPatron = new Patron("patron");
    assertEquals("patron",testPatron.getName());
  }

  @Test
  public void all_EmptyAtFirst_true() {
    assertEquals(0, Patron.all().size());
  }

  @Test
  public void equals_returnsTrueIfDescriptionsAreTheSame_true() {
    Patron firstPatron = new Patron("patron1");
    Patron secondPatron = new Patron("patron1");
    assertTrue(firstPatron.equals(secondPatron));
  }

  @Test
  public void save_savesObjectIntoDatabase_true() {
    Patron testPatron = new Patron("patron");
    testPatron.save();
    assertTrue(Patron.all().get(0).equals(testPatron));
  }

  @Test
  public void save_assignsIdToObject_int() {
    Patron testPatron = new Patron("patron");
    testPatron.save();
    Patron savedPatron = Patron.all().get(0);
    assertEquals(testPatron.getId(), savedPatron.getId());
  }

  @Test
  public void find_findPatronInDatabase_true() {
    Patron testPatron = new Patron("patron");
    testPatron.save();
    Patron savedPatron = Patron.all().get(0);
    assertEquals(savedPatron, Patron.find(testPatron.getId()));
  }

  @Test
  public void update_updateTaskDescriptionInDatabase_true() {
    Patron testPatron = new Patron("patron");
    testPatron.save();
    testPatron.update("other patron");
    assertEquals("other patron", Patron.find(testPatron.getId()).getName());
  }

  @Test
  public void addPatron_addsPatronToCopy_true() {
    Copy myCopy = new Copy(2);
    myCopy.save();
    Patron myPatron = new Patron("James Blake");
    myPatron.save();
    myCopy.addPatron(myPatron);
    Patron savedPatron = myCopy.getPatrons().get(0);
    assertTrue(myPatron.equals(savedPatron));
  }

  @Test
  public void getPatron_getsPatronForACopy_true() {
    Copy myBook = new Copy(2);
    myBook.save();
    Patron myPatron = new Patron("James Blake");
    myPatron.save();
    myBook.addPatron(myPatron);
    List savedPatrons = myBook.getPatrons();
    assertEquals(1, savedPatrons.size());
  }

  @Test
  public void delete_deletesAllCopiesAndPatronsAssociations() {
    Patron myPatron = new Patron("JK Rowling");
    myPatron.save();
    Copy myCopy = new Copy(2);
    myCopy.save();
    myCopy.addPatron(myPatron);
    myCopy.delete();
    assertEquals(0, myPatron.getCopies().size());
  }
}
