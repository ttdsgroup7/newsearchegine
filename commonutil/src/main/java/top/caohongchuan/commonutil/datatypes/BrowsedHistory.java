package top.caohongchuan.commonutil.datatypes;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@Data
public class BrowsedHistory {
    private HashMap<Integer, ArrayList<Long>> userNews;
    private Set<Long> newsSet;
}
