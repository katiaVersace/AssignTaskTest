package test.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

public class Task {

	private int id;

	private String description;

	@JsonIgnore
	private Employee employee;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate expectedStartTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate realStartTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate expectedEndTime;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate realEndTime;

	private int version;

	public Task(String description, LocalDate expectedStartTime, LocalDate realStartTime, LocalDate expected_end_time,
			LocalDate realEndTime) {
		super();
		this.description = description;
		this.expectedStartTime = expectedStartTime;
		this.realStartTime = realStartTime;
		this.expectedEndTime = expected_end_time;
		this.realEndTime = realEndTime;
		this.version = 0;
	}

	public Task() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public LocalDate getExpectedStartTime() {
		return expectedStartTime;
	}

	public void setExpectedStartTime(LocalDate expectedStartTime) {
		this.expectedStartTime = expectedStartTime;
	}

	public LocalDate getRealStartTime() {
		return realStartTime;
	}

	public void setRealStartTime(LocalDate realStartTime) {
		this.realStartTime = realStartTime;
	}

	public LocalDate getExpectedEndTime() {
		return expectedEndTime;
	}

	public void setExpectedEndTime(LocalDate expected_end_time) {
		this.expectedEndTime = expected_end_time;
	}

	public LocalDate getRealEndTime() {
		return realEndTime;
	}

	public void setRealEndTime(LocalDate realEndTime) {
		this.realEndTime = realEndTime;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", description=" + description + ", employee=" + employee + ", expectedStartTime="
				+ expectedStartTime + ", realStartTime=" + realStartTime + ", expectedEndTime=" + expectedEndTime
				+ ", realEndTime=" + realEndTime + ", version=" + version + "]";
	}

}
