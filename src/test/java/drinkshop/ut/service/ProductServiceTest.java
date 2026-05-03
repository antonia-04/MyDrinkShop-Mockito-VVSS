package drinkshop.ut.service;

import drinkshop.domain.Product;
import drinkshop.repository.file.FileProductRepository;
import drinkshop.service.ProductService;
import drinkshop.service.validator.ProductValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    // setup: mock dependencies
    @Mock
    private FileProductRepository productRepo;

    // setup: mock validator
    @Mock
    private ProductValidator productValidator;

    // setup: mock entity (E)
    @Mock
    private Product product;

    // setup: system under test with injected mocks
    @InjectMocks
    private ProductService productService;

    @Test
    void addProduct_success_callsRepositorySave() {
        // setup: mock behavior
        doNothing().when(productValidator).validate(product);
        when(productRepo.save(product)).thenReturn(product);

        // assert: no exception and behavior is correct
        assertDoesNotThrow(() -> productService.addProduct(product));

        // verify: interactions with mocks
        verify(productValidator, times(1)).validate(product);
        verify(productRepo, times(1)).save(product);
    }

    @Test
    void addProduct_repoThrows_propagatesException() {
        // setup: mock behavior
        doNothing().when(productValidator).validate(product);
        RuntimeException repoError = new RuntimeException("repo error");
        doThrow(repoError).when(productRepo).save(product);

        // assert: exception is propagated
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> productService.addProduct(product));
        assertSame(repoError, thrown);

        // verify: interactions with mocks
        verify(productValidator, times(1)).validate(product);
        verify(productRepo, times(1)).save(product);
    }
}
