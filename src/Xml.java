import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class Xml{

    public File[] makeFileList(String path) { // 5개의 파일이 들어있는 경로를 입력을 하면 파일이 쭉 들어온다.
        File dir = new File(path);
        return dir.listFiles();
    }

    public void run() throws ParserConfigurationException, IOException, TransformerException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.newDocument();

        File[] files = makeFileList("C:\\Users\\chamg\\OneDrive\\바탕 화면\\4학년 1학기\\OpenSrc\\SimpleIR\\data");

        // docs 태그 생성 -> root 태그
        Element docs = document.createElement("docs");
        document.appendChild(docs);

        for(int i = 0; i< files.length; i++) {
            System.out.println(i);
            org.jsoup.nodes.Document html = Jsoup.parse(files[i], "UTF-8");
            String titleData = html.title();
            String bodyData = html.body().text();

            // doc 태그 생성 -> 각각 아이디는 0~4 (파일 이름은 5개라서)
            Element doc = document.createElement("doc");
            docs.appendChild(doc);
            doc.setAttribute("id",Integer.toString(i));

            Element title = document.createElement("title");
            title.appendChild(document.createTextNode(titleData));
            doc.appendChild(title);

            Element body = document.createElement("body");
            body.appendChild(document.createTextNode(bodyData));
            doc.appendChild(body);

        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");

        DOMSource source = new DOMSource(document);
        StreamResult result= new StreamResult(new FileOutputStream(new File("src/collection.xml")));
        transformer.transform(source,result);
    }

}
