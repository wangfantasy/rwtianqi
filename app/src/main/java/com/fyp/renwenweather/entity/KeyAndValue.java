package com.fyp.renwenweather.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by fyp on 2016/3/22.
 */
@Table(name = "config")
public class KeyAndValue {
    @Column(
            name = "id",
            isId = true,
            autoGen = true
    )
    int id;
    @Column(name = "key")
    String key;
    @Column(name = "value")
    String value;

    public KeyAndValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public KeyAndValue() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
