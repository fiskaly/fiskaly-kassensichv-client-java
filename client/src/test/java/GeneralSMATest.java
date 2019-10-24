import com.fiskaly.kassensichv.sma.GeneralSma;

public class GeneralSMATest {
    public static void main(String[] args) throws Exception {
        GeneralSma sma = new GeneralSma();
        String response = sma.invoke("{\"jsonrpc\":\"2.0\",\"method\":\"version\"}");

        if (response == null || response.isEmpty()) {
            System.exit(1);
        }

        System.exit(0);
    }
}
