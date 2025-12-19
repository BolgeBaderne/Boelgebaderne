package com.example.bolgebaderne.model;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.LocalTime;

    @Entity
    @Table(name = "shifts")
    public class Shift {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer shiftId;

        @Column(nullable = false)
        private LocalDate date;

        @Column(nullable = false)
        private LocalTime startTime;

        @Column(nullable = false)
        private LocalTime endTime;

        @Column(nullable = false)
        private String label;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        public Integer getShiftId() {
            return shiftId;
        }

        public void setShiftId(Integer shiftId) {
            this.shiftId = shiftId;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public LocalTime getStartTime() {
            return startTime;
        }

        public void setStartTime(LocalTime startTime) {
            this.startTime = startTime;
        }

        public LocalTime getEndTime() {
            return endTime;
        }

        public void setEndTime(LocalTime endTime) {
            this.endTime = endTime;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

