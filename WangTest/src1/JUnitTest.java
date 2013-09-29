import java.io.IOException;

import org.jboss.byteman.contrib.bmunit.BMScript;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;



/**
 * 用byteman测试junit
 * @author wang
 *
 */
@RunWith(BMUnitRunner.class)
@BMScript(value="JUnitTest", dir="target/classes")
public class JUnitTest {
	
	@Test
	public void test1() throws IOException{
		
		System.out.println("ok");
	}

	
}
