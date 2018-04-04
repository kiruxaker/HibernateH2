package model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Departments")
public class Department extends Base {

    public Department() {
    }

    public Department(String name) {
        super(name);
    }


    public Department(int id, String name) {
        super(id, name);
    }

    @Override
    public Department clone() {
        return new Department(id, name);
    }
}