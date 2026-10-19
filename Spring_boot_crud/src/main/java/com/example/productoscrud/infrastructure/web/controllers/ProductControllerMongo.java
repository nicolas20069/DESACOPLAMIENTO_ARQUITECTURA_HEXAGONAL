package com.example.productoscrud.infrastructure.web.controllers;

import com.example.productoscrud.application.service.ProductService;
import com.example.productoscrud.domain.model.Product;
import com.example.productoscrud.infrastructure.persistence.mongo.ProductRepositoryMongo;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mongo/productos")
@CrossOrigin(origins = "*")
public class ProductControllerMongo {
    private final ProductService productService;
    private final ProductRepositoryMongo productRepositoryMongo;

    public ProductControllerMongo(ProductRepositoryMongo productRepositoryMongo) {
        this.productRepositoryMongo = productRepositoryMongo;
        this.productService = new ProductService(productRepositoryMongo);
    }

    @GetMapping({"", "/"})
    public List<Product> listar() {
        return productService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> buscarPorId(@PathVariable int id) {
        return productService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping({"", "/"})
    public ResponseEntity<?> crear(@RequestBody Product product) {
        if (!product.esValido()) {
            return ResponseEntity.badRequest().body("Nombre, precio o descripción inválidos");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.crear(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Product product) {
        if (!product.esValido()) {
            return ResponseEntity.badRequest().body("Nombre, precio o descripción inválidos");
        }
        return productService.actualizar(id, product)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        return productService.eliminar(id)
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }

    @GetMapping("/filtro/precio-mayor")
    public Map<String, Object> listarPorPrecioMayorQue(@RequestParam BigDecimal minimo) {
        List<Product> productos = productRepositoryMongo.listarPorPrecioMayorQue(minimo);
        return Map.of(
            "pipeline", "[{ \"$match\": { \"price\": { \"$gt\": " + minimo + " } } }, { \"$sort\": { \"price\": -1 } }]",
            "productos", productos
        );
    }

    @GetMapping("/filtro/nombre")
    public Map<String, Object> listarPorNombreContiene(@RequestParam String termino) {
        List<Product> productos = productRepositoryMongo.listarPorNombreContiene(termino);
        return Map.of(
            "pipeline", "[{ \"$match\": { \"name\": { \"$regex\": \"" + termino + "\", \"$options\": \"i\" } } }, { \"$sort\": { \"name\": 1 } }]",
            "productos", productos
        );
    }

    @GetMapping("/filtro/descripcion")
    public Map<String, Object> listarPorDescripcionContiene(@RequestParam String termino) {
        List<Product> productos = productRepositoryMongo.listarPorDescripcionContiene(termino);
        return Map.of(
            "pipeline", "[{ \"$match\": { \"description\": { \"$regex\": \"" + termino + "\", \"$options\": \"i\" } } }, { \"$sort\": { \"description\": 1 } }]",
            "productos", productos
        );
    }

    @GetMapping("/filtro/rango-precio")
    public Map<String, Object> listarPorRangoPrecio(
        @RequestParam BigDecimal minimo,
        @RequestParam BigDecimal maximo
    ) {
        List<Product> productos = productRepositoryMongo.listarPorRangoPrecio(minimo, maximo);
        return Map.of(
            "pipeline", "[{ \"$match\": { \"price\": { \"$gte\": " + minimo + ", \"$lte\": " + maximo + " } } }, { \"$sort\": { \"price\": 1 } }]",
            "productos", productos
        );
    }
}
