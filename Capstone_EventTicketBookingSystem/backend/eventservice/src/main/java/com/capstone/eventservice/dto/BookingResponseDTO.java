package com.capstone.eventservice.dto;

public class BookingResponseDTO
{
    private Long id;
    private Long eventId;
    private Integer noOfTickets;
    private Double totalAmount;
    private String status;
    private String customerName;
    private String customerEmail;

    public BookingResponseDTO(){}

    public BookingResponseDTO(Long id,Long eventId,Integer noOfTickets,Double totalAmount,String status,String customerName,String customerEmail)
    {
        this.id = id;
        this.eventId = eventId;
        this.noOfTickets = noOfTickets;
        this.totalAmount = totalAmount;
        this.status = status;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
    }

    public Long getId(){return id;}
    public Long getEventId(){return eventId;}
    public Integer getNoOfTickets(){return noOfTickets;}
    public Double getTotalAmount(){return totalAmount;}
    public String getStatus() { return status; }
    public String getCustomerName() { return customerName; }
    public String getCustomerEmail() { return customerEmail; }

    public void setId(Long id){this.id = id;}
    public void setEventId(Long eventId){this.eventId = eventId;}
    public void setNoOfTickets(Integer noOfTickets){this.noOfTickets = noOfTickets;}
    public void setTotalAmount(Double totalAmount){this.totalAmount = totalAmount;}
    public void setStatus(String status) { this.status = status; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
}