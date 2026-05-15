package ru.mirea.vakhrushevra.employeedb;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Employee {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;

    public String universe;

    public String power;
}