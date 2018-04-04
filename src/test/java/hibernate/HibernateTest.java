package hibernate;

import Manager.EntityManagerSingleton;
import dao.DaoImpl;
import exception.AppException;
import model.Base;
import model.City;
import model.Department;
import model.User;
import service.MainService;
import service.MainServiceImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class HibernateTest {

    private static MainService mainService;

    @BeforeClass
    public static void initDB(){
        EntityManagerSingleton.getEntityManagerSingleton();
        mainService = new MainServiceImpl(
                new DaoImpl<>(City.class, EntityManagerSingleton.getEntityManager()),
                new DaoImpl<>(Department.class, EntityManagerSingleton.getEntityManager()),
                new DaoImpl<>(User.class, EntityManagerSingleton.getEntityManager()));

        try {
            City city1 = new City("Kyiv");
            City city2 = new City("Lviv");
            City city3 = new City("Ivano-Frankivsk");

            Department department1 = new Department("IT");
            Department department2 = new Department("QA");

            mainService.addCity(city1);
            mainService.addCity(city2);
            mainService.addCity(city3);
            mainService.addDepartment(department1);
            mainService.addDepartment(department2);

            User user = new User("Nazar", 34, 2000, department1, city3, null, LocalDateTime.of(2005,07,11,0,0,0));
            mainService.register(user);
            User user2 = new User("Kolya", 35, 3000.00, department2, city1, user, LocalDateTime.of(2005,10,1,0,0,0));
            User user3 = new User("Olya", 51, 2500.00, department1, city1, user2, LocalDateTime.of(1991, 1,6,0,0,0));
            User user4 = new User("Andy", 40, 2200.00, department2, city2, user, LocalDateTime.of(2001, 1,18,0,0,0));
            User user5 = new User("Roma", 33, 1500.00, department1, city2, user2, LocalDateTime.of(2006, 2,15,0,0,0));
            User user6 = new User("Igor", 42, 2800.00, department2, city1, user2, LocalDateTime.of(1998,9,22,0,0,0));
            User user7 = new User("Andy", 22, 1000.00, department2, city3, user, LocalDateTime.of(2010,8,24,0,0,0));

            mainService.register(user2);
            mainService.register(user3);
            mainService.register(user4);
            mainService.register(user5);
            mainService.register(user6);
            mainService.register(user7);

        } catch (AppException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void register() throws AppException {
        User user = new User("Vasyl",34,2500, new Department("MachineLearning"), new City("Rivne"), null, LocalDateTime.now());
        User actual = mainService.register(user);
        assertThat(actual.getName(), equalTo("Vasyl"));
        assertThat(actual.getDepartment().getName(), equalTo("MachineLearning"));
        assertThat(actual.getCity().getName(), equalTo("Rivne"));
    }

    @Test
    public void addDepartment() throws AppException {
        Department department = new Department("MachineLearning");
        Department actual = mainService.addDepartment(department);
        assertThat(actual.getName(), equalTo("MachineLearning"));
    }

    @Test
    public void addCity() throws AppException {
        City city = new City("Lutsk");
        City  actual = mainService.addCity(city);
        assertThat(actual.getName(), equalTo("Lutsk"));
    }

    @Test
    public void update() throws AppException {

        User updatedUser = mainService.findByName("Nazar").get(0);

        updatedUser.setSalary(3000);
        updatedUser.setAge(35);

        User actual = mainService.update(updatedUser);

        assertThat(actual.getAge(), equalTo(35));
        assertThat(actual.getSalary(), equalTo(3000.0));
    }

    @Test
    public void remove() throws AppException {

        mainService.findAll().forEach(System.out::println);

        User deletedUser = mainService.findByName("Igor").get(0);
        User actual = mainService.remove(deletedUser);

        assertThat(actual.getName(), equalTo("Igor"));
        assertThat(actual.getAge(), equalTo(42));
        assertThat(actual.getSalary(), equalTo(2800.0));
    }

    @Test
    public void getUsersGroupByDepartment() throws AppException {
        Map<Department,List<User>> usersGroupByDepartment = mainService.getUsersGroupByDepartment();
        assertThat(usersGroupByDepartment.keySet().size(), equalTo(2));
    }

    @Test
    public void getAvgSalaryGroupByDepartment() throws AppException {
        Map<Department, Integer> getAvgSalaryGroupByDepartment = mainService.getAvgSalaryGroupByDepartment();
        assertThat(getAvgSalaryGroupByDepartment.keySet().size(), equalTo(2));
    }

    @Test
    public void getUsersGroupByManagersAndOrderedThatLiveInKiev() throws AppException {
        Map<User,List<User>> getUsersGroupByManagersAndOrderedThatLiveInKiev = mainService.getUsersGroupByManagersAndOrderedThatLiveInKiev();
        User actual = mainService.findByName("Kolya").get(0);
        assertThat(getUsersGroupByManagersAndOrderedThatLiveInKiev.get(actual).size(), equalTo(2));
    }

    @Test
    public void findByName() throws AppException {
        List<User> users = mainService.findByName("Andy");
        User user = users.get(1);
        assertThat(user.getAge(), equalTo(22));
        user = users.get(0);
        assertThat(user.getAge(), equalTo(40));
    }

    @Test
    public void findInRange() throws AppException {
        List<User> users = mainService.findInRange(2000.0,3000.0);
        assertThat(users.size(), equalTo(5));
        assertThat(users.stream()
                .map(User::getSalary)
                .reduce(0.0, Double::sum), equalTo(12500.0));
    }

    @Test
    public void findByDate() throws AppException {
        List<User> users = mainService.findByDate(LocalDateTime.of(2000,1,1,0,0,0),
                LocalDateTime.of(2011,1,1,0,0,0));
        assertThat(users.size(), equalTo(5));
        assertThat(users.stream()
                .min(Comparator.comparing(User::getLocalDateTime))
                .get()
                .getLocalDateTime(), equalTo(LocalDateTime.of(2001, 1,18,0,0,0)));
        assertThat(users.stream()
                .max(Comparator.comparing(User::getLocalDateTime))
                .get()
                .getLocalDateTime(), equalTo(LocalDateTime.of(2010,8,24,0,0,0)));
    }

    @Test
    public void findAll() throws AppException {
        List<User> users = mainService.findAll();
        assertThat(users.size(), equalTo(6));
        assertThat(users.stream()
                .mapToInt(User::getAge)
                .average()
                .getAsDouble(), equalTo(35.833333333333336));
    }

    @Test
    public void testException() {
        assertThat(new AppException("message") instanceof Exception, equalTo(true));
    }

    @Test
    public void testBase() {
        Base base = new Base(1, "Kirill");

        Base base1 = new Base();
        base1.setId(1);
        base1.setName("Kirill");

        assertThat(base.toString(), equalTo(base1.toString()));
    }

    @Test
    public void testUser() {
        User user = new User(1, "Kirill", 10, 1000.00,
                new Department(1, "Department"),
                new City(1, "Kiev"),
                new User(1, "Manager", 12));

        User user1 = new User();

        user1.setId(1);
        user1.setName("Kirill");
        user1.setAge(10);

        user1.setSalary(1000.00);

        user1.setDepartment(new Department(1, "Department"));
        user1.setCity(new City(1, "Kiev"));

        user1.setManage(new User(1, "Manager", 12));

        assertThat(user.toString(), equalTo(user1.toString()));

        assertThat(user.getName(), equalTo(user1.getName()));
        assertThat(user.getAge(), equalTo(user1.getAge()));
        assertThat(user.getId(), equalTo(user1.getId()));

        assertThat(user.getDepartment().toString(), equalTo(user1.getDepartment().toString()));
        assertThat(user.getCity().toString(), equalTo(user1.getCity().toString()));

        assertThat(user.getSalary(), equalTo(user1.getSalary()));

        assertThat(user.getManage().toString(), equalTo(user1.getManage().toString()));
    }

    @AfterClass
    public static void closeDB(){
        EntityManagerSingleton.getEntityManager().close();
        EntityManagerSingleton.getEntityManagerFactory().close();
    }
}