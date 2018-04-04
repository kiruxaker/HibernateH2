package Manager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

//

public class EntityManagerSingleton {

    private static final EntityManagerSingleton ENTITY_MANAGER_SINGLETON = new EntityManagerSingleton();
    private static EntityManagerFactory entityManagerFactory;
    private static EntityManager entityManager;

    private EntityManagerSingleton() {}

    public static EntityManagerSingleton getEntityManagerSingleton() {
        entityManagerFactory = Persistence.createEntityManagerFactory("hibernate-h2-unit");
        entityManager = entityManagerFactory.createEntityManager();
        return ENTITY_MANAGER_SINGLETON;
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public static EntityManager getEntityManager() {
        return entityManager;
    }
}