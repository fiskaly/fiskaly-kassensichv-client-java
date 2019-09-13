import com.fiskaly.kassensichv.sma.GeneralSMA;

import java.io.IOException;


// javac -cp "sma-general-0.0.1-alpha.jar:sma-common-0.0.1-alpha.jar" GeneralSMATest.java

public class GeneralSMATest {

    public static GeneralSMA generalSMA = null;

    public static void main(String[] args) {

        try {
            generalSMA = new GeneralSMA();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }

        invokeResponseTest();
    }

    private static void invokeResponseTest() {

        String invokeResponse = generalSMA.invoke("{\"jsonrpc\":\"2.0\",\"method\":\"version\"}");

        if(invokeResponse != null && !invokeResponse.equals("")){
            System.out.println(invokeResponse);
        }
    }

}
