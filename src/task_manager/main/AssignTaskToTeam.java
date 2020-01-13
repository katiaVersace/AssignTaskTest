package task_manager.main;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import task_manager.model.Employee;
import task_manager.model.Task;
import task_manager.model.Team;

public class AssignTaskToTeam {



	public static void testPositivo() {
		System.out.println("Test Positivo: ");
		Team team1 = new Team("Team1");

		Employee luca = new Employee();
		luca.setUserName("Luca");
		Employee katia = new Employee();
		katia.setUserName("Katia");

		team1.getEmployees().add(luca);
		team1.getEmployees().add(katia);

		LocalDate start = LocalDate.parse("2019-12-16");
		LocalDate end = LocalDate.parse("2019-12-23");
		long days_max = ChronoUnit.DAYS.between(start, end) + 1;

		Task task_A = new Task("TASK A", LocalDate.parse("2019-12-18"), null, LocalDate.parse("2019-12-21"), null);
		Task task_B = new Task("TASK B", LocalDate.parse("2019-12-21"), null, LocalDate.parse("2019-12-22"), null);
		Task task_C = new Task("TASK C", LocalDate.parse("2019-12-17"), null, LocalDate.parse("2019-12-19"), null);
		Task task_D = new Task("TASK D", LocalDate.parse("2019-12-17"), null, LocalDate.parse("2019-12-17"), null);
		Task task_E = new Task("TASK E", LocalDate.parse("2019-12-23"), null, LocalDate.parse("2019-12-23"), null);
		Task task_F = new Task("TASK F", LocalDate.parse("2019-12-20"), null, LocalDate.parse("2019-12-20"), null);

		luca.getTasks().add(task_D);
		luca.getTasks().add(task_B);
		luca.getTasks().add(task_F);
		task_D.setEmployee(luca);
		task_B.setEmployee(luca);
		task_F.setEmployee(luca);

		katia.getTasks().add(task_C);
		katia.getTasks().add(task_E);
		task_C.setEmployee(katia);
		task_E.setEmployee(katia);

		System.out.println("Prima");
		printEmployeeScheduling(luca, days_max, start, end);
		printEmployeeScheduling(katia, days_max, start, end);

		System.out.print("Provo ad assegnare TASK_A: ");
		printTaskSchedule(task_A, start, end);

		List<Task> visti = new ArrayList<>();
		Map<Task, Employee> solution = new HashMap<>();
		if (assignTaskToTeam(task_A, team1.getEmployees(), visti, solution)) {
			for (Map.Entry<Task, Employee> entry : solution.entrySet()) {
				System.out.println(
						"Task : " + entry.getKey().getDescription() + " Employee : " + entry.getValue().getUserName());
				Employee oldEmployee = entry.getKey().getEmployee();
				if (oldEmployee != null)
					oldEmployee.getTasks().remove(entry.getKey());
				entry.getValue().getTasks().add(entry.getKey());
				entry.getKey().setEmployee(entry.getValue());
			}
		} else
			System.out.println("Assegnamento fallito");
		printEmployeeScheduling(luca, days_max, start, end);
		printEmployeeScheduling(katia, days_max, start, end);

	}

	public static void test2() {
		System.out.println("Test 2: ");
		Team team1 = new Team("Team1");

		Employee A = new Employee();
		A.setUserName("A");
		Employee B = new Employee();
		B.setUserName("B");
		Employee C = new Employee();
		C.setUserName("C");

		team1.getEmployees().add(B);
		team1.getEmployees().add(C);
		team1.getEmployees().add(A);

		LocalDate start = LocalDate.parse("2020-01-01");
		LocalDate end = LocalDate.parse("2020-01-08");
		long days_max = ChronoUnit.DAYS.between(start, end) + 1;

		Task task_1 = new Task("TASK 1", LocalDate.parse("2020-01-03"), null, LocalDate.parse("2020-01-04"), null);
		Task task_2 = new Task("TASK 2", LocalDate.parse("2020-01-07"), null, LocalDate.parse("2020-01-08"), null);
		Task task_3 = new Task("TASK 3", LocalDate.parse("2020-01-01"), null, LocalDate.parse("2020-01-06"), null);
		Task task_4 = new Task("TASK 4", LocalDate.parse("2020-01-08"), null, LocalDate.parse("2020-01-08"), null);
		Task task_5 = new Task("TASK 5", LocalDate.parse("2020-01-01"), null, LocalDate.parse("2020-01-01"), null);
		Task task_6 = new Task("TASK 6", LocalDate.parse("2020-01-07"), null, LocalDate.parse("2020-01-08"), null);
		Task task_7 = new Task("TASK 7", LocalDate.parse("2020-01-03"), null, LocalDate.parse("2020-01-07"), null);

		A.getTasks().add(task_1);
		A.getTasks().add(task_2);
		task_1.setEmployee(A);
		task_2.setEmployee(A);

		B.getTasks().add(task_3);
		B.getTasks().add(task_4);
		task_3.setEmployee(B);
		task_4.setEmployee(B);

		C.getTasks().add(task_5);
		C.getTasks().add(task_6);
		task_5.setEmployee(C);
		task_6.setEmployee(C);

		System.out.println("Prima");
		printEmployeeScheduling(A, days_max, start, end);
		printEmployeeScheduling(B, days_max, start, end);
		printEmployeeScheduling(C, days_max, start, end);

		System.out.print("Provo ad assegnare TASK_7: ");
		printTaskSchedule(task_7, start, end);

		List<Task> visti = new ArrayList<>();
		Map<Task, Employee> solution = new HashMap<>();
		if (assignTaskToTeam(task_7, team1.getEmployees(), visti, solution)) {
			for (Map.Entry<Task, Employee> entry : solution.entrySet()) {
				System.out.println(
						"Task : " + entry.getKey().getDescription() + " Employee : " + entry.getValue().getUserName());
				Employee oldEmployee = entry.getKey().getEmployee();
				if (oldEmployee != null)
					oldEmployee.getTasks().remove(entry.getKey());
				entry.getValue().getTasks().add(entry.getKey());
				entry.getKey().setEmployee(entry.getValue());
			}
		} else
			System.out.println("Assegnamento fallito");
		printEmployeeScheduling(A, days_max, start, end);
		printEmployeeScheduling(B, days_max, start, end);
		printEmployeeScheduling(C, days_max, start, end);

	}

	public static void test3() {
		System.out.println("Test 3: ");
		Team team1 = new Team("Team1");

		Employee A = new Employee();
		A.setUserName("A");
		Employee B = new Employee();
		B.setUserName("B");

		team1.getEmployees().add(B);
		team1.getEmployees().add(A);

		LocalDate start = LocalDate.parse("2020-01-01");
		LocalDate end = LocalDate.parse("2020-01-04");
		long days_max = ChronoUnit.DAYS.between(start, end) + 1;

		Task task_1 = new Task("TASK 1", LocalDate.parse("2020-01-01"), null, LocalDate.parse("2020-01-02"), null);
		Task task_2 = new Task("TASK 2", LocalDate.parse("2020-01-04"), null, LocalDate.parse("2020-01-04"), null);
		Task task_3 = new Task("TASK 3", LocalDate.parse("2020-01-01"), null, LocalDate.parse("2020-01-01"), null);
		Task task_4 = new Task("TASK 4", LocalDate.parse("2020-01-03"), null, LocalDate.parse("2020-01-04"), null);
		Task task_5 = new Task("TASK 5", LocalDate.parse("2020-01-02"), null, LocalDate.parse("2020-01-03"), null);

		A.getTasks().add(task_1);
		A.getTasks().add(task_2);
		task_1.setEmployee(A);
		task_2.setEmployee(A);

		B.getTasks().add(task_3);
		B.getTasks().add(task_4);
		task_3.setEmployee(B);
		task_4.setEmployee(B);

		System.out.println("Prima");
		printEmployeeScheduling(A, days_max, start, end);
		printEmployeeScheduling(B, days_max, start, end);

		System.out.print("Provo ad assegnare TASK_5: ");
		printTaskSchedule(task_5, start, end);

		List<Task> visti = new ArrayList<>();
		Map<Task, Employee> solution = new HashMap<>();
		if (assignTaskToTeam(task_5, team1.getEmployees(), visti, solution)) {
			for (Map.Entry<Task, Employee> entry : solution.entrySet()) {
				System.out.println(
						"Task : " + entry.getKey().getDescription() + " Employee : " + entry.getValue().getUserName());
				Employee oldEmployee = entry.getKey().getEmployee();
				if (oldEmployee != null)
					oldEmployee.getTasks().remove(entry.getKey());
				entry.getValue().getTasks().add(entry.getKey());
				entry.getKey().setEmployee(entry.getValue());
			}
		} else
			System.out.println("Assegnamento fallito");
		printEmployeeScheduling(A, days_max, start, end);
		printEmployeeScheduling(B, days_max, start, end);

	}

	public static void testNegativo() {
		System.out.println("Test Negativo: ");
		Team team1 = new Team("Team1");
		Employee A = new Employee();
		A.setUserName("A");
		Employee B = new Employee();
		B.setUserName("B");
		Employee C = new Employee();
		C.setUserName("C");
		Employee D = new Employee();
		D.setUserName("D");

		team1.getEmployees().add(A);
		team1.getEmployees().add(B);
		team1.getEmployees().add(C);
		team1.getEmployees().add(D);

		LocalDate start = LocalDate.parse("2019-12-16");
		LocalDate end = LocalDate.parse("2019-12-22");
		long days_max = ChronoUnit.DAYS.between(start, end) + 1;

		Task task_A = new Task("TASK A", LocalDate.parse("2019-12-19"), null, LocalDate.parse("2019-12-22"), null);
		Task task_B = new Task("TASK B", LocalDate.parse("2019-12-16"), null, LocalDate.parse("2019-12-20"), null);
		Task task_C = new Task("TASK C", LocalDate.parse("2019-12-18"), null, LocalDate.parse("2019-12-20"), null);
		Task task_D = new Task("TASK D", LocalDate.parse("2019-12-18"), null, LocalDate.parse("2019-12-18"), null);
		Task task_E = new Task("TASK E", LocalDate.parse("2019-12-20"), null, LocalDate.parse("2019-12-22"), null);
		Task task_F = new Task("TASK F", LocalDate.parse("2019-12-17"), null, LocalDate.parse("2019-12-18"), null);
		Task task_G = new Task("TASK G", LocalDate.parse("2019-12-19"), null, LocalDate.parse("2019-12-20"), null);

		A.getTasks().add(task_B);
		task_B.setEmployee(A);

		B.getTasks().add(task_C);
		task_C.setEmployee(B);

		C.getTasks().add(task_D);
		task_D.setEmployee(C);

		C.getTasks().add(task_E);
		task_E.setEmployee(C);

		D.getTasks().add(task_F);
		task_F.setEmployee(D);

		D.getTasks().add(task_G);
		task_G.setEmployee(D);

		System.out.println("Prima");

		for (Employee e : team1.getEmployees()) {
			printEmployeeScheduling(e, days_max, start, end);
		}

		System.out.print("Provo ad assegnare TASK_A: ");
		printTaskSchedule(task_A, start, end);

		List<Task> visti = new ArrayList<>();
		Map<Task, Employee> solution = new HashMap<>();
		if (assignTaskToTeam(task_A, team1.getEmployees(), visti, solution)) {
			for (Map.Entry<Task, Employee> entry : solution.entrySet()) {
				System.out.println("\nFinal solution: \n" + entry.getKey().getDescription() + " -> "
						+ entry.getValue().getUserName());
				Employee oldEmployee = entry.getKey().getEmployee();
				if (oldEmployee != null)
					oldEmployee.getTasks().remove(entry.getKey());
				entry.getValue().getTasks().add(entry.getKey());
				entry.getKey().setEmployee(entry.getValue());
			}
		} else
			System.out.println("Assegnamento fallito");

		System.out.println("Dopo");

		for (Employee e : team1.getEmployees()) {
			printEmployeeScheduling(e, days_max, start, end);
		}

	}

	public static boolean assignTaskToTeam(Task task, List<Employee> team, List<Task> visti,
			Map<Task, Employee> solution) {

		// caso base negativo
		if (visti.contains(task)) {
			return false;
		}

		// caso base positivo
		for (Employee employee : team) {
			if (employee != task.getEmployee()) {
				if (employeeAvailable(employee, task.getExpectedStartTime(), task.getExpectedEndTime())) {

					solution.put(task, employee);
					
					if(isValid(solution)) {
					return true;
					}
					else {
						solution.remove(task);
					}

				}
			}
		}

		// passo ricorsivo
		for (Employee employee : team) {
			if (employee != task.getEmployee()) {

				if (!visti.contains(task))
					visti.add(task);

				List<Task> tasks_in_period = getTasksInPeriod(employee, task.getExpectedStartTime(),
						task.getExpectedEndTime());

				int result = 1;
				Map<Task, Employee> partialSolution = new HashMap<Task, Employee>(solution);
				
				partialSolution.put(task, employee);
				if (!isValid(partialSolution)) {
				
					continue;
				}
				for (Task task_to_rearrange : tasks_in_period) {
					if (taskInProgress(task_to_rearrange)
							|| !assignTaskToTeam(task_to_rearrange, team, visti, partialSolution)) {

						result = -result;
						break;
					}

				}
				
				if (result > 0 && isValid(partialSolution)) {
					// copy
					for (Map.Entry<Task, Employee> entry : partialSolution.entrySet()) {
						solution.put(entry.getKey(), entry.getValue());
					}

					
					

					return true;
				} else {
					partialSolution.remove(task);
				
				
				}

			}
		}
		return false;
	}

	private static boolean taskInProgress(Task task_to_rearrange) {
		LocalDate today = LocalDate.now();
		if ((task_to_rearrange.getExpectedStartTime().isBefore(today)
				&& task_to_rearrange.getExpectedEndTime().isAfter(today))
				|| task_to_rearrange.getExpectedStartTime().equals(today)
				|| task_to_rearrange.getExpectedEndTime().equals(today))
			return true;

		else
			return false;
	}

	private static List<Task> getTasksInPeriod(Employee employee, LocalDate start, LocalDate end) {
		List<Task> tasks_in_period = new ArrayList<Task>();
		for (Task t : employee.getTasks()) {
			if (betweenTwoDate(start, t.getExpectedStartTime(), t.getExpectedEndTime())
					|| betweenTwoDate(end, t.getExpectedStartTime(), t.getExpectedEndTime())
					|| betweenTwoDate(t.getExpectedStartTime(), start, end)
					|| betweenTwoDate(t.getExpectedEndTime(), start, end)) {
				tasks_in_period.add(t);
			}
		}

		return tasks_in_period;
	}

	public static boolean betweenTwoDate(LocalDate toCheck, LocalDate start, LocalDate end) {
		boolean result = (toCheck.isAfter(start) && toCheck.isBefore(end)) || toCheck.equals(start)
				|| toCheck.equals(end);
		return result;
	}

	public static boolean employeeAvailable(Employee e, LocalDate startTask, LocalDate endTask) {
		for (Task t : e.getTasks()) {
			if (betweenTwoDate(startTask, t.getExpectedStartTime(), t.getExpectedEndTime())
					|| betweenTwoDate(endTask, t.getExpectedStartTime(), t.getExpectedEndTime())
					|| betweenTwoDate(t.getExpectedStartTime(), startTask, endTask)
					|| betweenTwoDate(t.getExpectedEndTime(), startTask, endTask)) {

				return false;

			}

		}

		return true;
	}

	private static void printEmployeeScheduling(Employee employee, long schedule_size, LocalDate start, LocalDate end) {

		Boolean[] schedule = new Boolean[(int) schedule_size];
		Arrays.fill(schedule, Boolean.FALSE);

		for (Task task : employee.getTasks()) {
			LocalDate taskStartDate = task.getExpectedStartTime();
			LocalDate taskEndDate = task.getExpectedEndTime();

			// check on task start and end because a task can end over the end of the period
			// specified or start before
			if (taskStartDate.isBefore(start)) {
				taskStartDate = start;
			}
			if (taskEndDate.isAfter(end)) {
				taskEndDate = end;
			}

			long diff1 = ChronoUnit.DAYS.between(start, taskStartDate);
			long task_duration = ChronoUnit.DAYS.between(taskStartDate, taskEndDate) + 1;

			for (int i = (int) diff1; i < diff1 + task_duration; i++) {
				schedule[i] = true;
			}
		}
		String result = "";

		for (Boolean value : schedule) {
			if (value.booleanValue() == true) {
				result = result.concat("X ");
			} else {
				result = result.concat("_ ");
			}
		}
		result = result.concat(employee.getUserName());

		System.out.println(result);

	}

	public static void printTaskSchedule(Task task, LocalDate start, LocalDate end) {

		long diff1 = ChronoUnit.DAYS.between(start, task.getExpectedStartTime());
		long task_duration = ChronoUnit.DAYS.between(task.getExpectedStartTime(), task.getExpectedEndTime()) + 1;
		long diff2 = ChronoUnit.DAYS.between(task.getExpectedEndTime(), end);

		for (int i = 0; i < diff1; i++) {
			System.out.print("_ ");
		}

		for (int i = 0; i < task_duration; i++) {
			System.out.print("X ");
		}

		for (int i = 0; i < diff2; i++) {
			System.out.print("_ ");
		}

		System.out.println();

	}

	public static boolean inConflict(Task t1, Task t2) {
		
		if (betweenTwoDate(t1.getExpectedStartTime(), t2.getExpectedStartTime(), t2.getExpectedEndTime())
				|| betweenTwoDate(t1.getExpectedEndTime(), t2.getExpectedStartTime(), t2.getExpectedEndTime())
				|| betweenTwoDate(t2.getExpectedStartTime(), t1.getExpectedStartTime(), t1.getExpectedEndTime())
				|| betweenTwoDate(t2.getExpectedEndTime(), t1.getExpectedStartTime(), t1.getExpectedEndTime())) {
			
			return true;

		} else {

		
			return false;

		}

	}

	public static boolean isValid(Map<Task, Employee> solution) {
		for (Map.Entry<Task, Employee> firstEntry : solution.entrySet()) {
			for (Map.Entry<Task, Employee> secondEntry : solution.entrySet()) {

				if (firstEntry.getKey() != secondEntry.getKey()
						&& firstEntry.getValue().getId() == secondEntry.getValue().getId()
						&& inConflict(firstEntry.getKey(), secondEntry.getKey())) {
				
					return false;
				}
			}

		}
		return true;
	}
	
	public static void orderByTasksNumberInPeriod(List<Employee> employees, LocalDate start, LocalDate end) {
		
		Collections.sort(employees, new Comparator<Employee>(){
		     public int compare(Employee e1, Employee e2){
		         if(getTasksInPeriod(e1, start, end).size() == getTasksInPeriod(e2, start, end).size())
		             return 0;
		         return getTasksInPeriod(e1, start, end).size() < getTasksInPeriod(e2, start, end).size() ? -1 : 1;
		     }
		});
	}
}