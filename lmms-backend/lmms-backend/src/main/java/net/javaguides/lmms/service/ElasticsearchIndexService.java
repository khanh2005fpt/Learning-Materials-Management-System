//package net.javaguides.lmms.service;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class ElasticsearchIndexService {
//
//    private final ElasticsearchClient elasticsearchClient;
//
//    public void deleteIndex(String indexName) {
//        try {
//            boolean exists = elasticsearchClient.indices()
//                    .exists(e -> e.index(indexName))
//                    .value();
//
//            if (exists) {
//                elasticsearchClient.indices()
//                        .delete(d -> d.index(indexName));
//
//                System.out.println("Deleted index: " + indexName);
//            } else {
//                System.out.println("Index does not exist: " + indexName);
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to delete index: " + indexName, e);
//        }
//    }
//}