package com.fukurou;

import bluej.extensions2.PackageNotFoundException;
import bluej.extensions2.ProjectNotOpenException;
import bluej.extensions2.event.ClassEvent;
import bluej.extensions2.event.ClassListener;
import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;

public class LifecycleHandler implements ClassListener, PackageListener {

    private final LifecycleData data;

    public LifecycleHandler(LifecycleData data) {
        this.data = data;
    }

    /**
     * Speradic multiple opened projects can cause the project name to be null.
     * this is not ideal as it is assigend wrong project names to events or unknown due to BlueJ not being able to resolve the project name. 
     */
    private String resolveProject(ClassEvent event) {
        String project = data.getCurrentProjectName();
        if (project != null) {
            return project;
        }
        try {
            project = event.getBClass().getPackage().getProject().getName();
            data.setCurrentProjectName(project);
            return project;
        }   
        catch (ProjectNotOpenException | PackageNotFoundException e) {
            return "unknown";
        }
    }

    @Override
    public void classStateChanged(ClassEvent event) {
        String project = resolveProject(event);
        String className = event.getBClass().getName();

        if (!data.isKnownClass(project, className)) {
            data.classCreate(project, className);
            data.addEvent(new LifecycleEvent(
                    LifecycleEvent.Type.CREATE,
                    project,
                    className));
        } else {
            data.classModify(project);
            data.addEvent(new LifecycleEvent(
                    LifecycleEvent.Type.MODIFY,
                    project,
                    className));
        }
    }

    @Override
    public void classNameChanged(ClassEvent event) {
        String project = resolveProject(event);
        data.classModify(project);
        data.addEvent(new LifecycleEvent(
                LifecycleEvent.Type.MODIFY,
                project,
                event.getBClass().getName() + " (renamed from " + event.getOldName() + ")"));
    }

    @Override
    public void classRemoved(ClassEvent event) {
        String project = resolveProject(event);
        String className = event.getBClass().getName();
        data.classDelete(project, className);
        data.addEvent(new LifecycleEvent(
                LifecycleEvent.Type.DELETE,
                project,
                className));
    }

    @Override
    public void packageOpened(PackageEvent event) {
        String projectName;
        try {
            projectName = event.getPackage().getProject().getName();
        } catch (ProjectNotOpenException e) {
            return;
        }
        data.setCurrentProjectName(projectName);
        data.projectOpened();                    // ← MUST be here
    }

    @Override
    public void packageClosing(PackageEvent event) {
        String name = data.getCurrentProjectName();
        if (name == null) return;
        data.setCurrentProjectName(null);
        data.projectClosed();                    // ← MUST be here
    }  
}