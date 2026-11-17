package com.fukurou.telemetry;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Counters for project and class lifecycle events in a session, plus the
 * current project's name and directory. Updated by
 * {@link com.fukurou.handlers.LifecycleHandler}.
 * <p>
 * Methods that take a project name use {@code "unknown"} when it is {@code null}.
 * </p>
 */
public class LifecycleData {

    /** Number of classes created. */
    private int classCreates;
    /** Number of class modifications. */
    private int classModifies;
    /** Number of classes deleted. */
    private int classDeletes;
    /** Number of projects opened. */
    private int projectsOpened;
    /** Number of projects closed. */
    private int projectsClosed;
    /** Running total of method definitions added. */
    private int totalMethodDefinitions;

    /** Increments the number of projects opened. */
    public void projectOpened() { projectsOpened++; }

    /** Increments the number of projects closed. */
    public void projectClosed() { projectsClosed++; }

    /** Class names currently known in each project, keyed by project name. */
    private final Map<String, Set<String>> knownClassesPerProject;
    /** Number of class modifications in each project, keyed by project name. */
    private final Map<String, Integer> classModifiedPerProject;
    /** Name of the current project, or {@code null} if none is set. */
    private String currentProjectName;
    /** Directory of the current project, or {@code null} if none is set. */
    private File projectDir;

    /** Creates lifecycle data with all counts at zero and no known classes. */
    public LifecycleData() {
        knownClassesPerProject = new HashMap<>();
        classModifiedPerProject = new HashMap<>();
    }

    /**
     * Increments the class-create count and adds the class to the project's known classes.
     *
     * @param project the project name, or {@code null}
     * @param name    the class name
     */
    public void classCreate(String project, String name) {
        classCreates++;
        String key = project != null ? project : "unknown";
        Set<String> set = knownClassesPerProject.get(key);
        if (set == null) {
            set = new HashSet<>();
            knownClassesPerProject.put(key, set);
        }
        set.add(name);
    }

    /**
     * Increments the total class-modify count and the count for the project.
     *
     * @param project the project name, or {@code null}
     */
    public void classModify(String project) {
        classModifies++;
        String key = project != null ? project : "unknown";
        Integer prev = classModifiedPerProject.get(key);
        classModifiedPerProject.put(key, prev == null ? 1 : prev + 1);
    }

    /**
     * Increments the class-delete count and removes the class from the project's known classes.
     *
     * @param project the project name, or {@code null}
     * @param name    the class name
     */
    public void classDelete(String project, String name) {
        classDeletes++;
        String key = project != null ? project : "unknown";
        Set<String> set = knownClassesPerProject.get(key);
        if (set != null) {
            set.remove(name);
        }
    }

    /**
     * Whether the class has been created in the project and not deleted since.
     *
     * @param project the project name, or {@code null}
     * @param name    the class name
     * @return {@code true} if the class is known in the project
     */
    public boolean isKnownClass(String project, String name) {
        String key = project != null ? project : "unknown";
        Set<String> set = knownClassesPerProject.get(key);
        return set != null && set.contains(name);
    }

    /**
     * Sets the current project name.
     *
     * @param n the project name, or {@code null} to clear it
     */
    public void setCurrentProjectName(String n) { currentProjectName = n; }

    /**
     * Records the active BlueJ project directory, used to place report output.
     *
     * @param dir the project directory
     */
    public void setProjectDir(File dir) { this.projectDir = dir; }

    /**
     * Adds to the running total of method definitions.
     *
     * @param n the number of methods to add
     */
    public void addMethodCount(int n) {this.totalMethodDefinitions += n;}

    /** @return the running total of method definitions */
    public int getTotalMethodDefinitions() {return totalMethodDefinitions;}

    /** @return the current project name, or {@code null}; not included in JSON */
    @JsonIgnore
    public String getCurrentProjectName() { return currentProjectName; }

    /** @return the current project directory, or {@code null}; not included in JSON */
    @JsonIgnore
    public File getProjectDir() { return projectDir; }

    /** @return the number of projects opened */
    public int getProjectsOpened() { return projectsOpened; }

    /** @return the number of projects closed */
    public int getProjectsClosed() { return projectsClosed; }

    /** @return the number of classes created */
    public int getClassCreates()  { return classCreates; }

    /** @return the total number of class modifications */
    public int getClassModifies() { return classModifies; }

    /** @return the number of classes deleted */
    public int getClassDeletes()  { return classDeletes; }

    /** @return class modification counts keyed by project name (the internal map, not a copy) */
    public Map<String, Integer> getClassModifiedPerProject() { return classModifiedPerProject; }
}
