package com.myweb.jumia;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class JumiaAPI {
    private static final String HASH_ALGORITHM = "HmacSHA256";
    private static final String CHAR_UTF_8 = "UTF-8";
    private static final String CHAR_ASCII = "ASCII";

    public static ErrorResponse update(Map<String,String> map, APIKey apiKey) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> params = new HashMap<String, String>();
        params.put("Action", "ProductUpdate");
        params.put("Format", "JSON");
        params.put("Timestamp", getCurrentTimestamp());
        //params.put("UserID", "421585547@QQ.COM");
        params.put("UserID", apiKey.getUserId());
        params.put("Version", "1.0");
        //final String apiKey = "8c4c55f8abb921bd03b8daf94401d05ff5df34bc";
        String products = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            //System.out.println(entry.getKey() + ":" + entry.getValue());
            products += "<Product><SellerSku>" + entry.getKey() + "</SellerSku><Quantity>" + entry.getValue() + "</Quantity></Product>";
        }
        final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><Request>"+products+"</Request>";
        final String out = getSellercenterApiResponse(params, apiKey.getApiKey(), XML, "POST",apiKey.getApiUrl()); // provide XML as an empty stringwhen not needed
        //System.out.println(out); // print out the XML response
        ErrorResponse response = mapper.readValue(out, ErrorResponse.class);
        return response;
    }


    public static void main(String[] args) throws IOException {
        Map<String,String> map = new HashMap<String,String>();
        map.put("Nokia105DSU-2","55");
        map.put("170275301","15");

        APIKey apiKey = new APIKey();

        apiKey.setUserId("1661317838@qq.com");
        apiKey.setApiKey("c9c206d2571d2d33cc01007e6c1ff3ab849766ef");
        apiKey.setApiUrl("https://sellercenter-api.jumia.com.ng");

//        apiKey.setUserId("421585547@qq.com");
//        apiKey.setApiKey("248247b9aa88cdf2b27455364e9ee931f3de263c");
//        apiKey.setApiUrl("https://sellercenter-api.jumia.co.ke");

        String apiurl = "";
        ErrorResponse errorResponse = update(map,apiKey);
        if (errorResponse.getErrorResponse() != null) {
            System.out.println(errorResponse.getErrorResponse().getHead().getErrorMessage());
        } else {
            System.out.println("新获取到最后45条的商口更新库存成功！");
            return;
        }
    }

    /**
     * calculates the signature and sends the request
     *
     * @param params Map - request parameters
     * @param apiKey String - user's API Key
     * @param XML    String - Request Body
     */
    public static String getSellercenterApiResponse(Map<String, String> params, String apiKey, String XML, String requestMothed,String apiUrl) {
        String queryString = "";
        String Output = "";
        HttpURLConnection connection = null;
        URL url = null;
        Map<String, String> sortedParams = new TreeMap<String, String>(params);
        queryString = toQueryString(sortedParams);
        final String signature = hmacDigest(queryString, apiKey, HASH_ALGORITHM);
        queryString = queryString.concat("&Signature=".concat(signature));
        final String request = apiUrl.concat("?".concat(queryString));
        try {
            url = new URL(request);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(requestMothed);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", CHAR_UTF_8);
            connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            connection.setUseCaches(false);
            if (!XML.equals("")) {
                connection.setRequestProperty("Content-Length", "" + Integer.toString(XML.getBytes().length));
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(XML);
                wr.flush();
                wr.close();
            }
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                Output += line + "\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Output;
    }

    /**
     * generates hash key
     *
     * @param msg
     * @param keyString
     * @param algo
     * @return string
     */
    private static String hmacDigest(String msg, String keyString, String algo) {
        String digest = null;
        try {
            SecretKeySpec key = new SecretKeySpec((keyString).getBytes(CHAR_UTF_8), algo);
            Mac mac = Mac.getInstance(algo);
            mac.init(key);
            final byte[] bytes = mac.doFinal(msg.getBytes(CHAR_ASCII));
            StringBuffer hash = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    hash.append('0');
                }
                hash.append(hex);
            }
            digest = hash.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return digest;
    }

    /**
     * build querystring out of params map
     *
     * @param data map of params
     * @return string
     * @throws UnsupportedEncodingException
     */
    private static String toQueryString(Map<String, String> data) {
        String queryString = "";
        try {
            StringBuffer params = new StringBuffer();
            for (Map.Entry<String, String> pair : data.entrySet()) {
                params.append(URLEncoder.encode((String) pair.getKey(), CHAR_UTF_8) + "=");
                params.append(URLEncoder.encode((String) pair.getValue(), CHAR_UTF_8) + "&");
            }
            if (params.length() > 0) {
                params.deleteCharAt(params.length() - 1);
            }
            queryString = params.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return queryString;
    }

    /**
     * returns the current timestamp
     *
     * @return current timestamp in ISO 8601 format
     */
    private static String getCurrentTimestamp() {
        final TimeZone tz = TimeZone.getTimeZone("UTC");
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        df.setTimeZone(tz);
        final String nowAsISO = df.format(new Date());
        return nowAsISO;
    }
}