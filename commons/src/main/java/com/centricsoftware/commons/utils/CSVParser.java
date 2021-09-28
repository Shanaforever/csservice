package com.centricsoftware.commons.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/**
 * csv处理工具类
 * 同类方法 {@link cn.hutool.core.text.csv.CsvUtil}
 * @author zheng.gong
 * @date 2020/4/27
 */
public class CSVParser {

    public List<String[]> parse(File file) throws Exception {
        List<String[]> result = new ArrayList<String[]>();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        try {
            String line = null;
            while ((line = in.readLine()) != null) {
                result.add(parseLine(line));
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }

        return result;
    }

    enum State {
        Normal, QuoteBegin, Quote, QuoteEnd,
    }

    public String[] parseLine(String line) {
        ArrayList<String> list = new ArrayList<String>();
        State state = State.Normal;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); ++i) {
            char ch = line.charAt(i);
            switch (state) {
            case Normal:
                if (ch == ',') {
                    list.add(sb.toString());
                    sb.setLength(0);
                } else if (ch == '"') {
                    state = State.QuoteBegin;
                } else {
                    sb.append(ch);
                }
                break;
            case QuoteBegin:
                if (ch == '"') {
                    state = State.Normal;
                    sb.append('"');
                } else {
                    state = State.Quote;
                    sb.append(ch);
                }
                break;
            case Quote:
                if (ch == '"') {
                    state = State.QuoteEnd;
                } else {
                    sb.append(ch);
                }
                break;
            case QuoteEnd:
                if (ch == ',') {
                    state = State.Normal;
                    list.add(sb.toString());
                    sb.setLength(0);
                } else if (ch == '"') {
                    state = State.Quote;
                    sb.append('"');
                } else {
                    state = State.Normal;
                    sb.append(ch);
                }
                break;
            }
        }
        list.add(sb.toString());
        return list.toArray(new String[list.size()]);
    }

}
