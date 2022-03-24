import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class kuir {
    public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, SAXException {
        makeCollection xml = new makeCollection();
        xml.run();
        makeKeyword morphemeAnalysis = new makeKeyword();
        morphemeAnalysis.morphemeAnalysis();
    }
}
