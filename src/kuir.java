import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class kuir {
    public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, SAXException, ClassNotFoundException {

        String command = args[0];
        String path = args[1];

        if(command.equals("-c")){
            makeCollection xml = new makeCollection();
            xml.run(path);
        } else if(command.equals("-k")) {
            makeKeyword morphemeAnalysis = new makeKeyword();
            morphemeAnalysis.morphemeAnalysis(path);
        }else if(command.equals("-i")) {
            indexPost indexPost = new indexPost();
            indexPost.makeIndexPost(path);
        } else if(command.equals("-s")) {
            searcher searcher = new searcher();
            String query = "";
            if(args[2].equals("-q")) {
                StringBuilder stringBuilder = new StringBuilder();
                for(int i = 3; i<args.length; i++) {
                    stringBuilder.append(args[i]).append(" ");
                }
                query = stringBuilder.toString();
            }
            searcher.Calsim(path,query);
        }
    }
}
