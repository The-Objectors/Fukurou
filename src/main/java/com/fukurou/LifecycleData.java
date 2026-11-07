package com.fukurou;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LifecycleData {

    private int projectCreates;
    private int projectModifies;
    private int projectDeletes;

    private int classCreates;
    private int classModifies;
    private int classDeletes;

    private final ArrayList<LifecycleEvent> events;
    private final Set<String> knownClassNames;
    private String currentProjectName;

    public LifecycleData() {
        events = new ArrayList<LifecycleEvent>();
        knownClassNames = new HashSet<String>();
    }

    public void projectCreate() { projectCreates++; }
    public void projectModify() { projectModifies++; }
    public void projectDelete() { projectDeletes++; }

    public void classCreate(String name) {
        classCreates++;
        knownClassNames.add(name);
    }
    public void classModify() { classModifies++; }
    public void classDelete(String name) {
        classDeletes++;
        knownClassNames.remove(name);
    }

    public void addEvent(LifecycleEvent e) { events.add(e); }

    public boolean isKnownClass(String name)  { return knownClassNames.contains(name); }
    public void setCurrentProjectName(String n){ currentProjectName = n; }
    public String getCurrentProjectName()     { return currentProjectName; }

    public int getProjectCreates() { return projectCreates; }
    public int getProjectModifies(){ return projectModifies; }
    public int getProjectDeletes() { return projectDeletes; }
    public int getClassCreates()   { return classCreates; }
    public int getClassModifies()  { return classModifies; }
    public int getClassDeletes()   { return classDeletes; }
    public ArrayList<LifecycleEvent> getEvents() { return events; }
}