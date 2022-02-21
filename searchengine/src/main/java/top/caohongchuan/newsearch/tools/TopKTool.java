package top.caohongchuan.newsearch.tools;


import java.util.*;

public class TopKTool {

    static Comparator<Map.Entry<String, Double>> cmp = new Comparator<>() {
        @Override
        public int compare(Map.Entry<String, Double> e1, Map.Entry<String, Double> e2) {
            if (e2.getValue() - e1.getValue() > 0) {
                return 1;
            } else if (e2.getValue() - e1.getValue() < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    public static List<Map.Entry<String, Double>> getTopK(int k, Map<String, String> dataMap) {
        HashMap<String, Double> dataF = new HashMap<>();
        for(Map.Entry<String, String> objectEntry: dataMap.entrySet()){
            dataF.put(objectEntry.getKey(), Double.parseDouble(objectEntry.getValue()));
        }
        List<Map.Entry<String, Double>> data = new ArrayList<>(dataF.entrySet());

        PriorityQueue<Map.Entry<String, Double>> priorityQueue = new PriorityQueue<>(cmp);
        for (int i = 0; i < k; i++) {
            priorityQueue.add(data.get(i));
        }
        for (int i = k; i < data.size(); i++) {
            if(priorityQueue.peek().getValue() < data.get(i).getValue()){
                priorityQueue.poll();
                priorityQueue.add(data.get(i));
            }
        }
        List<Map.Entry<String, Double>> res = new ArrayList<>();
        while(!priorityQueue.isEmpty()){
            res.add(priorityQueue.poll());
        }
        Collections.reverse(res);
        return res;
    }

    public static List<Map.Entry<String, Double>> getTopKMap(int k, Map<String, Double> dataMap) {

        List<Map.Entry<String, Double>> data = new ArrayList<>(dataMap.entrySet());

        PriorityQueue<Map.Entry<String, Double>> priorityQueue = new PriorityQueue<>(cmp);
        for (int i = 0; i < k; i++) {
            priorityQueue.add(data.get(i));
        }
        for (int i = k; i < data.size(); i++) {
            if(priorityQueue.peek().getValue() < data.get(i).getValue()){
                priorityQueue.poll();
                priorityQueue.add(data.get(i));
            }
        }
        List<Map.Entry<String, Double>> res = new ArrayList<>();
        while(!priorityQueue.isEmpty()){
            res.add(priorityQueue.poll());
        }
        Collections.reverse(res);
        return res;
    }
}
