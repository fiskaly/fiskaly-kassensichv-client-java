import com.fiskaly.kassensichv.sma.GeneralSMA;

import java.io.IOException;

public class GeneralSMATest {

    public static GeneralSMA generalSMA = null;

    public static void main(String[] args) {

        System.out.println("GeneralSMATest");

        try {
            generalSMA = new GeneralSMA();
        } catch (IOException e){
            System.out.println("Error in Test \"invokeResponseTest\":");
            System.out.println(e.getMessage());
        }

        invokeResponseTest();
    }

    private static void invokeResponseTest() {

        String invokeResponse = generalSMA.invoke("{\"jsonrpc\":\"2.0\",\"method\":\"version\"}");

        if(invokeResponse == null || invokeResponse.equals("")){
            System.out.println("Error in Test \"invokeResponseTest\": \nGot Null or Empty return value.");
        } else {
            System.out.println("Test \"invokeResponseTest\" completed successfully.");
        }
    }

}
