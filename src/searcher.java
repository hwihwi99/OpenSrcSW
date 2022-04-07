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
            System.out.println(keyword.getString()+" "+keyword.getCnt());
        }

        return resultString;
    }

    public void CalcSim(String path, String query) throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        File file = new File("./index.xml");

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


        // index.post에서 입력받은 키워드에 있는 key값과 동일한 정보만 받아오기
        Iterator<String> it = inputResult.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            HashMap<Integer,Double> value = (HashMap<Integer, Double>) keywords.get(key);

            ArrayList<Double> needValue = new ArrayList<>();
            for(int i = 0; i< value.size(); i++) {
                needValue.add(value.get(i));
            }
            keyWordList.put(key,needValue);
        }

        // 파일 제목 + 유사도 저장
        HashMap<String, Double> answer = new HashMap<>();



        for(int i = 0; i<5; i++) {
            double A = 0, B = 0, result = 0;
            Iterator<String> l = inputResult.keySet().iterator();
            double temp = 0;

            while (l.hasNext()) {
                String key = l.next();
                temp += inputResult.get(key) * keyWordList.get(key).get(i);
            }

            l = inputResult.keySet().iterator();
            while (l.hasNext()) {
                String key = l.next();
                double result1 = Math.round(inputResult.get(key) * 100) / 100.0;

                A += result1 * result1;
            }

            l = inputResult.keySet().iterator();
            while (l.hasNext()) {
                String key = l.next();
                double result2 = Math.round( keyWordList.get(key).get(i) * 100) / 100.0;
                B += result2 * result2;
            }

            if((Math.sqrt(A) * Math.sqrt(B) != 0)) {
                result = temp / (Math.sqrt(A) * Math.sqrt(B));
            }
            System.out.println(result);
            if(result != 0) {
                answer.put(titleList.get(i),result);
            }
        }

        List<String> answerList = new ArrayList<>(answer.keySet());
        Collections.sort(answerList, (o1,o2) -> answer.get(o2).compareTo(answer.get(o1)));

        if(answerList.size() == 0) {
            System.out.println("검색된 문서가 없습니다");
            return;
        }

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
