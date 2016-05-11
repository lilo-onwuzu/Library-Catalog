import org.junit.*;
import static org.junit.Assert.*;
import org.sql2o.*;
import java.util.List;

public class CopyTest {

  @Rule
  public DatabaseRule database = new DatabaseRule();

  @Test
  public void Copy_InstantiatesWithString_true() {
    Copy testCopy = new Copy(2);
    assertEquals(2,testCopy.getCopy());
  }

  @Test
  public void all_EmptyAtFirst_true() {
    assertEquals(0, Copy.all().size());
  }

  @Test
  public void equals_returnsTrueIfDescriptionsAreTheSame_true() {
    Copy firstCopy = new Copy(2);
    Copy secondCopy = new Copy(2);
    assertTrue(firstCopy.equals(secondCopy));
  }

  @Test
  public void save_savesObjectIntoDatabase_true() {
    Copy testCopy = new Copy(2);
    testCopy.save();
    assertTrue(Copy.all().get(0).equals(testCopy));
  }

  @Test
  public void save_assignsIdToObject_int() {
    Copy testCopy = new Copy(2);
    testCopy.save();
    Copy savedCopy = Copy.all().get(0);
    assertEquals(testCopy.getId(), savedCopy.getId());
  }

  @Test
  public void find_findCopyInDatabase_true() {
    Copy testCopy = new Copy(2);
    testCopy.save();
    Copy savedCopy = Copy.all().get(0);
    assertEquals(savedCopy, Copy.find(testCopy.getId()));
  }

  @Test
  public void update_updateTaskDescriptionInDatabase_true() {
    Copy testCopy = new Copy(2);
    testCopy.save();
    testCopy.update(4);
    assertEquals(4, Copy.find(testCopy.getId()).getCopy());
  }

  @Test
  public void delete_deletesTaskInDatabase_true() {
    Copy testCopy = new Copy(2);
    testCopy.save();
    int id = testCopy.getId();
    testCopy.delete();
    assertEquals(Copy.all().size(), 0);
  }
}
