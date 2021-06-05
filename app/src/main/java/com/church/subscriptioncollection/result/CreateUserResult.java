package com.church.subscriptioncollection.result;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
public class CreateUserResult {
    @Nullable
    private String displayName;
    @Nullable
    private Integer error;

    public CreateUserResult(@Nullable Integer error) {
        this.error = error;
    }

    public CreateUserResult(@Nullable String displayName) {
        this.displayName = displayName;
    }

    @Nullable
    public String getSuccess() {
        return displayName;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}