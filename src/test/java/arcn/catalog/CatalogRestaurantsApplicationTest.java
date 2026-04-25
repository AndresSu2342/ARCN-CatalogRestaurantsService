package arcn.catalog;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;

@ExtendWith(MockitoExtension.class)
class CatalogRestaurantsApplicationTest {

    @Test
    void mainShouldCallSpringApplicationRun() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {

            CatalogRestaurantsApplication.main(new String[]{});

            mocked.verify(() ->
                SpringApplication.run(CatalogRestaurantsApplication.class, new String[]{})
            );
        }
    }
}