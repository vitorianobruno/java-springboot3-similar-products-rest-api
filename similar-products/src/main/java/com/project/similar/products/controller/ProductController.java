package com.project.similar.products.controller;

import com.project.similar.products.exceptions.ProductNotFoundException;
import com.project.similar.products.model.ProductDetail;
import com.project.similar.products.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}/similar")
    @Operation(summary = "Similar products", operationId = "get-product-similar")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Product Not found")
    })
    public ResponseEntity<List<ProductDetail>> getSimilarProducts(
            @Parameter(description = "Product ID", required = true)
            @PathVariable String productId
    ) {
        if(productService.getProductDetail(productId) == null) {
            throw new ProductNotFoundException("Product not found");
        }
        List<ProductDetail> similarProducts = productService.getSimilarProductDetails(productId);
        return ResponseEntity.ok(similarProducts);
    }
}