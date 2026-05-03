package drinkshop.it.service.td.breadthfirst;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.Repository;
import drinkshop.service.ProductService;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceIntegrationStep2Test {

    // setup: mock repository (R)
    @Mock
    private Repository<Integer, Product> productRepo;

    // setup: real validator (V) and real service (S)
    private ProductValidator productValidator;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productValidator = new ProductValidator();
        productService = new ProductService(productRepo, productValidator);
    }

    @Test
    void addProduct_valid_success_callsRepositorySave() {
        // setup: valid product and mock behavior
        Product product = new Product(1, "Latte", 12.5, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);
        when(productRepo.save(product)).thenReturn(product);

        // execution + assert
        assertDoesNotThrow(() -> productService.addProduct(product));

        // verify: repository interaction
        verify(productRepo, times(1)).save(product);
    }

    @Test
    void addProduct_invalid_throws_and_doesNotCallRepository() {
        // setup: invalid product (fails real validator)
        Product product = new Product(0, "", -1.0, CategorieBautura.TEA, TipBautura.WATER_BASED);

        // execution + assert
        assertThrows(ValidationException.class, () -> productService.addProduct(product));

        // verify: repository not touched (Mockito 2.x uses verifyZeroInteractions)
        verifyZeroInteractions(productRepo);
    }
}
