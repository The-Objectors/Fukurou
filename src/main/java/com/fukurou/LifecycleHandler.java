package com.fukurou;

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

    @Override
    public void classStateChanged(ClassEvent event) {
        String className = event.getBClass().getName();

        if (!data.isKnownClass(className)) {
            data.classCreate(className);
            data.addEvent(new LifecycleEvent(
                    LifecycleEvent.Type.CREATE,
                    LifecycleEvent.Target.CLASS,
                    className));
        } else {
            data.classModify();
            data.addEvent(new LifecycleEvent(
                    LifecycleEvent.Type.MODIFY,
                    LifecycleEvent.Target.CLASS,
                    className));
        }
    }

    @Override
    public void classNameChanged(ClassEvent event) {
        data.classModify();
        data.addEvent(new LifecycleEvent(
                LifecycleEvent.Type.MODIFY,
                LifecycleEvent.Target.CLASS,
                event.getBClass().getName() + " (renamed from " + event.getOldName() + ")"));
    }

    @Override
    public void classRemoved(ClassEvent event) {
        String className = event.getBClass().getName();
        data.classDelete(className);
        data.addEvent(new LifecycleEvent(
                LifecycleEvent.Type.DELETE,
                LifecycleEvent.Target.CLASS,
                className));
    }

    @Override
    public void packageOpened(PackageEvent event) {
        if (data.getCurrentProjectName() != null) {
            data.projectDelete();
            data.addEvent(new LifecycleEvent(
                LifecycleEvent.Type.DELETE,
                LifecycleEvent.Target.PROJECT,
                data.getCurrentProjectName()));
        }

        String projectName = "unknown";
        try {
            projectName = event.getPackage().getProject().getName();
        } catch (ProjectNotOpenException e) {
        }

        data.setCurrentProjectName(projectName);
        data.projectCreate();
        data.addEvent(new LifecycleEvent(
            LifecycleEvent.Type.CREATE,
            LifecycleEvent.Target.PROJECT,
            projectName));
    }

    @Override
    public void packageClosing(PackageEvent event) {
        if (data.getCurrentProjectName() == null) {
            return;  
        }   
        String name = data.getCurrentProjectName();
        data.projectDelete();
        data.addEvent(new LifecycleEvent(
            LifecycleEvent.Type.DELETE,
            LifecycleEvent.Target.PROJECT,
            name));
        data.setCurrentProjectName(null);  
    }
}