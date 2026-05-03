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

class ProductServiceIntegrationStep4Test {

    @TempDir
    Path tempDir;

    private FileProductRepository productRepo;
    private ProductValidator productValidator;
    private ProductService productService;

    @BeforeEach
    void setUp() throws IOException {
        // setup: real repository backed by a temp file
        Path repoFile = tempDir.resolve("products-step4.txt");
        Files.write(repoFile, new byte[0]);
        productRepo = new FileProductRepository(repoFile.toString());

        // setup: real validator and service
        productValidator = new ProductValidator();
        productService = new ProductService(productRepo, productValidator);
    }

    @Test
    void addProduct_valid_persistsEntity() {
        // setup: real product entity (E)
        Product product = new Product(21, "Mocha", 16.5, CategorieBautura.SPECIAL_COFFEE, TipBautura.DAIRY);

        // execution
        productService.addProduct(product);

        // assert: repository state updated with real entity
        assertEquals(1, productService.getAllProducts().size());
        assertNotNull(productService.findById(21));
    }

    @Test
    void addProduct_invalid_doesNotPersistEntity() {
        // setup: invalid real product entity (E)
        Product product = new Product(0, "", -2.0, CategorieBautura.TEA, TipBautura.WATER_BASED);

        // execution + assert
        assertThrows(ValidationException.class, () -> productService.addProduct(product));

        // assert: repository remains unchanged
        assertEquals(0, productService.getAllProducts().size());
    }
}
