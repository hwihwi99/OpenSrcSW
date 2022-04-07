import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

public class indexPost {
    @SuppressWarnings({"rawtypes", "unchecked", "nls"})
    public void makeIndexPost(String path) throws IOException, ParserConfigurationException, SAXException, ClassNotFoundException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        File file = new File(path);

        // index.xml 파싱을 위한 곳
        // 일단 root 태그 가져오고 그 바로 아래에 있는 doc 태그를 읽어왔습니다.
        Document document = builder.parse(file);
        Element root = document.getDocumentElement();
        System.out.println(root.getNodeName());
        NodeList nodeLists = root.getElementsByTagName("doc");

        // 각 파일별로 데이터에대한 정보를 hashMap

        ArrayList<HashMap<String, Integer>> allDataArray = new ArrayList<>();

        for(int i = 0; i<nodeLists.getLength(); i++) {
            Element node = (Element) nodeLists.item(i);

            // body 태그 속 정보가져오기
            NodeList body = node.getElementsByTagName("body");
            String xmlBody = body.item(0).getTextContent();
            String[] stringArr = xmlBody.split("#");
            HashMap<String, Integer> hashMap = new HashMap<>();
            for(String s : stringArr) {
                String[] str = s.split(":");
                hashMap.put(str[0], Integer.parseInt(str[1]));
            }
            allDataArray.add(hashMap);
        }

        HashMap<String, HashMap<Integer, Double>> resultHashMap = new HashMap<>();

        int N = allDataArray.size();

        for(int i = 0; i<allDataArray.size(); i++) {
            HashMap<String, Integer> tempHash = allDataArray.get(i);
            for(Map.Entry<String, Integer> entrySet : tempHash.entrySet()) {

                if(!resultHashMap.containsKey(entrySet.getKey())) {
                    HashMap<Integer,Double> h = new HashMap<>();
                    h.put(0,0.0);
                    h.put(1,0.0);
                    h.put(2,0.0);
                    h.put(3,0.0);
                    h.put(4,0.0);
                    resultHashMap.put(entrySet.getKey(),h);
                }

                int tfxy = entrySet.getValue();
                int dfx = 0;
                for(HashMap<String,Integer> ha : allDataArray) {
                    if(ha.containsKey(entrySet.getKey())){
                        dfx++;
                    }
                }
                double tempWxy = N / dfx;
                double wxy = tfxy * Math.log(tempWxy);
                wxy = Math.round(wxy*100) / 100.0;
                resultHashMap.get(entrySet.getKey()).put(i,wxy);
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream("./index.post");

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(resultHashMap);


        objectOutputStream.close();

        FileInputStream fileInputStream = new FileInputStream("./index.post");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        Object object = objectInputStream.readObject();
        objectInputStream.close();
        System.out.println("읽어온 객체의 type->"+object.getClass());

        HashMap hashMap = (HashMap) object;

        Iterator<String> it = hashMap.keySet().iterator();
        StringBuilder sb = new StringBuilder();
        while(it.hasNext()){  // 맵키가 존재할경우


            String key = it.next();  // 맵키를 꺼냄
            sb.append(key).append("=>");
            HashMap<Integer, Double> value   = (HashMap<Integer, Double>) hashMap.get(key);  // 키에 해당되는 객체 꺼냄

            Iterator<Integer> l = value.keySet().iterator();
            while (l.hasNext()) {
                Integer subKey = l.next();
                Double subValue = value.get(subKey);
                sb.append(subKey).append(" ").append(subValue).append(" ");
            }
            sb.append("\n");
        }

        System.out.println(sb.toString());

    }
}
