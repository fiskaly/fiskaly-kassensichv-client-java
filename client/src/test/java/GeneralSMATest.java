import com.fiskaly.kassensichv.sma.GeneralSMA;

import java.io.IOException;

public class GeneralSMATest {

    public static GeneralSMA generalSMA = null;

    public static void main(String[] args) throws Exception{

        System.out.println("GeneralSMATest");

        generalSMA = new GeneralSMA();

        invokeResponseTest();
    }

    private static void invokeResponseTest() {

        String invokeResponse = generalSMA.invoke("{\"jsonrpc\":\"2.0\",\"method\":\"version\"}");

        if(invokeResponse == null || invokeResponse.equals("")){
            System.err.println("Error in Test \"invokeResponseTest\": \nGot Null or Empty return value.");
            System.exit(1);
        } else {
            System.out.println("Test \"invokeResponseTest\" completed successfully.");
        }
    }

}
