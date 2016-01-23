package ru.bsuirhelper.android.app.api.entities;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

import ru.bsuirhelper.android.app.api.entities.Employee;

/**
 * Created by Grishechko on 23.01.2016.
 */
@Root(name = "employeeXmlModels")
public class EmployeeList {
    @ElementList(inline = true)
    private List<Employee> employees;

    public List<Employee> getEmployees() {
        return employees;
    }
}
