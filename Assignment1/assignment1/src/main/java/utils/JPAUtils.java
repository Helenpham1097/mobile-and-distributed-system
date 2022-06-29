package utils;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class JPAUtils {

    public static <T> void execute(EntityManager entityManager, List<Consumer<T>> actions, T entity) {
        try {
            entityManager.getTransaction().begin();
            actions.forEach(action -> action.accept(entity));
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        }
    }

    public static <T> void execute(EntityManager entityManager, Consumer<T> action, T entity) {
        execute(entityManager, Collections.singletonList(action), entity);
    }
}
