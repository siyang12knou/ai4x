package com.kailoslab.ai4x.alarm.db.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
public class AlarmObjectTypePK implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "object_type")
    @JsonProperty("object_type")
    private String objectType;
    private String property;

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((objectType == null) ? 0 : objectType.hashCode());
        result = prime * result + ((property == null) ? 0 : property.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AlarmObjectTypePK other = (AlarmObjectTypePK) obj;
        if (objectType == null) {
            if (other.objectType != null)
                return false;
        } else if (!objectType.equals(other.objectType))
            return false;
        if (property == null) {
            return other.property == null;
        } else return property.equals(other.property);
    }

}
