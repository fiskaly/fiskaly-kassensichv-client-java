import com.fiskaly.kassensichv.sma.GeneralSMA;

public class GeneralSMATest {
    public static void main(String[] args) throws Exception {
        GeneralSMA sma = new GeneralSMA();
        String response = sma.invoke("{\"jsonrpc\":\"2.0\",\"method\":\"version\"}");

        if (response == null || response.isEmpty()) {
            System.exit(1);
        }

        System.exit(0);
    }
}
