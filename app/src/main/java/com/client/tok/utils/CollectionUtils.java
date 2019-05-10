package com.client.tok.utils;

import com.client.tok.bean.ContactInfo;
import com.client.tok.pagejump.GlobalParams;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import scala.collection.Iterator;
import scala.collection.mutable.ArrayBuffer;

public class CollectionUtils {

    public static List convertArrayBufferToList(ArrayBuffer arrayBuffer) {
        List list = new ArrayList<>();
        Iterator it = arrayBuffer.iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }

    public static Comparator<ContactInfo> contactLetterComparator() {
        return new Comparator<ContactInfo>() {
            @Override
            public int compare(ContactInfo a, ContactInfo b) {
                if (!a.getFirstLetter().equals("#") && b.getFirstLetter().equals("#")) {
                    return -1;
                } else if (a.getFirstLetter().equals("#") && !b.getFirstLetter().equals("#")) {
                    return 1;
                } else {
                    return a.getFirstLetter().compareTo(b.getFirstLetter());
                }
            }
        };
    }


    /**
     * list2string, Separation ","
     *
     * @param list list
     * @return string
     */
    public static String list2String(List<String> list) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != list.size() - 1) {
                sb.append(GlobalParams.STR_CONNECTOR);
            }
        }
        return sb.toString();
    }
}
