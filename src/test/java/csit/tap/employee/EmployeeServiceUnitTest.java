package csit.tap.employee;

import csit.tap.employee.entities.Employee;
import csit.tap.employee.exception.InvalidDataEntry;
import csit.tap.employee.mocks.EmployeeRepositoryMock;
import csit.tap.employee.services.EmployeeService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnit4.class)
public class EmployeeServiceUnitTest {

    private EmployeeRepositoryMock employeeRepository = new EmployeeRepositoryMock();

    private EmployeeService employeeService = new EmployeeService(employeeRepository);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        Employee newEmployee = new Employee("Alex", "ITA");
    }

    @Test
    public void whenSaveEmployee_givenEmployee_shouldReturnEmployee(){
        //arrange
        Employee employeeToSave = new Employee("Mary", "CST", LocalDateTime.now());

        //act
        Employee newEmployee = employeeService.createEmployee(employeeToSave);

        //assert
        assertThat(newEmployee).isNotNull().isEqualTo(employeeToSave);
    }

    @Test
    public void whenSaveEmployee_givenEmployee_shouldSaveEmployee(){
        //arrange
        Employee employeeToSave = new Employee("Mary", "CST", LocalDateTime.now());

        //act
        Employee newEmployee = employeeService.createEmployee(employeeToSave);

        //assert
//        verify(employeeRepository, times(1)).save(any());
        assertThat(employeeRepository.verify("save", 1)).isTrue();
        assertThat(newEmployee).isNotNull().isEqualTo(employeeToSave);
    }

    @Test
    public void whenFindAllEmployees_ShouldReturnAllEmployees() throws Exception
    {

        //arrange
        List<Employee> employeeList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Employee employee = new Employee("Mary " + i, "Department" + i);
            employeeList.add(employee);
        }

        employeeRepository.setEmployeeList(employeeList);

        //act
        Page<Employee> employeePage = employeeService.findAll(0, 5, "id");
        assertThat(employeePage.getContent()).usingRecursiveFieldByFieldElementComparator().isEqualTo(employeeList.subList(0, 5));

        //assert
        assertThat(employeePage.getTotalElements()).isEqualTo(5);

    }

    @Test
    public void whenUpdateEmployee_givenEmptyName_shouldThrowInvalidDataException() {
        //arrange
        Employee existingEmployee = new Employee("Melvir", "Depart M");
        employeeRepository.setEmployeeList(Arrays.asList(existingEmployee));

        existingEmployee.setName("");

        //assert
        thrown.expect(InvalidDataEntry.class);
        thrown.expectMessage( "All data entry cannot be empty ");

        //act
        employeeService.updateEmployee(existingEmployee);

    }

    @Test
    public void whenGetEmployee_givenDepartment_shouldReturnEmployee() {

        //arrange
        List<Employee> employeeList = new ArrayList<>();
        Employee employee = new Employee("John", "ITA");
        employeeList.add(employee);

        Pageable paging = PageRequest.of(0, 10, Sort.by("id"));
        Page<Employee> employeePage = new PageImpl<>(employeeList);

        employeeRepository.setEmployeePage(employeePage);

        //act
        Page<Employee> resultEmployee = employeeService.findEmployeeByDepartment(0, 10, "id", "ITA");

        //assert
        assertThat(resultEmployee.getContent()).contains(employee);
    }

}
