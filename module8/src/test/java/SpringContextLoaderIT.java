import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration("/beans.xml")
public class SpringContextLoaderIT {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Testing Spring Context load")
    void testContextLoading() {
        assertThat(applicationContext.getBean("bookingFacade")).isNotNull();
    }
}
