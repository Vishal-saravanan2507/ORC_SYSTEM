package com.orc.wasteheat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "industrial_units")
public class IndustrialUnit {

    public enum IndustryType {
        STEEL, CEMENT, GLASS, CHEMICAL, TEXTILE, PAPER, FOOD_PROCESSING, POWER_PLANT, OTHER
    }

    public enum Status {
        ACTIVE, INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Industry name is required")
    @Column(nullable = false)
    private String industryName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IndustryType industryType;

    private String location;

    private String contactPerson;

    /** Installed electrical/process capacity of the industrial unit, in kW (informational). */
    private Double installedCapacity;

    private LocalDate createdDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.ACTIVE;

    @OneToMany(mappedBy = "industrialUnit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<WasteHeatData> wasteHeatRecords = new ArrayList<>();

    public IndustrialUnit() {
    }

    @PrePersist
    public void prePersist() {
        if (createdDate == null) {
            createdDate = LocalDate.now();
        }
        if (status == null) {
            status = Status.ACTIVE;
        }
    }

    // ---- Getters / Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIndustryName() { return industryName; }
    public void setIndustryName(String industryName) { this.industryName = industryName; }

    public IndustryType getIndustryType() { return industryType; }
    public void setIndustryType(IndustryType industryType) { this.industryType = industryType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public Double getInstalledCapacity() { return installedCapacity; }
    public void setInstalledCapacity(Double installedCapacity) { this.installedCapacity = installedCapacity; }

    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public List<WasteHeatData> getWasteHeatRecords() { return wasteHeatRecords; }
    public void setWasteHeatRecords(List<WasteHeatData> wasteHeatRecords) { this.wasteHeatRecords = wasteHeatRecords; }
}
