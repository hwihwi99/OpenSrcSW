import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class kuir {
    public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, SAXException, ClassNotFoundException {

        String command = args[0];
        String path = args[1];

        if(command.equals("-i")){
            makeCollection xml = new makeCollection();
            xml.run();
        } else if(command.equals("-m")) {
            makeKeyword morphemeAnalysis = new makeKeyword();
            morphemeAnalysis.morphemeAnalysis();
        }else if(command.equals("-d")) {
            indexPost indexPost = new indexPost();
            indexPost.makeIndexPost();
        }
    }
}
