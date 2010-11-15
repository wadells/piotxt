package core;

//import static org.junit.Assert.*;
import org.junit.*;

public class TableHandlerTest extends MessageHandlerTest {

	@Before
	public void setUp() {
		handler = new TableHandler();
		keywords = handler.keywords;
	}

}
