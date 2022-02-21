package top.caohongchuan.newsearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;

@Service
public class SpellCorrectionService {

    @Autowired
    CorrectionService corr;

    public static HashSet<String> keyword = new HashSet<>();

    static {
        keyword.add("AND");
        keyword.add("NOT");
        keyword.add("(");
        keyword.add(")");
        keyword.add("OR");
    }

    public String checkQueryString(String qry) {
        String[] qrylist = qry.split(" ");
        ArrayList<String> thiskey = new ArrayList<>();
        StringBuilder s = new StringBuilder();

        for (String value : qrylist) {
            if (keyword.contains(value)) {
                if (s.length() != 0) {
                    s.append(" ");
                    s.append(value);
                } else {
                    s.append(value);
                }
            } else {
                if (s.length() != 0) {
                    thiskey.add(s.toString());
                    s.setLength(0);
                }
            }
        }

        String[] handle = qry.replaceAll("AND|NOT|\\(|\\)", "").replaceAll(" +", " ").split("OR");
        StringBuilder res = new StringBuilder();
        int cur = 0;
        if (keyword.contains(qrylist[0])) {
            res.append(thiskey.get(cur));
            cur++;
        }

        for (String query : handle) {
            String[] to_handle = query.strip().toLowerCase().split(" ");
            for (int i = 0; i < to_handle.length; i++
            ) {

                // error
                if (!(CorrectionService.term_cnt.containsKey(to_handle[i])) && (to_handle[i].charAt(0)!='\"') ) {
                    // edit distance 1
                    HashSet<String> candi = corr.editDis1(to_handle[i]);
                    HashSet<String> candi2 = new HashSet<>(candi);
                    // edit distance 2
                    for (String j : candi
                    ) {
                        candi2.addAll(corr.editDis1(j));

                    }
                    res.append(" ").append(corr.calculate(candi2, to_handle[i], i, to_handle));
                } else {
                    res.append(" ").append(to_handle[i]);
                }
                if (cur < thiskey.size()) {
                    res.append(" ").append(thiskey.get(cur));
                    cur++;
                }
            }
        }

        return res.toString().replaceAll("window\\[(.*?),(.*?)","WINDOW\\[$1,$2\\]").replaceAll("window\\[(.*?),(.*?),(.*?)","WINDOW\\[$1,$2,$3\\]").replaceAll("\"(.*?)\"", "$1").strip();
    }
}
