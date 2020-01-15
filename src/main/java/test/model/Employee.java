package test.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Employee implements Serializable{

	private int id;

	private String userName;

	private String password;

	private String firstName;

	private String lastName;

	private String email;

	private boolean topEmployee;

	private List<Role> roles;

	private List<Task> tasks;

	private int version;

	public Employee() {
		roles = new ArrayList<Role>();
		tasks = new ArrayList<Task>();
	}

	public Employee(String userName, String password, String firstName, String lastName, String email,
			boolean topEmployee) {
		this.userName = userName;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.topEmployee = topEmployee;
		roles = new ArrayList<Role>();
		tasks = new ArrayList<Task>();
	}

	public Employee(String userName, String password, String firstName, String lastName, String email,
			boolean topEmployee, List<Role> roles) {
		this.userName = userName;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.topEmployee = topEmployee;
		this.roles = roles;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isTopEmployee() {
		return topEmployee;
	}

	public void setTopEmployee(boolean topEmployee) {
		this.topEmployee = topEmployee;
	}

	public Collection<Role> getRoles() {
		if (roles == null)
			roles = new ArrayList<Role>();
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", userName=" + userName + ", password=" + password + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", email=" + email + ", topEmployee=" + topEmployee + ", roles=" + roles
				+ ", version=" + version + "]";
	}

	

}
