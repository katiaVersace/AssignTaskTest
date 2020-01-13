package task_manager.main;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import task_manager.model.Employee;
import task_manager.model.Task;
import task_manager.model.Team;

public class PopulateDb {

	public static String outputSeparator = "  ";

	public static void main(String[] args) {

		// input
		LocalDate start = LocalDate.parse("2019-12-01");
		LocalDate end = LocalDate.parse("2019-12-15");

		int teams_size = 1, employees_size = 10, tasks_size = 20, task_max_duration = 6, nRandomChars = 2;

		// Check input
		long days_max = ChronoUnit.DAYS.between(start, end) + 1;
		if (teams_size > (employees_size / 2) || tasks_size < employees_size || task_max_duration > days_max) {
			System.out.println("invalid input");
			return;
		}

		// create Employees
		List<Employee> employees = new ArrayList<Employee>();
		for (int i = 0; i < employees_size; i++) {

			String generatedString = generateRandom(nRandomChars);
			Employee employee = new Employee("EMP_" + generatedString, generatedString, "Name_" + generatedString,
					"LastName_" + generatedString, generatedString + "@alten.it", false);
			employee.setId(i + 1);
			employees.add(employee);

		}

		// create Teams
		List<Team> teams = new ArrayList<Team>();
		for (int i = 0; i < teams_size; i++) {

			String generatedString = generateRandom(nRandomChars);
			Team team = new Team("TEAM_" + generatedString);
			team.setId(i + 1);
			teams.add(team);

		}

		// assign team to employees
		int team_index = 0;
		for (Employee employee : employees) {
			teams.get(team_index).getEmployees().add(employee);
			team_index++;
			if (team_index == teams_size)
				team_index = 0;
		}

		// create Tasks
		Random rn = new Random();

		List<Task> tasks = new ArrayList<Task>();
		for (int i = 0; i < tasks_size; i++) {
			int duration = rn.nextInt(task_max_duration) + 1;

			String generatedString = toString(i + 1,36);
			if(generatedString.length()<2) generatedString = "0"+generatedString;
			LocalDate endMinusDuration = end.plusDays(-duration);
			LocalDate expectedStartTime = between(start, endMinusDuration);
			LocalDate expected_end_time = expectedStartTime.plusDays(duration - 1);
			Task task = new Task(generatedString, expectedStartTime, null, expected_end_time, null);
			task.setId(i + 1);
			tasks.add(task);

		}

		// assign at least 1 task for each employee
		int task_index = 0;
		for (Employee employee : employees) {
			tasks.get(task_index).setEmployee(employee);
			employee.getTasks().add(tasks.get(task_index));
			task_index++;
		}

		// assign remaining task
		List<Task> remainingTasks = new ArrayList<Task>(tasks.subList(task_index, tasks.size()));

		boolean availability = true;
		while (remainingTasks.size() != 0 && availability) {
			if (assignTask(remainingTasks.get(0), employees, start, end)) {
				remainingTasks.remove(0);
			} else {
				availability = false;
			
			}

		}
		
		printOutput(start, end, tasks);
		System.out.println();
		
		System.out.println("\nAvailability: ");
		for(int i = 0; i<days_max;i++ ) {
			LocalDate currentDay = start.plusDays(i);
			String day = String.valueOf(currentDay.getDayOfMonth());
			if(currentDay.getDayOfMonth()<10) {
				day = "0"+day;
			}
			System.out.print(day+outputSeparator);
			}
			System.out.println();
		
	
		for (Employee employee : employees) {
			printEmployeeScheduling(employee, days_max, start, end);

		}
		
		printFreeEmployee(start, end, days_max, teams.get(0).getEmployees());

		Scanner reader = new Scanner(System.in); // Reading from System.in
		System.out.println("Enter a title for the task: ");
		String task_title = reader.nextLine();
		System.out.println("Enter a day of start: ");
		int day_start = reader.nextInt(); 
		System.out.println("Enter a day for end: ");
		int day_end = reader.nextInt();

		while (!task_title.equals(".")) {

			String day_start_string = String.valueOf(day_start);
			if (day_start < 10) {
				day_start_string = "0" + String.valueOf(day_start);
			}

			String day_end_string = String.valueOf(day_end);
			if (day_end < 10) {
				day_end_string = "0" + String.valueOf(day_end);
			}
			Task task_A = new Task(task_title, LocalDate.parse("2019-12-" + day_start_string), null,
					LocalDate.parse("2019-12-" + day_end_string), null);
			
			task_A.setId(tasks.size());
			task_A.setDescription(toString(tasks.size(), 36));
			System.out.println("Sto per aggiungere il task: "+task_A.getDescription());
			tasks.add(task_A);
			
			//order
			AssignTaskToTeam.orderByTasksNumberInPeriod(teams.get(0).getEmployees(), task_A.getExpectedStartTime(), task_A.getExpectedEndTime());

			List<Task> visti = new ArrayList<>();
			Map<Task, Employee> solution = new HashMap<>();
			if (AssignTaskToTeam.assignTaskToTeam(task_A, teams.get(0).getEmployees(), visti, solution)) {
				
				System.out.println("\nFinal solution:");
				for (Map.Entry<Task, Employee> entry : solution.entrySet()) {
					System.out.println(entry.getKey().getDescription() + " -> " + entry.getValue().getUserName());
					Employee oldEmployee = entry.getKey().getEmployee();
					if (oldEmployee != null)
						oldEmployee.getTasks().remove(entry.getKey());
					entry.getValue().getTasks().add(entry.getKey());
					entry.getKey().setEmployee(entry.getValue());
				}
			} else
				System.out.println("Assegnamento fallito");

			System.out.println("Dopo");

			for(int i = 0; i<days_max;i++ ) {
			LocalDate currentDay = start.plusDays(i);
			String day = String.valueOf(currentDay.getDayOfMonth());
			if(currentDay.getDayOfMonth()<10) {
				day = "0"+day;
			}
			System.out.print(day+outputSeparator);
			}
			System.out.println();
			
			for (Employee e : teams.get(0).getEmployees()) {
				printEmployeeScheduling(e, days_max, start, end);
			}

			printFreeEmployee(start, end, days_max, teams.get(0).getEmployees());

			reader.nextLine(); 
			System.out.println("Enter a title for the task: ");
			task_title = reader.nextLine();
			System.out.println("Enter a day of start: ");
			day_start = reader.nextInt(); // Scans the next token of the input as an int.
			System.out.println("Enter a day for end: ");
			day_end = reader.nextInt();
		}
		// once finished
		reader.close();
	}

	public static void printFreeEmployee(LocalDate start, LocalDate end, long schedule_size, List<Employee> employees) {
		Integer[] freeEmployees = new Integer[(int) schedule_size];
		Arrays.fill(freeEmployees, 0);

		LocalDate currentDay = start;
		int currentIndexDay = 0;
		while (currentDay.compareTo(end) <= 0) {

			for (Employee employee : employees) {
				if (employeeAvailable(employee, currentDay, currentDay)) {
					freeEmployees[currentIndexDay]++;
				}

			}
			currentDay = currentDay.plusDays(1);
			currentIndexDay++;
		}

		for (int i = 0; i < freeEmployees.length; i++) {
			if(freeEmployees[i]<10) {
				System.out.print("0"+ freeEmployees[i] + "  ");
			}
			else System.out.print(freeEmployees[i] + "  ");
			
		}
		System.out.println("Free employees");

	}

	private static void printEmployeeScheduling(Employee employee, long schedule_size, LocalDate start, LocalDate end) {

		String[] schedule = new String[(int) schedule_size];
		Arrays.fill(schedule, "__");

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

			

			String stringToPrint = toString(task.getId(),36) ;
			if(stringToPrint.length()<2) stringToPrint = "0"+stringToPrint;

			for (int i = (int) diff1; i < diff1 + task_duration; i++) {
				schedule[i] = stringToPrint;
			}
		}

		for (String c : schedule)
			System.out.print(c + outputSeparator);
		System.out.println(employee.getUserName());

	}

	

	public static void printOutput(LocalDate start, LocalDate end, List<Task> tasks) {
		for (Task t : tasks) {
			if (t.getEmployee() != null) {
				
				long diff1 = ChronoUnit.DAYS.between(start, t.getExpectedStartTime());
				long task_duration = ChronoUnit.DAYS.between(t.getExpectedStartTime(), t.getExpectedEndTime()) + 1;
				long diff2 = ChronoUnit.DAYS.between(t.getExpectedEndTime(), end);

				for (int i = 0; i < diff1; i++) {
					System.out.print("_"+outputSeparator);
				}

				for (int i = 0; i < task_duration; i++) {
					System.out.print("X"+outputSeparator);
				}

				for (int i = 0; i < diff2; i++) {
					System.out.print("_"+outputSeparator);
				}

				System.out.println(" "+t.getEmployee().getUserName() + ", " + t.getDescription() + ", Task ID: " + t.getId()+ ", "+ t.getExpectedStartTime().getDayOfMonth()+"-"+t.getExpectedEndTime().getDayOfMonth());
			}
		}

	}

	public static boolean assignTask(Task task, List<Employee> employees, LocalDate start, LocalDate end) {
		// per non assegnare tutti i task al primo impiegato
		Collections.shuffle(employees);

		for (Employee employee : employees) {
			if (employeeAvailable(employee, task.getExpectedStartTime(), task.getExpectedEndTime())) {
				task.setEmployee(employee);
				employee.getTasks().add(task);
				return true;
			}
		}

		// Non sono riuscito ad assegnarlo quindi devo modificare la data del task
		for (Employee employee : employees) {
			LocalDate availability = getAvailability(employee, start, end);
			if (availability != null) {
				
				task.setExpectedStartTime(availability);
				task.setExpectedEndTime(availability);

				task.setEmployee(employee);
				employee.getTasks().add(task);
				return true;
			}
		}

		// Nessun impiegato ha una disponibilità 
		return false;
	}

	public static LocalDate getAvailability(Employee employee, LocalDate start, LocalDate end) {
		LocalDate currentDay = start;
		while (currentDay.compareTo(end) <= 0) {
			if (employeeAvailable(employee, currentDay, currentDay)) {
				return currentDay;
			}

			currentDay = currentDay.plusDays(1);
		}

		return null;
	}

	public static LocalDate between(LocalDate startInclusive, LocalDate endInclusive) {
		Random rn = new Random();

		long days_max = (ChronoUnit.DAYS.between(startInclusive, endInclusive)) + 1;
		int days = rn.nextInt((int) days_max);

		LocalDate randomDate = startInclusive.plusDays(days);

		return randomDate;
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

	public static String generateRandom(int length) {
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		Random random = new SecureRandom();
		if (length <= 0) {
			throw new IllegalArgumentException("String length must be a positive integer");
		}

		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(characters.charAt(random.nextInt(characters.length())));
		}

		return sb.toString();
	}
	
	 

	    public static String toString(int i, int radix) { // SORGENTE RICAVATO dal toString di java > Integer.toString(n, 16);
		 String SYMBOLS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_-";
	        char[] digits = SYMBOLS.toCharArray();
	        char[] buf = new char[33];
	        boolean negative = (i < 0);
	        int charPos = 32;

	        if (!negative) {
	            i = -i;
	        }

	        while (i <= -radix) {
	            buf[charPos--] = digits[-(i % radix)];
	            i = i / radix;
	        }
	        buf[charPos] = digits[-i];

	        if (negative) {
	            buf[--charPos] = '-';
	        }

	        return new String(buf, charPos, (33 - charPos));
	    }

}
