package task_manager.model;

import java.util.ArrayList;
import java.util.List;

public class Team {

	private int id;

	private String name;

	private List<Employee> employees;

	private int version;

	public Team() {
		super();
	}

	public Team(String name) {
		super();
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Employee> getEmployees() {
		if (employees == null)
			employees = new ArrayList<Employee>();
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Team [id=" + id + ", name=" + name + ", employees=" + employees + ", version=" + version + "]";
	}

}
