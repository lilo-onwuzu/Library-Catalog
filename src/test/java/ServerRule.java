import org.junit.rules.ExternalResource;
import spark.Spark;

public class ServerRule extends ExternalResource {

  protected void before() {
    String[] args = {};
    // run the main method on the dictionary class with an emty array of arguments
    App.main(args);
  }

  protected void after() {
    // stop the spark from sending info to the server after this rule terminates
    Spark.stop();
  }
}
