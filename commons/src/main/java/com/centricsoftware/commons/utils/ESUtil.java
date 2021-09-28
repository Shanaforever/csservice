/**
* @author GHUANG
* @version 2019年5月20日 下午8:32:44
*
*/
package com.centricsoftware.commons.utils;

import com.centricsoftware.config.entity.CsProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
@Slf4j
public class ESUtil {
    protected RestHighLevelClient client;

    public static String CLASSNAME = Thread.currentThread().getStackTrace()[1].getClassName();

    public static String host;

    public static int port;

    public static String http;

    static{
        CsProperties properties = NodeUtil.getProperties();
        host = properties.getValue("host", "localhost");
        port = Integer.parseInt(properties.getValue("es.portNumber", "9200"));
        http= properties.getValue("es.proto", "http");
    }

    /**
     * Java High Level REST Client 初始化
     */
    public ESUtil() {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, port, http)));
    }

    /**
     *
     * @param index
     * @param termkey
     * @param termvalue
     * @param timekey
     * @param fromTime
     *            2018-01-26T08:00:00Z
     * @param toTime
     *            2018-01-26T08:00:00Z
     * @author GHUANG
     * @version 2019年5月29日 下午3:12:15
     */
    public void termquery(String index, String termkey, String termvalue, String timekey, String fromTime,
            String toTime) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // sourceBuilder.from(0);
        // sourceBuilder.size(3);
        // sourceBuilder.fetchSource(new String[] { "index", "source" }, new String[] {});

        if (termkey != null && termvalue != null) {
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(termkey, termvalue);
            sourceBuilder.query(termQueryBuilder);
            // MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(termkey, termvalue);
            // sourceBuilder.query(matchQueryBuilder);
        }
        if (timekey != null) {
            RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(timekey);
            rangeQueryBuilder.gte(fromTime);
            rangeQueryBuilder.lte(toTime);
            sourceBuilder.query(rangeQueryBuilder);
        }
        // BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        // sourceBuilder.query(boolBuilder);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.source(sourceBuilder);
        System.out.println(searchRequest);
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            // System.out.println(response);
            for (SearchHit hit : response.getHits()) {
                // System.out.println(hit.getFields() + "");
                // Map<String, Object> sourceMap = hit.getSourceAsMap();
                String sourceString = hit.getSourceAsString();
                System.out.println(sourceString + "");
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 单条记录
     *
     * @param indexName
     * @param jsonStr
     * @author GHUANG
     * @version 2019年6月1日 上午12:27:12
     */
    public void addData(String indexName, String jsonStr) {
        try {
            log.info("index " + indexName, CLASSNAME);
            IndexRequest request = new IndexRequest(indexName); // 文档id
            request.source(jsonStr, XContentType.JSON);
            IndexResponse indexResponse = null;
            try {
                indexResponse = client.index(request, RequestOptions.DEFAULT);
            } catch (ElasticsearchException e) {
                log.error(CLASSNAME, e);
                if (e.status() == RestStatus.CONFLICT) {
                    log.info("发生冲突！");
                }
            }
            if (indexResponse != null) {
                log.info("修改Response=" + indexResponse.getResult());
                if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
                    log.info("新增成功！");
                } else if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
                    log.info("修改成功！");
                }
                ReplicationResponse.ShardInfo shardInfo = indexResponse.getShardInfo();
                if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
                    log.info("分片处理信息..");
                }
                if (shardInfo.getFailed() > 0) {
                    for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                        String reason = failure.reason();
                        log.info("副本失败原因：" + reason);
                    }
                }
            }

        } catch (Exception e) {
            log.error(CLASSNAME, e);
        }
    }

    /**
     *
     * @param indexName
     * @param list
     * @author GHUANG
     * @version 2019年6月1日 上午12:27:06
     */
    public void bulkDate(String indexName, List<Map<String, Object>> list) {
        try {

            if (null == list || list.size() <= 0) {
                return;
            }
            if (StringUtils.isBlank(indexName)) {
                return;
            }
            BulkRequest request = new BulkRequest();
            for (Map<String, Object> map : list) {
                request.add(new IndexRequest(indexName)
                        .source(map, XContentType.JSON));
            }
            /*
             * request.timeout("2m"); request.setRefreshPolicy("wait_for"); request.waitForActiveShards(2);
             */
            // 同步请求
            BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
            // 4、处理响应
            if (bulkResponse != null) {
                for (BulkItemResponse bulkItemResponse : bulkResponse) {
                    DocWriteResponse itemResponse = bulkItemResponse.getResponse();

                    if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                            || bulkItemResponse.getOpType() == DocWriteRequest.OpType.CREATE) {
                        IndexResponse indexResponse = (IndexResponse) itemResponse;
                        // TODO 新增成功的处理
                        log.info("新增成功！" + indexResponse);
                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE) {
                        UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                        // TODO 修改成功的处理
                        log.info("修改成功！" + updateResponse);
                    } else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE) {
                        DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                        // TODO 删除成功的处理
                        log.info("删除成功！" + deleteResponse);
                    }
                }
            }
        } catch (IOException e) {
            log.error(CLASSNAME, e);
        }
    }

    public static void main(String ags[]) throws Exception {
        // ESUtil esUtil = new ESUtil();
//        Map<String, Object> map1 = new HashMap<String, Object>();
//        map1.put("id", "11");
//        map1.put("type", "change");
//        map1.put("postDate", "2018-08-30");
//        map1.put("user", "cc123");
//        map1.put("attribute", "Node");
//        JSONObject json = new JSONObject();
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        list.add(map1);
//        json.put("id", "9");
//        json.put("type", "change");
//        json.put("postDate", "2018-08-30");
//        json.put("user", "cc12345");
//        json.put("attribute", "Node");
//
//        esUtil.bulkDate("changenode2", list);
        String str = "{\"comment\":\"一体织\",\"vendorFullName\":\"海宁市叶氏针织有限公司\",\"licenseNumber\":\"990569\",\"supplierStatus\":\"A4\",\"country\":\"CN\",\"providerType\":\"APP\",\"stateProvince\":\"130\",\"name\":\"叶氏\",\"baseOfProduce\":\"02\",\"languagesSpoken\":\"ZH\",\"vendorSAPNO\":\"990569\",\"timeOfApply\":\"20110614\",\"Status\":\"success\",\"Loginfo\":\"接收成功\"}";
        // esUtil.addData("plmchangetrack", str);
        log.info(str);
        // esUtil.termquery("track", "attrflag", "262208", null, null, null);

    }
}
