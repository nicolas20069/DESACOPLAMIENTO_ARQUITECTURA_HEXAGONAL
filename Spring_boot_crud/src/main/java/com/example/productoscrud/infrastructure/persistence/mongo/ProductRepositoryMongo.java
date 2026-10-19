package com.example.productoscrud.infrastructure.persistence.mongo;

import com.example.productoscrud.application.ports.ProductRepository;
import com.example.productoscrud.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository("productRepositoryMongo")
public class ProductRepositoryMongo extends ProductRepository {
    private static final String COLLECTION = "products";

    private final MongoTemplate mongoTemplate;

    public ProductRepositoryMongo(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Product> listar() {
        return mongoTemplate.findAll(Product.class, COLLECTION);
    }

    @Override
    public Optional<Product> buscarPorId(int id) {
        return Optional.ofNullable(
            mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), Product.class, COLLECTION)
        );
    }

    @Override
    public Product crear(Product product) {
        int siguienteId = mongoTemplate.findAll(Product.class, COLLECTION).stream()
            .map(Product::getId)
            .filter(java.util.Objects::nonNull)
            .max(Integer::compareTo)
            .orElse(0) + 1;

        product.setId(siguienteId);
        mongoTemplate.insert(product, COLLECTION);
        return product;
    }

    @Override
    public boolean actualizar(int id, Product product) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = new Update()
            .set("name", product.getName())
            .set("price", product.getPrice())
            .set("description", product.getDescription());
        return mongoTemplate.updateFirst(query, update, COLLECTION).getModifiedCount() > 0;
    }

    @Override
    public boolean eliminar(int id) {
        Query query = Query.query(Criteria.where("id").is(id));
        return mongoTemplate.remove(query, COLLECTION).getDeletedCount() > 0;
    }

    public List<Product> listarPorPrecioMayorQue(BigDecimal minimo) {
        var aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("price").gt(minimo)),
            Aggregation.sort(Sort.Direction.DESC, "price")
        );
        return mongoTemplate.aggregate(aggregation, COLLECTION, Product.class).getMappedResults();
    }

    public List<Product> listarPorNombreContiene(String termino) {
        var aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("name").regex(termino, "i")),
            Aggregation.sort(Sort.Direction.ASC, "name")
        );
        return mongoTemplate.aggregate(aggregation, COLLECTION, Product.class).getMappedResults();
    }

    public List<Product> listarPorDescripcionContiene(String termino) {
        var aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("description").regex(termino, "i")),
            Aggregation.sort(Sort.Direction.ASC, "description")
        );
        return mongoTemplate.aggregate(aggregation, COLLECTION, Product.class).getMappedResults();
    }

    public List<Product> listarPorRangoPrecio(BigDecimal minimo, BigDecimal maximo) {
        var aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("price").gte(minimo).lte(maximo)),
            Aggregation.sort(Sort.Direction.ASC, "price")
        );
        return mongoTemplate.aggregate(aggregation, COLLECTION, Product.class).getMappedResults();
    }
}
