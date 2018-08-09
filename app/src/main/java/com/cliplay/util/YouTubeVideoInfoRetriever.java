package com.cliplay.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;
import java.util.TreeMap;

public class YouTubeVideoInfoRetriever {
    private static final String URL_YOUTUBE_GET_VIDEO_INFO = "http://www.youtube" +
            ".com/get_video_info?&video_id=";
    public static final String KEY_DASH_VIDEO = "dashmpd";
    public static final String KEY_HLS_VIDEO = "hlsvp";
    private TreeMap<String, String> kvpList = new TreeMap<>();

    public void retrieve(String videoId) throws IOException {
        String targetUrl = URL_YOUTUBE_GET_VIDEO_INFO + videoId +
                "&el=info&ps=default&eurl=&gl=US&hl=en";
        SimpleHttpClient client = new SimpleHttpClient();
        String output = client.execute(targetUrl);
        parse(output);
    }

    public String getInfo(String key) {
        return kvpList.get(key);
    }

    public void printAll() {
        System.out.println("TOTAL VARIABLES=" + kvpList.size());
        for (Map.Entry<String, String> entry : kvpList.entrySet()) {
            System.out.print("" + entry.getKey() + "=");
            System.out.println("" + entry.getValue() + "");
        }
    }

    private void parse(String data) throws UnsupportedEncodingException {
        String[] splits = data.split("&");
        String kvpStr;
        if (splits.length < 1) {
            return;
        }
        kvpList.clear();
        for (String split : splits) {
            kvpStr = split;
            try {
                // Data is encoded multiple times
                kvpStr = URLDecoder.decode(kvpStr, SimpleHttpClient.ENCODING_UTF_8);
                kvpStr = URLDecoder.decode(kvpStr, SimpleHttpClient.ENCODING_UTF_8);
                String[] kvpSplits = kvpStr.split("=", 2);
                if (kvpSplits.length == 2) {
                    kvpList.put(kvpSplits[0], kvpSplits[1]);
                } else if (kvpSplits.length == 1) {
                    kvpList.put(kvpSplits[0], "");
                }
            } catch (UnsupportedEncodingException ex) {
                throw ex;
            }
        }
    }

    public static class SimpleHttpClient {
        static final String ENCODING_UTF_8 = "UTF-8";
        static final int DEFAULT_TIMEOUT = 10000;
        static final String HTTP_GET = "GET";

        String execute(String urlStr) throws IOException {
            URL url;
            HttpURLConnection conn = null;
            InputStream inStream;
            OutputStream outStream = null;
            String response = null;
            try {
                url = new URL(urlStr);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(SimpleHttpClient.DEFAULT_TIMEOUT);
                conn.setRequestMethod(SimpleHttpClient.HTTP_GET);
                inStream = new BufferedInputStream(conn.getInputStream());
                response = getInput(inStream);
            } finally {
                if (conn != null && conn.getErrorStream() != null) {
                    String errorResponse = " : ";
                    errorResponse = errorResponse + getInput(conn.getErrorStream());
                    response = response + errorResponse;
                }
                if (conn != null) {
                    conn.disconnect();
                }
            }
            return response;
        }

        private String getInput(InputStream in) throws IOException {
            StringBuilder sb = new StringBuilder(8192);
            byte[] b = new byte[1024];
            int bytesRead;
            while (true) {
                bytesRead = in.read(b);
                if (bytesRead < 0) {
                    break;
                }
                String s = new String(b, 0, bytesRead, ENCODING_UTF_8);
                sb.append(s);
            }
            return sb.toString();
        }
    }
}