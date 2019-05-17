import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wyl
 * @date 2019/04/01
 */
public class CommonUtils {

    private static RestTemplate restTemplate = new RestTemplate();

    private CommonUtils(){}

    /**
     * 判断list不为空
     * @param list
     * @return
     */
    public static boolean isNotBlank(List list) {
        return null != list && 0 < list.size();
    }

    /**
     * 判断map不为空
     * @param map
     * @return
     */
    public static boolean isNotBlank(Map map) {
        return null != map && 0 < map.size();
    }

    /**
     * 判断byte[] 不为空
     * @param bytes
     * @return
     */
    public static boolean isNotBlank(byte[] bytes) {
        return null != bytes && 0 < bytes.length;
    }

    /**
     * 判断字符串不为空
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        return null != str && !"".equals(str);
    }
    /**
     * 处理空字符串
     * @param str
     * @return
     */
    public static String blankString(String str) {
        return null == str ? "" : str;
    }

    /**
     * 调用远程url接口
     * @param param
     * @return
     */
    public static Map<String, Object> callInterface(String url, Map<String, Object> param) {

        return restTemplate.postForObject(url, param, Map.class);
    }



    /**
     * 表格接口将List<Map>根据表头进行重排序
     * @param tableDataList 表格数据
     * @param titleList 表头数据
     * @return
     */
    public static List<Map<String,Object>> sort(List<Map<String,Object>> tableDataList, List<Map<String, Object>> titleList) {
        List<Map<String,Object>> returnList = new ArrayList<>(tableDataList.size());
        for(Map<String,Object> tableDataMap : tableDataList){
            Map<String,Object> reSortMap = new LinkedHashMap<>();
            for(Map<String,Object> titleMap:titleList){
                for(Map.Entry<String, Object> entry : tableDataMap.entrySet()){
                    if(entry.getKey().equals(titleMap.get("dataIndex"))){
                        reSortMap.put(entry.getKey(),entry.getValue());
                        break;
                    }
                }
            }
            returnList.add(reSortMap);
        }
        return returnList;
    }

    /**
     * 调用远程url接口
     * @param url,param
     * @return
     */
    public static Map<String, Object> callBdeInterface(String url, Map<String, Object> param) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");
        headers.add("Cookie", "JSESSIONID="+param.get("token").toString());
        HttpEntity<Map<String, Object>> newParam = new HttpEntity<>(param, headers);
        Map<String, Object> responseMap =restTemplate.postForObject(url, newParam, Map.class);
        return responseMap;
    }

}
