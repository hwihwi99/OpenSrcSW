import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class MidTerm {

    public void showSnippet(String path, String query) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        File file = new File(path);

        ArrayList<String> queryKeyword = kkmaQueryString(query);

        Document document = builder.parse(file);
        Element root = document.getDocumentElement();
        NodeList nodeLists = root.getElementsByTagName("doc");

        //  collection.xml을 토대로 총 내용을 읽어온다. doc 기준으로
        for(int i = 0; i<nodeLists.getLength(); i++) {

            Element node = (Element) nodeLists.item(i);

            // title 태그 내용 가져오기
            NodeList title = node.getElementsByTagName("title");
            String xmlTitle = title.item(0).getTextContent();

            // body 태그 내용 가져오기
            NodeList body = node.getElementsByTagName("body");
            String xmlBody = body.item(0).getTextContent();

            System.out.println(xmlBody);
            System.out.println(xmlTitle);
            String answer = "";
            int count = Integer.MIN_VALUE;
            // Body내용을 다 가지고 돌린다. 30단어씩 subString을 통해서, 그 후  실행한다.
            for(int j = 0; j<=xmlBody.length()-30; j++) {
                String subStr = xmlBody.substring(j,j+30);
                KeywordExtractor keywordExtractor = new KeywordExtractor();
                KeywordList keywordList = keywordExtractor.extractKeyword(subStr, true);
                int temp = 0;
                for(int k = 0; k<keywordList.size(); k++) {
                    Keyword keyword = keywordList.get(k);
                    if(queryKeyword.contains(keyword.getString())){
                        temp += keyword.getCnt();
                    }
                }
                int deforeCount = count;
                count = Math.max(count, temp);
                if(count == temp && deforeCount != count) { // 같아졌다면
                    answer = subStr;
                }
            }
            if(count != 0) {
                System.out.println(xmlTitle+", "+answer+", "+count);
            }
        }

    }

    // 쿼리문에서 키워드 뽑아오기
    private ArrayList<String> kkmaQueryString(String query) {

        ArrayList<String> result = new ArrayList<>();

        //kkma를 통해서 스트링을 분석하고
        KeywordExtractor keywordExtractor = new KeywordExtractor();
        KeywordList keywordList = keywordExtractor.extractKeyword(query, true);

        // 그 스트링을 통해서 형태를 출력하고
        for(int i = 0; i<keywordList.size(); i++) {
            Keyword keyword = keywordList.get(i);
            result.add(keyword.getString());
        }
        // 반환한다.
        return result;
    }
}
