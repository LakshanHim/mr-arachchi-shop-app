package lk.Arachchi.dto;

import java.time.LocalDate;

public class OrderDto {
    private String name;
    private String type;
    private int phone;
    private String email;
    private String status;
    private String note;
    private LocalDate formatDate;

    public OrderDto(String name, String type, int phone, String email, String status, String note, LocalDate formatDate) {
        this.name = name;
        this.type = type;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.note = note;
        this.formatDate = formatDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getFormatDate() {
        return formatDate;
    }

    public void setFormatDate(LocalDate formatDate) {
        this.formatDate = formatDate;
    }
}
