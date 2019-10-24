import java.io.IOException;

import com.fiskaly.kassensichv.sma.GeneralSMA;

public class SmaProvider {
  private static GeneralSMA sma;

  public static GeneralSMA getSma() throws IOException {
    if(sma == null) {
      sma = new GeneralSMA();
    }

    return sma;
  }
}
