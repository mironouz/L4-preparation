import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    private static final Logger LOG  = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ApplicationContext ctx = new
                ClassPathXmlApplicationContext( "beans.xml" );

        ctx.getBean("bookingFacade");

        LOG.info("Context loaded successfully.");
    }
}