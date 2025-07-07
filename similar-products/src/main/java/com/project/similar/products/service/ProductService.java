package com.project.similar.products.service;

import com.project.similar.products.model.ProductDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final WebClient webClient;

    public ProductService(@Value("${external.api.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<String> getSimilarProductIds(String productId) {
        return webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
                .block();
    }

    public ProductDetail getProductDetail(String productId) {
        try {
            return webClient.get()
                    .uri("/product/{productId}", productId)
                    .retrieve()
                    .bodyToMono(ProductDetail.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }

    public List<ProductDetail> getSimilarProductDetails(String productId) {
        List<String> stringIds = getSimilarProductIds(productId);
        List<String> ids = extractIds(stringIds.get(0));
        return ids.stream()
                .map(this::getProductDetail)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<String> extractIds(String ids) {
        ids = ids.replaceAll("[\\[\\]]", "");
        return Arrays.stream(ids.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

}