package com.project.similar.products.service;

import com.project.similar.products.model.ProductDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductService productService;

    @Mock
    private WebClient webClient;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService("http://fake-api.com");

        // Inject mock WebClient into service using reflection (workaround for constructor-based init)
        // In real code, you'd better inject the WebClient externally
        try {
            java.lang.reflect.Field field = ProductService.class.getDeclaredField("webClient");
            field.setAccessible(true);
            field.set(productService, webClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetSimilarProductIds() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/product/{productId}/similarids", "123")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(String.class)).thenReturn(Flux.just("[1,2,3]"));

        List<String> result = productService.getSimilarProductIds("123");
        assertEquals(1, result.size());
        assertEquals("[1,2,3]", result.get(0));
    }

    @Test
    void testGetProductDetail_Success() {
        ProductDetail mockDetail = new ProductDetail("123", "Product Name", 99.99, true);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/product/{productId}", "123")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductDetail.class)).thenReturn(Mono.just(mockDetail));

        ProductDetail result = productService.getProductDetail("123");

        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals("Product Name", result.getName());
    }

    @Test
    void testGetProductDetail_NotFound() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/product/{productId}", "999")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProductDetail.class)).thenReturn(Mono.error(new RuntimeException("Not found")));

        ProductDetail result = productService.getProductDetail("999");
        assertNull(result);
    }

    @Test
    void testGetSimilarProductDetails() {
        // Step 1: getSimilarProductIds returns ["[100,200]"]
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/product/{productId}/similarids", "1")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(String.class)).thenReturn(Flux.just("[100,200]"));

        // Step 2: getProductDetail returns real product
        when(requestHeadersUriSpec.uri("/product/{productId}", "100")).thenReturn(requestHeadersSpec);
        when(requestHeadersUriSpec.uri("/product/{productId}", "200")).thenReturn(requestHeadersSpec);
        when(responseSpec.bodyToMono(ProductDetail.class))
                .thenReturn(Mono.just(new ProductDetail("100", "P100", 50.0, true)))
                .thenReturn(Mono.just(new ProductDetail("200", "P200", 80.0, true)));

        List<ProductDetail> result = productService.getSimilarProductDetails("1");

        assertEquals(2, result.size());
        assertEquals("100", result.get(0).getId());
        assertEquals("200", result.get(1).getId());
    }
}
