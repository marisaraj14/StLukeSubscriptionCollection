package com.church.subscriptioncollection.result;

import androidx.annotation.Nullable;

import com.church.subscriptioncollection.model.Volunteer;

import java.util.List;

public class VolunteerResult {
    private Integer error;
    private List<Volunteer> volunteerList;

    public VolunteerResult(@Nullable Integer error) {
        this.error = error;
    }

    public VolunteerResult(List<Volunteer> volunteerList) {
        this.volunteerList = volunteerList;
    }

    public List<Volunteer> getVolunteerList() {
        return volunteerList;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}
