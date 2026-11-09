package com.fukurou;

import java.util.HashMap;
import java.util.Map;

import com.fukurou.TelemetryEvent.Target;
import com.fukurou.TelemetryEvent.Type;

import bluej.extensions2.BMethod;
import bluej.extensions2.PackageNotFoundException;
import bluej.extensions2.ProjectNotOpenException;
import bluej.extensions2.event.ClassEvent;
import bluej.extensions2.event.ClassListener;
import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;

public class LifecycleHandler implements ClassListener, PackageListener {

    private final LifecycleData data;
    private final EventLog log;
    private final Map<String, Integer> methodCountPerClass = new HashMap<String, Integer>();

    public LifecycleHandler(LifecycleData data, EventLog log) {
        this.data = data;
        this.log = log;
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
            log.record(Target.CLASS, Type.CREATE, project, className, null);
        } else {
            data.classModify(project);
            log.record(Target.CLASS, Type.MODIFY, project, className, null);
        }
        try {
            BMethod[] methods = event.getBClass().getDeclaredMethods();
            int currentCount = (methods != null) ? methods.length : 0;

            String key = project + "::" + className;
            Integer previousCount = methodCountPerClass.get(key);

            if (previousCount == null) {
                if (currentCount > 0) {
                    log.record(Target.CLASS, Type.MODIFY, project, className,
                            "baseline: " + currentCount + " method(s)");
                    data.addMethodCount(currentCount);
                }
            } else if (currentCount > previousCount) {
                int added = currentCount - previousCount;
                log.record(Target.CLASS, Type.MODIFY, project, className,
                        "+ " + added + " method(s) (total " + currentCount + ")");
                data.addMethodCount(added);
            } else if (currentCount < previousCount) {
                int removed = previousCount - currentCount;
                log.record(Target.CLASS, Type.MODIFY, project, className,
                        "- " + removed + " method(s) (total " + currentCount + ")");
            }

            methodCountPerClass.put(key, currentCount);
        } catch (Exception e) {
            // Reflection can fail if class disappears mid-event — non-fatal, ignore
        }
    }

    @Override
    public void classNameChanged(ClassEvent event) {
        String project = resolveProject(event);
        data.classModify(project);
        log.record(Target.CLASS, Type.MODIFY, project,
                event.getBClass().getName(),
                "renamed from " + event.getOldName());
    }

    @Override
    public void classRemoved(ClassEvent event) {
        String project = resolveProject(event);
        String className = event.getBClass().getName();
        data.classDelete(project, className);
        log.record(Target.CLASS, Type.DELETE, project, className, null);
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
        data.projectOpened();
        log.record(Target.PROJECT, Type.OPEN, projectName, projectName, null);
    }

    @Override
    public void packageClosing(PackageEvent event) {
        String name = data.getCurrentProjectName();
        if (name == null) return;
        data.setCurrentProjectName(null);
        data.projectClosed();
        log.record(Target.PROJECT, Type.CLOSE, name, name, null);
    }
}
