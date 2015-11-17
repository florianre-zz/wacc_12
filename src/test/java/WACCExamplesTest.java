import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Created by jonathanking on 17/11/2015.
 */
public class WACCExamplesTest {

    public static final String ERRORS_LOG_FILE_NAME = "errors.log";

    @Test
    public void allTestsPass() throws Exception {

        try {
            String line;
            Process p = Runtime.getRuntime().exec("./src/test/test");
            BufferedReader bri = new BufferedReader
                    (new InputStreamReader(p.getInputStream()));
            BufferedReader bre = new BufferedReader
                    (new InputStreamReader(p.getErrorStream()));
            while ((line = bri.readLine()) != null) {
                System.out.println(line);
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                System.out.println(line);
            }
            bre.close();
            p.waitFor();

            BufferedReader br = new BufferedReader(new FileReader(ERRORS_LOG_FILE_NAME));
            // The error log has no lines - ie no errors, which means all tests have passed.
            assertNull(br.readLine());
            assertNull(null);
        }
        catch (Exception err) {
            err.printStackTrace();
        }
        fail();
    }
}
