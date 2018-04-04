package dao;

import model.City;
import model.Department;
import model.User;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

//

public class DaoImpl<T, ID> implements Dao<T, ID> {

    private EntityManager entityManager;
    private Class<T> persistentClass;

    public DaoImpl() {
    }

    public DaoImpl(Class<T> persistentClass, EntityManager entityManager) {
        this.persistentClass = persistentClass;
        this.entityManager = entityManager;
    }

    @Override
    public List<T> findAll() {
        List<T> result = entityManager.createQuery("SELECT t FROM "
                + persistentClass.getSimpleName() + " t")
                .getResultList();
        return result;
    }

    @Override
    public List<T> findAll(int offset, int length) {

        Query query = entityManager.createQuery("SELECT t FROM " + persistentClass.getSimpleName()
                + " t WHERE ROWNUM() BETWEEN = :offset AND = :length");

        query.setParameter(1, offset);
        query.setParameter(2, length);

        List<T> result = query.getResultList();
        return result;
    }

    @Override
    public T find(ID id) {

        T foundEntity;
        try {
            foundEntity = entityManager.find(persistentClass, id);
            if (foundEntity == null) {
                System.out.println("Client not found!");
                return null;
            }
        } catch (Exception e) {
            e.getMessage();
            return null;
        }

        return foundEntity;
    }

    @Override
    public T remove(ID id) {

        T foundEntity = find(id);

        entityManager.getTransaction().begin();
        try {
            entityManager.remove(foundEntity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            return null;
        }

        return foundEntity;
    }

    @Override
    public T update(T entity) {

        T foundEntity;

        if (User.class == entity.getClass()) {
            User user = (User) entity;
            foundEntity = entityManager.find(persistentClass, user.getId());
            if (!find(entity, foundEntity)) return null;

        } else if (Department.class == entity.getClass()) {
            Department department = (Department) entity;
            foundEntity = entityManager.find(persistentClass, department.getId());
            if (!find(entity, foundEntity)) return null;

        } else if (City.class == entity.getClass()) {
            City city = (City) entity;
            foundEntity = entityManager.find(persistentClass, city.getId());
            if (!find(entity, foundEntity)) return null;

        }
        return entity;
    }
    @Override
    public T create(T entity) {

        entityManager.getTransaction().begin();
        try {
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            e.printStackTrace();
            return null;
        }
        return entity;
    }

    private boolean find(T entity, T foundEntity) {
        if (foundEntity == null) {
            System.out.println("Entity not found!");
            return false;
        }

        entityManager.getTransaction().begin();
        try {
            foundEntity = entity;
            entityManager.persist(foundEntity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            e.getMessage();
            return false;
        }
        return true;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public Class<T> getPersistentClass() {
        return persistentClass;
    }

}