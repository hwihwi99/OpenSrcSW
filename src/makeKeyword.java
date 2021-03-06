import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class makeKeyword {
    public void morphemeAnalysis(String path) throws ParserConfigurationException, IOException, SAXException, TransformerException {

        // xml 파일을 만들기 위한 클래스
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        File file = new File(path);

        // Collection.xml 파싱을 위한 곳
        // 일단 root 태그 가져오고 그 바로 아래에 있는 doc 태그를 읽어왔습니다.
        Document document = builder.parse(file);
        Element root = document.getDocumentElement();
        System.out.println(root.getNodeName());
        NodeList nodeLists = root.getElementsByTagName("doc");

        // xml 파일 형태
        Document rootTag = builder.newDocument();
        Element docs = rootTag.createElement("docs");
        rootTag.appendChild(docs);

        //  collection.xml을 토대로 총 내용을 읽어온다.
        for(int i = 0; i<nodeLists.getLength(); i++) {
            Element node = (Element) nodeLists.item(i);

            // doc 태그 생성 -> 각각 아이디는 0~4 (파일 이름은 5개라서)
            Element doc = rootTag.createElement("doc");
            docs.appendChild(doc);
            doc.setAttribute("id",Integer.toString(i));

            // title 태그 생성
            NodeList title = node.getElementsByTagName("title");
            String xmlTitle = title.item(0).getTextContent();

            Element titleTag = rootTag.createElement("title");
            titleTag.appendChild(rootTag.createTextNode(xmlTitle));
            doc.appendChild(titleTag);

            // body 태그 생성
            NodeList body = node.getElementsByTagName("body");
            String xmlBody = body.item(0).getTextContent();
            String dobyValue = kkmaString(xmlBody);

            Element bodyTag = rootTag.createElement("body");
            bodyTag.appendChild(rootTag.createTextNode(dobyValue));
            doc.appendChild(bodyTag);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");

        DOMSource source = new DOMSource(rootTag);
        StreamResult result= new StreamResult(new FileOutputStream(new File("./index.xml")));
        transformer.transform(source,result);
    }
    private String kkmaString(String inputBody) {
        //kkma를 통해서 스트링을 분석하고
        StringBuilder stringBuilder = new StringBuilder();
        KeywordExtractor keywordExtractor = new KeywordExtractor();
        KeywordList keywordList = keywordExtractor.extractKeyword(inputBody, true);

        // 그 스트링을 통해서 형태를 출력하고
        for(int i = 0; i<keywordList.size(); i++) {
            Keyword keyword = keywordList.get(i);
            stringBuilder.append(keyword.getString()).append(":").append(keyword.getCnt()).append("#");
        }
        // 반환한다.
        return stringBuilder.toString();
    }
}
