package lt.scoutress.StatisticsApp.entity.McTickets;

import lt.scoutress.StatisticsApp.entity.Employee;

public class McTicketsAvgDailyRatioResponse {
    private Integer id;
    private Employee employee;
    private Double averageDailyRatio;

    public McTicketsAvgDailyRatioResponse(Employee employee, Double averageDailyRatio) {
        this.employee = employee;
        this.averageDailyRatio = averageDailyRatio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getAverageDailyRatio() {
        return averageDailyRatio;
    }

    public void setAverageDailyRatio(Double averageDailyRatio) {
        this.averageDailyRatio = averageDailyRatio;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
