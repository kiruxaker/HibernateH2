package service;

import Manager.EntityManagerSingleton;
import dao.Dao;
import dao.DaoImpl;
import exception.AppException;
import model.City;
import model.Department;
import model.User;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainServiceImpl extends DaoImpl implements MainService {

    private Dao<City, Integer> cityDao;
    private Dao<Department, Integer> departmentDao;
    private Dao<User, Integer> userdao;

    public MainServiceImpl(Dao<City, Integer> cityDao,
                           Dao<Department, Integer> departmentDao,
                           Dao<User, Integer> userdao) {

        this.cityDao = cityDao;
        this.departmentDao = departmentDao;
        this.userdao = userdao;
    }

    @Override
    public User register(User user) throws AppException {
        userdao.create(user);
        return user;
    }

    @Override
    public Department addDepartment(Department department) throws AppException {
        departmentDao.create(department);
        return department;
    }

    @Override
    public City addCity(City city) throws AppException {
        cityDao.create(city);
        return city;
    }

    @Override
    public User update(User user) throws AppException {
        userdao.update(user);
        return user;
    }

    @Override
    public User remove(User user) throws AppException {
        userdao.remove(user.getId());
        return user;
    }

    @Override
    public Map<Department, List<User>> getUsersGroupByDepartment() throws AppException {

        TypedQuery<User> query = EntityManagerSingleton.getEntityManager().createQuery("SELECT u FROM User u", User.class);
        List<User> users = query.getResultList();

        return users.stream().collect(Collectors.groupingBy(User::getDepartment));
    }

    @Override
    public Map<Department, Integer> getAvgSalaryGroupByDepartment() throws AppException {

        Map<Department, Integer> getAvgSalaryGroupByDepartment = new HashMap<>();

        TypedQuery<Object[]> query = EntityManagerSingleton.getEntityManager().createQuery("SELECT u.department, avg(u.salary) AS sal FROM User u GROUP BY u.department", Object[].class);

        List<Object[]> results = query.getResultList();

        for (Object[] o : results) {
            getAvgSalaryGroupByDepartment.put((Department) o[0], ((Number) o[1]).intValue());
        }

        return getAvgSalaryGroupByDepartment;
    }

    @Override
    public Map<User, List<User>> getUsersGroupByManagersAndOrderedThatLiveInKiev() throws AppException {

        TypedQuery<User> query = EntityManagerSingleton.getEntityManager().createQuery("SELECT u FROM User u WHERE u.city.name = 'Kyiv' ", User.class);
        List<User> users = query.getResultList();

        return users.stream()
                .filter(u -> u.getManage() != null)
                .collect(Collectors.groupingBy(User::getManage));

    }

    @Override
    public List<User> findByName(String name) throws AppException {

        List<User> users = userdao.findAll();
        List<User> userByName = new ArrayList<>();

        for (User user : users) {

            if (user.getName().equals(name)) {
                userByName.add(user);
            }

        }
        return userByName;
    }

    @Override
    public List<User> findInRange(double minSal, double maxSal) throws AppException {

        List<User> users = userdao.findAll();
        List<User> userBySalary = new ArrayList<>();

        for (User user : users) {

            if (user.getSalary() >= minSal && user.getSalary() <= maxSal) {
                userBySalary.add(user);
            }

        }
        return userBySalary;
    }

    @Override
    public List<User> findByDate(LocalDateTime start, LocalDateTime end) throws AppException {
        List<User> users = userdao.findAll();
        List<User> userByDate = new ArrayList<>();

        for (User user : users) {

            if (user.getLocalDateTime().isAfter(start) && user.getLocalDateTime().isBefore(end)) {
                userByDate.add(user);
            }

        }
        return userByDate;
    }

    @Override
    public List<User> findAll() {
        return userdao.findAll();
    }

}