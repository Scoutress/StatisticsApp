package lt.scoutress.StatisticsApp.entity;

import java.sql.Date;

import jakarta.persistence.*;

@Entity
@Table(name = "scoutress_help")
public class HelpRequests {
    
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private Date date;

    @Column(name = "help_answered")
    private Integer amountOfTickets;

    @Column(name = "help_count")
    private Integer countOfTickets;

    @Column(name = "help_comparison")
    private Double ticketComparison;

    @Column(name = "help_percent")
    private Double ticketComparisonPercent;

    @Column(name = "help_sum")
    private Integer allThatDayTickets;

    public HelpRequests(Date date, Integer amountOfTickets, Integer countOfTickets, Double ticketComparison,
        Double ticketComparisonPercent, Integer allThatDayTickets) {
        this.date = date;
        this.amountOfTickets = amountOfTickets;
        this.countOfTickets = countOfTickets;
        this.ticketComparison = ticketComparison;
        this.ticketComparisonPercent = ticketComparisonPercent;
        this.allThatDayTickets = allThatDayTickets;
    }

    public HelpRequests(){}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getAmountOfTickets() {
        return amountOfTickets;
    }

    public void setAmountOfTickets(Integer amountOfTickets) {
        this.amountOfTickets = amountOfTickets;
    }

    public Integer getCountOfTickets() {
        return countOfTickets;
    }

    public void setCountOfTickets(Integer countOfTickets) {
        this.countOfTickets = countOfTickets;
    }

    public Double getTicketComparison() {
        return ticketComparison;
    }

    public void setTicketComparison(Double ticketComparison) {
        this.ticketComparison = ticketComparison;
    }

    public Double getTicketComparisonPercent() {
        return ticketComparisonPercent;
    }

    public void setTicketComparisonPercent(Double ticketComparisonPercent) {
        this.ticketComparisonPercent = ticketComparisonPercent;
    }

    public Integer getAllThatDayTickets() {
        return allThatDayTickets;
    }

    public void setAllThatDayTickets(Integer allThatDayTickets) {
        this.allThatDayTickets = allThatDayTickets;
    }

    
}
