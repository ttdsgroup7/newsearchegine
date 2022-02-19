package top.caohongchuan.newsearch.tools;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Calculate post expression from query string
 *
 * @author Hongchuan CAO
 * @version 1.0
 */
@Component
public class ParseQuery {

    @Autowired
    WordProcess wordProcess;

    /**
     * middle expression to post expression
     *
     * @param querystr query string
     * @return post expression, a list contain words and keywords
     */
    public ArrayList<String> getPostExpr(String querystr) {
        // stack and post list
        LinkedList<String> stack = new LinkedList<>();
        ArrayList<String> postexpr = new ArrayList<>();
        // traverse middle expression from left to right
        int len = querystr.length();
        int cur_index = 0;
        while (cur_index < len) {
            if (querystr.charAt(cur_index) == '(') {
                stack.addLast("(");
                cur_index++;
            } else if (querystr.charAt(cur_index) == ')') {
                while (!stack.isEmpty()) {
                    String cur_str = stack.removeLast();
                    if (cur_str.equals("(")) {
                        break;
                    }
                    postexpr.add(cur_str);
                }
                cur_index++;
            } else if (cur_index + 1 < len && querystr.charAt(cur_index) == 'O' && querystr.charAt(cur_index + 1) == 'R') {
                if (!stack.isEmpty()) {
                    String cur_str = stack.getLast();
                    while (cur_str.equals("AND") || cur_str.equals("OR") || cur_str.equals("NOT") || (cur_str.length() >= 6 && cur_str.substring(0, 6).equals("WINDOW"))) {
                        postexpr.add(cur_str);
                        stack.removeLast();
                        if (stack.isEmpty()) {
                            break;
                        }
                        cur_str = stack.getLast();
                    }
                }
                stack.addLast("OR");
                cur_index += 2;
            } else if (cur_index + 2 < len && querystr.charAt(cur_index) == 'A' && querystr.charAt(cur_index + 1) == 'N' && querystr.charAt(cur_index + 2) == 'D') {
                if (!stack.isEmpty()) {
                    String cur_str = stack.getLast();
                    while (cur_str.equals("AND") || cur_str.equals("OR") || cur_str.equals("NOT") || (cur_str.length() >= 6 && cur_str.substring(0, 6).equals("WINDOW"))) {
                        postexpr.add(cur_str);
                        stack.removeLast();
                        if (stack.isEmpty()) {
                            break;
                        }
                        cur_str = stack.getLast();
                    }
                }
                stack.addLast("AND");
                cur_index += 3;
            } else if (cur_index + 2 < len && querystr.charAt(cur_index) == 'N' && querystr.charAt(cur_index + 1) == 'O' && querystr.charAt(cur_index + 2) == 'T') {
                if (!stack.isEmpty()) {
                    String cur_str = stack.getLast();
                    while (cur_str.equals("NOT") || (cur_str.length() >= 6 && cur_str.substring(0, 6).equals("WINDOW"))) {
                        postexpr.add(cur_str);
                        stack.removeLast();
                        if (stack.isEmpty()) {
                            break;
                        }
                        cur_str = stack.getLast();
                    }
                }
                stack.addLast("NOT");
                cur_index += 3;
            } else if (querystr.charAt(cur_index) == 'W' && cur_index + 5 < len && querystr.substring(cur_index, cur_index + 6).equals("WINDOW")) {
                if (!stack.isEmpty()) {
                    String cur_str = stack.getLast();
                    while (cur_str.length() >= 6 && cur_str.substring(0, 6).equals("WINDOW")) {
                        postexpr.add(cur_str);
                        stack.removeLast();
                        if (stack.isEmpty()) {
                            break;
                        }
                        cur_str = stack.getLast();
                    }
                }
                int stopindex = querystr.indexOf("]", cur_index);
                stack.addLast(querystr.substring(cur_index, stopindex + 1));
                cur_index = stopindex + 1;
            } else if (querystr.charAt(cur_index) == ' ') {
                cur_index++;
            } else {
                int stopindex = querystr.indexOf(" ", cur_index);
                if (stopindex == -1) {
                    stopindex = querystr.length();
                }
                postexpr.add(wordProcess.dealWord(querystr.substring(cur_index, stopindex)));
                cur_index = stopindex + 1;
            }
        }
        // add rest to post expression
        while (!stack.isEmpty()) {
            postexpr.add(stack.removeLast());
        }
        return postexpr;
    }
}
