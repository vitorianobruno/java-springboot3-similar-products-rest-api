package com.project.similar.products.controller;

import com.project.similar.products.model.ProductDetail;
import com.project.similar.products.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("GET /product/{productId}/similar - Success")
    void givenValidProductId_whenGetSimilarProducts_thenReturnProductList() throws Exception {
        ProductDetail product1 = new ProductDetail("100", "Product 100", 10.0, true);
        ProductDetail product2 = new ProductDetail("101", "Product 101", 10.0, true);

        Mockito.when(productService.getProductDetail("1")).thenReturn(new ProductDetail("1", "Product 1", 10.0, true));
        Mockito.when(productService.getSimilarProductDetails("1")).thenReturn(List.of(product1, product2));

        mockMvc.perform(get("/product/1/similar")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is("100")))
                .andExpect(jsonPath("$[0].name", is("Product 100")))
                .andExpect(jsonPath("$[1].id", is("101")))
                .andExpect(jsonPath("$[1].name", is("Product 101")));
    }

    @Test
    @DisplayName("GET /product/{productId}/similar - Product Not Found")
    void givenInvalidProductId_whenGetSimilarProducts_thenReturn404() throws Exception {
        Mockito.when(productService.getProductDetail(anyString())).thenReturn(null);

        mockMvc.perform(get("/product/999/similar")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
