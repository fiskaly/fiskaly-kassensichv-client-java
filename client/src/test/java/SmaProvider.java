import java.io.IOException;

import com.fiskaly.kassensichv.sma.GeneralSma;

public class SmaProvider {
  private static GeneralSma sma;

  public static GeneralSma getSma() throws IOException {
    if(sma == null) {
      sma = new GeneralSma();
    }

    return sma;
  }
}
