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
/**
 * 1주차 과제: html 파일을 읽어와서 그 파일속 정보 크롤링해서 xml파일 생성
 * */
public class makeCollection {

    public File[] makeFileList(String path) { // 5개의 파일이 들어있는 경로를 입력을 하면 파일이 쭉 들어온다.
        File dir = new File(path);
        return dir.listFiles();
    }

    public void run(String path) throws ParserConfigurationException, IOException, TransformerException {
        // xml 파일을 만들기 위한 클래스
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.newDocument();

        // 5개의 파일을 읽어오고
        File[] files = makeFileList(path);

        // docs 태그 생성 -> root 태그
        Element docs = document.createElement("docs");
        document.appendChild(docs);

        for(int i = 0; i< files.length; i++) {
            System.out.println(i);
            org.jsoup.nodes.Document html = Jsoup.parse(files[i], "UTF-8");

            // 각 html의 title, body에 있는 값 받아오기
            String titleData = html.title();
            String bodyData = html.body().text();

            // doc 태그 생성 -> 각각 아이디는 0~4 (파일 이름은 5개라서)
            Element doc = document.createElement("doc"); // 각각 doc를 만든다.
            docs.appendChild(doc); // 그리고 이걸 루트에 추가한다.
            doc.setAttribute("id",Integer.toString(i)); // id로 추가

            Element title = document.createElement("title"); // title이라는 태그속에
            title.appendChild(document.createTextNode(titleData));
            doc.appendChild(title);

            Element body = document.createElement("body"); // body라는 태그 가져오기
            body.appendChild(document.createTextNode(bodyData));
            doc.appendChild(body);

        }

        // 위에서 얻은 정보들을 토대로 xml파일 생성하기

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");

        DOMSource source = new DOMSource(document);
        StreamResult result= new StreamResult(new FileOutputStream(new File("./collection.xml")));
        transformer.transform(source,result);

    }

}
