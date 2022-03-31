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
import java.io.*;
import java.util.*;

public class searcher {

    // 입력받은 새로운 문자열에 대해서 kkma 분석하기
    public HashMap<String,Integer> kkmaQuery(String str) {
        KeywordExtractor keywordExtractor = new KeywordExtractor();
        KeywordList keywordList = keywordExtractor.extractKeyword(str,true);
        HashMap<String, Integer> resultString = new HashMap<>();

        for(int i = 0; i<keywordList.size(); i++) {
            Keyword keyword = keywordList.get(i);
            resultString.put(keyword.getString(),keyword.getCnt());
        }

        return resultString;
    }

    public void Calsim(String path, String query) throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        File file = new File("./collection.xml");

        Document document = builder.parse(file);
        Element root = document.getDocumentElement();
        System.out.println(root.getNodeName());
        NodeList nodeLists = root.getElementsByTagName("doc");

        HashMap<Integer, String> titleList = new HashMap<>();

        for(int i = 0; i<nodeLists.getLength(); i++) {
            Element node = (Element) nodeLists.item(i);
            NodeList title = node.getElementsByTagName("title");
            String xmlTitle = title.item(0).getTextContent();
            titleList.put(i,xmlTitle);
        }


        FileInputStream fileInputStream = new FileInputStream(path);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        Object object = objectInputStream.readObject();
        objectInputStream.close();

        HashMap keywords = (HashMap) object;
        HashMap<String, Integer> inputResult = kkmaQuery(query);

        HashMap<String, ArrayList<Double>> keyWordList = new HashMap<>();

        Iterator<String> it = inputResult.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            HashMap<Integer,Double> value = (HashMap<Integer, Double>) keywords.get(key);

            ArrayList<Double> needValue = new ArrayList<>();
            for(int i = 0; i<5; i++) {
                needValue.add(value.get(i));
            }
            keyWordList.put(key,needValue);
        }

        // 해쉬맵에 키워드 , ArrayList로 만들어 놓고

        HashMap<String, Double> answer = new HashMap<>();

        for(int i = 0; i<5; i++) {
            Iterator<String> l = inputResult.keySet().iterator();
            double temp = 0;
            while (l.hasNext()) {
                String key = l.next();
                temp += inputResult.get(key) * keyWordList.get(key).get(i);
            }
            answer.put(titleList.get(i),temp);
        }

        List<String> answerList = new ArrayList<>(answer.keySet());
        Collections.sort(answerList, (o1,o2) -> answer.get(o2).compareTo(answer.get(o1)));

        int i = 0;
        StringBuilder sb = new StringBuilder();
        for(String key : answerList) {
            sb.append(key).append("\n");
            i++;
            if(i == 3) {
                break;
            }
        }
        System.out.println(sb);
    }
}
