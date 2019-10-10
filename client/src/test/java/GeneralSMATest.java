import com.fiskaly.kassensichv.sma.GeneralSMAImplementation;

public class GeneralSMATest {
    public static void main(String[] args) throws Exception {
        GeneralSMAImplementation sma = new GeneralSMAImplementation();
        String response = sma.invoke("{\"jsonrpc\":\"2.0\",\"method\":\"version\"}");

        if (response == null || response.isEmpty()) {
            System.exit(1);
        }

        System.exit(0);
    }
}
