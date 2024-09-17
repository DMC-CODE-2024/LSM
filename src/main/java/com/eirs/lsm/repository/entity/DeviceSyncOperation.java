package com.eirs.lsm.repository.entity;

public enum DeviceSyncOperation {
    DELETE, ADD;

    public static DeviceSyncOperation get(Integer index) {
        return DeviceSyncOperation.values()[index];
    }
}
