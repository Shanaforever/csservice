/**
* @author GHUANG
* @version 2019年4月25日 上午10:29:23
*
*/
package com.centricsoftware.commons.utils;

import ch.qos.logback.classic.Logger;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;

@Slf4j
public class PLMComparator implements Comparator<String> {
    String exp1 = "";

    String exp2 = "";

    String exp3 = "";

    String exp4 = "";

    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

    public void setFirst(String ss) {
        exp1 = ss;
    }

    public void setSec(String ss) {
        exp2 = ss;
    }

    public void setThird(String ss) {
        exp3 = ss;
    }

    public void setFour(String ss) {
        exp4 = ss;
    }

    @Override
    public int compare(String url1, String url2) {
        try{

            String sort1 = NodeUtil.queryExpressionResult(exp1, url1);
            String sort2 = NodeUtil.queryExpressionResult(exp2, url2);
            String sort3 = "";
            String sort4 = "";
            // NodeUtil.outInfo("url1=" + url1, "hang");
            if (exp3.length() > 0) {
                sort3 = NodeUtil.queryExpressionResult(exp3, url1);
            }
            if (exp4.length() > 0) {
                sort4 = NodeUtil.queryExpressionResult(exp4, url2);
            }

            if (sort1 == null || sort1.length() == 0) {
                return 1;
            }
            if (sort2.length() == 0 || sort2 == null) {
                return -1;
            }
            if (sort1.equals(sort2)) {
                if (sort3 == null || sort3.length() == 0) {
                    return 1;
                }
                if (sort4.length() == 0 || sort4 == null) {
                    return -1;
                }
                try {
                    int i = Integer.parseInt(sort3);
                    int j = Integer.parseInt(sort4);
                    if (i < j) {
                        return -1;
                    }
                } catch (Exception e) {
                    return sort3.compareTo(sort4);
                }

            } else {
                // 对日期字段进行升序，如果欲降序可采用before方法
                try {
                    int i = Integer.parseInt(sort1);
                    int j = Integer.parseInt(sort2);
                    if (i < j) {
                        return -1;
                    }
                } catch (Exception e) {
                    return sort1.compareTo(sort2);
                }
            }
        }catch (Exception e){
            log.error("PLMComparator error",e);
        }
        return 1;
    }
}
