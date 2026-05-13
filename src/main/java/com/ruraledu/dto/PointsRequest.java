package com.ruraledu.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;


/**
 * DTO for adding gamification points with validation.
 */
public class PointsRequest {
    
    @NotNull(message = "Points are required")
    @Min(value = 0, message = "Points cannot be negative")
    @Max(value = 50, message = "Maximum points per transaction is 50")
    private Integer points;
    
    @NotNull(message = "Action type is required")
    private String actionType;
    
    @NotNull(message = "Entity ID is required")
    private Long entityId;

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
