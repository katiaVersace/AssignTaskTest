package test.main;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import test.model.Employee;
import test.model.Task;
import test.model.Team;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AssignTask {


    public static void main(String[] args) {

        // range visualized
        LocalDate
                start = LocalDate.parse("2019-12-01"),
                end = LocalDate.parse("2019-12-15");

        List<Employee> employees = randomMap(start, end);
        List<Task> tasks = new ArrayList<Task>();

        employees.parallelStream().forEach(e -> tasks.addAll(e.getTasks()));

        long daysMax = ChronoUnit.DAYS.between(start, end) + 1;
        System.out.println("Availability: \n" + printDays(start, daysMax));
        employees.stream().map(e -> printEmployeeScheduling(e, daysMax, start, end)).forEach(System.out::println);
        System.out.println(printFreeEmployees(start, end, daysMax, employees));
        Scanner reader = new Scanner(System.in);// Reading from System.in

        do {
            System.out.println("Enter a day of start for your task: ");
            LocalDate dateStart = LocalDate.parse("2019-12-" + String.format("%02d", Math.abs(reader.nextInt())));
            System.out.println("Enter a day for end for your task: ");
            LocalDate dateEnd = LocalDate.parse("2019-12-" + String.format("%02d", Math.abs(reader.nextInt())));

            //creating object
            Task task = new Task("temp", dateStart, null,
                    dateEnd, null);
            task.setId(tasks.size() + 1);
            task.setDescription(toString(task.getId(), 36));
            System.out.println("Trying to add Task " + task.getDescription());
            tasks.add(task);

            saveToFile(employees);

            // sort employees in  base alla loro disponibilità nel periodo del task
            Collections.sort(employees, (e1, e2) -> getTasksInPeriod(e1, task.getExpectedStartTime(), task.getExpectedEndTime()).size() - getTasksInPeriod(e2, task.getExpectedStartTime(), task.getExpectedEndTime()).size());

            List<Task> visitedTasks = new ArrayList<Task>();
            HashMap<Task, Employee> oldAssignments = new HashMap<Task, Employee>();
            if (checkNoSolution(task.getExpectedStartTime(), task.getExpectedEndTime(), employees) || !assignTaskToTeam(task, employees, visitedTasks, oldAssignments)) {
                System.out.println("Impossible to assign the task");
            } else {
                System.out.println("Solution: ");
                visitedTasks.stream().forEach(taskInSolution ->
                        System.out.println(String.format("%s -> %s", taskInSolution.getDescription(), taskInSolution.getEmployee().getUserName()))
                );
                System.out.println("New scheduling:\n" + printDays(start, daysMax));
                employees.stream().map(e -> printEmployeeScheduling(e, daysMax, start, end)).forEach(System.out::println);
                System.out.println(printFreeEmployees(start, end, daysMax, employees));
            }
            reader.nextLine();
            System.out.println("Press 0 to exit, others to continue");

        }
        while (!reader.nextLine().equals("0"));
        reader.close();
    }

    public static boolean assignTaskToTeam(Task task, List<Employee> team, List<Task> visitedTasks,
                                           HashMap<Task, Employee> oldAssignments) {

        visitedTasks.add(task);
        Employee oldEmployee = task.getEmployee();

        // caso base positivo (c'è un impiegato libero)
        AtomicBoolean scheduled = new AtomicBoolean(false);
        team.stream().filter(employee -> employee != task.getEmployee() && employeeAvailable(employee, task.getExpectedStartTime(), task.getExpectedEndTime()))
                .findFirst().ifPresent(newEmployee -> {
            assignTaskToEmployee(task, newEmployee);
            oldAssignments.put(task, oldEmployee);
            scheduled.set(true);
        });
        if (scheduled.get()) return true;

        // passo ricorsivo
        Optional<Employee> result = team.stream().filter(employee -> employee != task.getEmployee() && reassign(employee, task, visitedTasks, team, oldAssignments, oldEmployee) == true).findFirst();
        if (result.isPresent()) return true;

        visitedTasks.remove(task);

        return false;
    }

    public static boolean reassign(Employee employee, Task task, List<Task> visitedTasks, List<Employee> team, HashMap<Task, Employee> oldAssignments, Employee oldEmployee) {

        List<Task> tasksInPeriod = getTasksInPeriod(employee, task.getExpectedStartTime(),
                task.getExpectedEndTime());

        // forzatamente assegno il task all'impiegato e cerco di spostare i task di intralcio
        assignTaskToEmployee(task, employee);
        boolean atLeastOneFailed = false;

        for (Task taskToRearrange : tasksInPeriod) {
            if (visitedTasks.contains(taskToRearrange)) {
                atLeastOneFailed = true;
                break;
            }
            HashMap<Task, Employee> oldAssignmentsForBranch = new HashMap<Task, Employee>();
            if (taskInProgress(taskToRearrange)
                    || !assignTaskToTeam(taskToRearrange, team, visitedTasks, oldAssignmentsForBranch)) {
                atLeastOneFailed = true;
                break;
            } else {
                // tutti i branch hanno restituito true quindi faccio merge dei vecchi assegnamenti dei task in quei branch
                oldAssignments.putAll(oldAssignmentsForBranch);
            }
        }

        //se almeno uno degli assegnamenti dei task di intralcio fallisce, ripristino
        if (atLeastOneFailed) {

            assignTaskToEmployee(task, oldEmployee);
            // ripristino i task dei branch che hanno restituito true
            oldAssignments.entrySet().stream().forEach(entry -> {
                Task taskToRevert = entry.getKey();
                assignTaskToEmployee(taskToRevert, entry.getValue());
                visitedTasks.remove(taskToRevert);
            });
            oldAssignments.clear();
            return false;

        } else {
            oldAssignments.put(task, oldEmployee);
            return true;
        }
    }

    public static void assignTaskToEmployee(Task task, Employee employee) {

        if (task.getEmployee() != null) {
            task.getEmployee().getTasks().remove(task);
            if (task.getEmployee().getTasks().size() < 5) {
                task.getEmployee().setTopEmployee(false);
            }
        }
        if (employee != null) {
            employee.getTasks().add(task);
            if (employee.getTasks().size() >= 5) {
                employee.setTopEmployee(true);
            }
        }
        task.setEmployee(employee);
    }

    private static boolean taskInProgress(Task taskToRearrange) {

        LocalDate today = LocalDate.now();
        return (taskToRearrange.getExpectedStartTime().isBefore(today)
                && taskToRearrange.getExpectedEndTime().isAfter(today))
                || taskToRearrange.getExpectedStartTime().equals(today)
                || taskToRearrange.getExpectedEndTime().equals(today);
    }

    private static List<Task> getTasksInPeriod(Employee employee, LocalDate start, LocalDate end) {

        return employee.getTasks().stream().filter(t -> betweenTwoDate(start, t.getExpectedStartTime(), t.getExpectedEndTime())
                || betweenTwoDate(end, t.getExpectedStartTime(), t.getExpectedEndTime())
                || betweenTwoDate(t.getExpectedStartTime(), start, end)
                || betweenTwoDate(t.getExpectedEndTime(), start, end)).collect(Collectors.toList());
    }


    private static String printEmployeeScheduling(Employee employee, long scheduleSize, LocalDate start, LocalDate end) {

        String[] schedule = new String[(int) scheduleSize];
        Arrays.fill(schedule, "__");

        employee.getTasks().stream().forEach(task -> {
            LocalDate taskStartDate = task.getExpectedStartTime(), taskEndDate = task.getExpectedEndTime();
            // check on task start and end because a task can end over the end of the period specified or start before
            taskStartDate = (taskStartDate.isBefore(start)) ? start : taskStartDate;
            taskEndDate = (taskEndDate.isAfter(end)) ? end : taskEndDate;
            long taskStart = ChronoUnit.DAYS.between(start, taskStartDate), taskDuration = ChronoUnit.DAYS.between(taskStartDate, taskEndDate) + 1;
            String stringToPrint = toString(task.getId(), 36);
            stringToPrint = (stringToPrint.length() < 2) ? "0" + stringToPrint : stringToPrint;
            String finalStringToPrint = stringToPrint;
            IntStream.range((int) taskStart, (int) (taskStart + taskDuration))
                    .forEach(i -> schedule[i] = finalStringToPrint);
        });

        StringBuilder sb = new StringBuilder();
        Stream.of(schedule).forEach(availability -> sb.append(availability + "  "));
        sb.append(employee.getUserName());
        return sb.toString();
    }

    public static boolean assignTaskRandom(Task task, List<Employee> employees, LocalDate start, LocalDate end) {

        Collections.shuffle(employees);
        AtomicBoolean scheduled = new AtomicBoolean(false);
        employees.parallelStream().filter(employee -> employeeAvailable(employee, task.getExpectedStartTime(), task.getExpectedEndTime()))
                .findFirst().ifPresent(employee -> {
            assignTaskToEmployee(task, employee);
            scheduled.set(true);
        });
        if (scheduled.get()) return true;

        //nessun impiegato è disponibile in quel periodo quindi mi  prendo le disponibilità, ove ce ne siano
        employees.parallelStream().filter(employee -> getAvailability(employee, start, end) != null).findFirst().ifPresent(employee -> {
            LocalDate availability = getAvailability(employee, start, end);
            task.setExpectedStartTime(availability);
            task.setExpectedEndTime(availability);
            assignTaskToEmployee(task, employee);
            scheduled.set(true);
        });

        return scheduled.get();
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
        long daysMax = (ChronoUnit.DAYS.between(startInclusive, endInclusive)) + 1;
        return startInclusive.plusDays(rn.nextInt((int) daysMax));
    }

    public static boolean betweenTwoDate(LocalDate toCheck, LocalDate start, LocalDate end) {

        return (toCheck.isAfter(start) && toCheck.isBefore(end)) || toCheck.equals(start)
                || toCheck.equals(end);
    }

    public static boolean employeeAvailable(Employee e, LocalDate startTask, LocalDate endTask) {

        Optional<Task> result = e.getTasks().stream().filter(t -> betweenTwoDate(startTask, t.getExpectedStartTime(), t.getExpectedEndTime())
                || betweenTwoDate(endTask, t.getExpectedStartTime(), t.getExpectedEndTime())
                || betweenTwoDate(t.getExpectedStartTime(), startTask, endTask)
                || betweenTwoDate(t.getExpectedEndTime(), startTask, endTask))
                .findFirst();

        if (result.isPresent()) return false;

        return true;
    }

    public static String generateRandom(int length) {

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        Random random = new SecureRandom();
        if (length <= 0) {
            throw new IllegalArgumentException("String length must be a positive integer");
        }

        StringBuilder sb = new StringBuilder(length);

        IntStream.range(0, length).parallel()
                .forEach(i -> sb.append(characters.charAt(random.nextInt(characters.length()))));
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

    public static void saveToFile(List<Employee> team) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {

            writer.writeValue(new File("src/main/resources/test/lastSave.txt"), team);
        } catch (JsonGenerationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static List<Employee> loadMap() {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        File f = new File("src/main/resources/test/mapToLoad.txt");
        List<Employee> employees = null;
        try {
            employees = mapper.readValue(f,
                    mapper.getTypeFactory().constructCollectionType(List.class, Employee.class));
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        employees.parallelStream().forEach(e -> e.getTasks().forEach(t -> t.setEmployee(e)));

        return employees;
    }

    public static List<Employee> randomMap(LocalDate start, LocalDate end) {

        int teamsSize = 1, employeesSize = 10, tasksSize = 20, taskMaxDuration = 6, nRandomChars = 2;

        // Check input
        long daysMax = ChronoUnit.DAYS.between(start, end) + 1;
        if (teamsSize > (employeesSize / 2) || tasksSize < employeesSize || taskMaxDuration > daysMax) {
            System.out.println("invalid input");
            return null;
        }

        // create Employees
        List<Employee> employees = IntStream.range(0, employeesSize).parallel()
                .mapToObj(i -> {
                    String generatedString = generateRandom(nRandomChars);
                    Employee employee = new Employee("EMP_" + generatedString, generatedString, "Name_" + generatedString,
                            "LastName_" + generatedString, generatedString + "@alten.it", false);
                    employee.setId(i + 1);
                    return employee;
                }).collect(Collectors.toList());


        // create Teams
        List<Team> teams = IntStream.range(0, teamsSize).parallel()
                .mapToObj(i -> {
                    String generatedString = generateRandom(nRandomChars);
                    Team team = new Team("TEAM_" + generatedString);
                    team.setId(i + 1);
                    return team;
                }).collect(Collectors.toList());


        // assign team to employees

        int teamIndex = 0;
        for (Employee employee : employees) {
            teams.get(teamIndex).getEmployees().add(employee);
            teamIndex++;
            if (teamIndex == teamsSize)
                teamIndex = 0;
        }

        // create Tasks
        Random rn = new Random();

        List<Task> tasks = IntStream.range(0, tasksSize).parallel()
                .mapToObj(i -> {
                    int duration = rn.nextInt(taskMaxDuration) + 1;
                    String generatedString = toString(i + 1, 36);
                    if (generatedString.length() < 2)
                        generatedString = "0" + generatedString;
                    LocalDate endMinusDuration = end.plusDays(-duration);
                    LocalDate expectedStartTime = between(start, endMinusDuration);
                    LocalDate expectedEndTime = expectedStartTime.plusDays(duration - 1);
                    Task task = new Task(generatedString, expectedStartTime, null, expectedEndTime, null);
                    task.setId(i + 1);
                    return task;
                }).collect(Collectors.toList());


        // assign at least 1 task for each employee

        IntStream.range(0, employees.size()).parallel()
                .forEach(taskIndex -> {
                            tasks.get(taskIndex).setEmployee(employees.get(taskIndex));
                            employees.get(taskIndex).getTasks().add(tasks.get(taskIndex));
                        }
                );

        // assign remaining task
        List<Task> remainingTasks = new ArrayList<Task>(tasks.subList(employees.size(), tasks.size()));

        boolean availability = true;
        while (remainingTasks.size() != 0 && availability) {
            if (assignTaskRandom(remainingTasks.get(0), employees, start, end)) {
                remainingTasks.remove(0);
            } else {
                availability = false;
                System.out.println(
                        remainingTasks.size() + " tasks, have not been assigned because no employee is available");
            }

        }
        return employees;
    }

    private static String printDays(LocalDate start, long daysMax) {

        StringBuilder sb = new StringBuilder();
        IntStream.range(0, (int) daysMax)
                .forEach(i -> {
                    LocalDate currentDay = start.plusDays(i);
                    String day = String.valueOf(currentDay.getDayOfMonth());
                    if (currentDay.getDayOfMonth() < 10) {
                        day = "0" + day;
                    }
                    sb.append(day + "  ");
                });
        return sb.toString();
    }

    public static String printFreeEmployees(LocalDate start, LocalDate end, long scheduleSize, List<Employee> employees) {

        Integer[] freeEmployees = new Integer[(int) scheduleSize];
        Arrays.fill(freeEmployees, 0);

        LocalDate currentDay = start;
        int currentIndexDay = 0;
        while (currentDay.compareTo(end) <= 0) {

            LocalDate finalCurrentDay = currentDay;
            int finalCurrentIndexDay = currentIndexDay;

            employees.stream().filter(employee -> employeeAvailable(employee, finalCurrentDay, finalCurrentDay)).forEach((e) -> freeEmployees[finalCurrentIndexDay]++);

            currentDay = currentDay.plusDays(1);
            currentIndexDay++;
        }
        StringBuilder sb = new StringBuilder();
        Stream.of(freeEmployees).forEach(nFreeEmployees -> {
            if (nFreeEmployees < 10) {
                sb.append("0" + nFreeEmployees + "  ");
            } else
                sb.append(nFreeEmployees + "  ");
        });

        sb.append("Free Employees");
        return sb.toString();
    }

    public static boolean checkNoSolution(LocalDate start, LocalDate end, List<Employee> employees) {

        LocalDate currentDay = start;
        while (currentDay.compareTo(end) <= 0) {
            LocalDate finalCurrentDay = currentDay;
            Optional<Employee> result = employees.parallelStream().filter(employee -> employeeAvailable(employee, finalCurrentDay, finalCurrentDay)).findFirst();
            if (!result.isPresent()) {
                return true;
            }
            currentDay = currentDay.plusDays(1);
        }
        return false;

    }

}
