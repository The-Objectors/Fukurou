package com.fukurou;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class LifecycleData {

    private int classCreates;
    private int classModifies;
    private int classDeletes;
    private int projectsOpened;
    private int projectsClosed;

    public void projectOpened() { projectsOpened++; }
    public void projectClosed() { projectsClosed++; }
    private final ArrayList<LifecycleEvent> events;
    private final Map<String, Set<String>> knownClassesPerProject;
    private final Map<String, Integer> classModifiedPerProject;
    private String currentProjectName;

    public LifecycleData() {
        events = new ArrayList<LifecycleEvent>();
        knownClassesPerProject = new HashMap<String, Set<String>>();
        classModifiedPerProject = new HashMap<String, Integer>();
    }

    public void classCreate(String project, String name) {
        classCreates++;
        String key = project != null ? project : "unknown";
        Set<String> set = knownClassesPerProject.get(key);
        if (set == null) {
            set = new HashSet<String>();
            knownClassesPerProject.put(key, set);
        }
        set.add(name);
    }

    public void classModify(String project) {
        classModifies++;
        String key = project != null ? project : "unknown";
        Integer prev = classModifiedPerProject.get(key);
        classModifiedPerProject.put(key, prev == null ? 1 : prev + 1);
    }

    public void classDelete(String project, String name) {
        classDeletes++;
        String key = project != null ? project : "unknown";
        Set<String> set = knownClassesPerProject.get(key);
        if (set != null) {
            set.remove(name);
        }
    }

    public boolean isKnownClass(String project, String name) {
        String key = project != null ? project : "unknown";
        Set<String> set = knownClassesPerProject.get(key);
        return set != null && set.contains(name);
    }

    public void addEvent(LifecycleEvent e) { events.add(e); }

    public void setCurrentProjectName(String n) { currentProjectName = n; }

    @JsonIgnore
    public String getCurrentProjectName() { return currentProjectName; }
    public int getProjectsOpened() { return projectsOpened; }
    public int getProjectsClosed() { return projectsClosed; }
    public int getClassCreates()  { return classCreates; }
    public int getClassModifies() { return classModifies; }
    public int getClassDeletes()  { return classDeletes; }
    public ArrayList<LifecycleEvent> getEvents() { return events; }
    public Map<String, Integer> getClassModifiedPerProject() { return classModifiedPerProject; }
}