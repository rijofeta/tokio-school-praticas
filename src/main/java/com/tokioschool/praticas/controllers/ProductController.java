package com.tokioschool.praticas.controllers;

import com.tokioschool.praticas.domain.Product;
import com.tokioschool.praticas.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @GetMapping("/products")
    public String findAll(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "products";
    }

    @GetMapping(value = "/products", params = "string")
    public ResponseEntity<Set<Product>> findByNameContainig(@RequestParam String string) {
        return ResponseEntity.ok(productService.findByNameContainig(string));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/products/stock")
    public ResponseEntity<Set<Product>> findByInStock() {
        return ResponseEntity.ok(productService.findByInStock());
    }

    @GetMapping("/products/create_product")
    public String getCreateProductPage(Model model) {
        model.addAttribute("product", new Product());
        return "create_product";
    }

    @PostMapping("/products/create_product")
    public String createProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        productService.createProduct(product);
        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("savedProductName", product.getName());
        logger.atInfo()
                .setMessage("A product was created.")
                .addKeyValue("createdProduct", product)
                .log();
        return "redirect:/products/create_product";
    }

    @GetMapping("/products/update_product")
    public String getUpdateProductPage(@RequestParam Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "update_product";
    }

    @PutMapping("/products/update_product")
    public String updateProduct(@ModelAttribute Product product) {
        productService.updateProduct(product);
        logger.atInfo()
                .setMessage("A product was updated.")
                .addKeyValue("updatedProduct", product)
                .log();
        return "redirect:/products";
    }

    @DeleteMapping("/products/delete_product/{id}")
    public String deleteProduct(@PathVariable Long id) {
        Product deletedProduct = productService.findById(id);
        productService.deleteProduct(id);
        logger.atInfo()
                .setMessage("A product was deleted.")
                .addKeyValue("deletedProduct", deletedProduct)
                .log();
        return "redirect:/products";
    }
}
