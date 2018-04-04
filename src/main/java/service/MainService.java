package service;

import exception.AppException;
import model.City;
import model.Department;
import model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MainService {

    User register(User user) throws AppException;

    Department addDepartment(Department department) throws AppException;

    City addCity(City city) throws AppException;

    User update(User user) throws AppException;

    User remove(User user) throws AppException;

    Map<Department, List<User>> getUsersGroupByDepartment() throws AppException;

    Map<Department, Integer> getAvgSalaryGroupByDepartment() throws AppException;

    Map<User, List<User>> getUsersGroupByManagersAndOrderedThatLiveInKiev() throws AppException;

    List<User> findByName(String name) throws AppException;

    List<User> findInRange(double minSal, double maxSal) throws AppException;

    List<User> findByDate(LocalDateTime start, LocalDateTime end) throws AppException;

    List<User> findAll();

}