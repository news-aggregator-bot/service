package vlad110kg.news.aggregator.web.reader;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

//@Service("scannerReader")
public class ScannerWebPageReader implements WebPageReader {

    @Override
    public String read(String path) {
        try {
            URL url = new URL(path);
            Scanner sc = new Scanner(url.openStream());
            StringBuilder sb = new StringBuilder();
            while (sc.hasNext()) {
                sb.append(sc.next());
            }
            return sb.toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
