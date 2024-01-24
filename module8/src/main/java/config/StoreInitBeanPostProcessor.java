package config;

import com.google.common.base.Preconditions;
import dao.InMemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class StoreInitBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOG  = LoggerFactory.getLogger(StoreInitBeanPostProcessor.class);

    private final String beanName;

    private final String path;

    public StoreInitBeanPostProcessor(String beanName, String path) {
        this.beanName = Preconditions.checkNotNull(beanName, "Name of store to initialize cannot be null");
        this.path = Preconditions.checkNotNull(path, "Path to file from what to initialize from cannot be null");
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (this.beanName.equalsIgnoreCase(beanName)) {
            LOG.info("Store bean will be initialized from {}.", path);

            if (bean instanceof InMemoryStore store) {
                StoreData sti = StoreData.fromPath(path);
                store.init(sti.load());
            }
        }

        return bean;
    }
}