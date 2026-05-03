package drinkshop.it.service.td.breadthfirst;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.ProductService;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductServiceIntegrationStep3Test {

    @TempDir
    Path tempDir;

    private FileProductRepository productRepo;
    private ProductValidator productValidator;
    private ProductService productService;

    @BeforeEach
    void setUp() throws IOException {
        // setup: real repository backed by a temp file
        Path repoFile = tempDir.resolve("products-test.txt");
        Files.write(repoFile, new byte[0]);
        productRepo = new FileProductRepository(repoFile.toString());

        // setup: real validator and service
        productValidator = new ProductValidator();
        productService = new ProductService(productRepo, productValidator);
    }

    @Test
    void addProduct_valid_persistsToRepository() {
        // setup: valid product
        Product product = new Product(10, "Cappuccino", 14.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);

        // execution
        productService.addProduct(product);

        // assert: repository state updated
        assertEquals(1, productService.getAllProducts().size());
        assertNotNull(productService.findById(10));
    }

    @Test
    void addProduct_invalid_doesNotPersist() {
        // setup: invalid product
        Product product = new Product(-5, "", 0.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);

        // execution + assert
        assertThrows(ValidationException.class, () -> productService.addProduct(product));

        // assert: repository state unchanged
        assertEquals(0, productService.getAllProducts().size());
    }
}
