package com.fukurou.handlers;

import java.util.HashMap;
import java.util.Map;

import com.fukurou.telemetry.EventLog;
import com.fukurou.telemetry.LifecycleData;
import com.fukurou.telemetry.TelemetryEvent.Target;
import com.fukurou.telemetry.TelemetryEvent.Type;

import java.io.File;

import bluej.extensions2.BMethod;
import bluej.extensions2.BProject;
import bluej.extensions2.PackageNotFoundException;
import bluej.extensions2.ProjectNotOpenException;
import bluej.extensions2.event.ClassEvent;
import bluej.extensions2.event.ClassListener;
import bluej.extensions2.event.PackageEvent;
import bluej.extensions2.event.PackageListener;

/**
 * Observes BlueJ class and package-lifecycle events and records them into
 * {@link LifecycleData} and {@link EventLog}.
 *
 * This handler is registered with BlueJ (the subject) via
 * {@code addClassListener}/{@code addPackageListener}; BlueJ pushes
 * events to the {@link ClassListener}/{@link PackageListener} callbacks below. New
 * reactions can be added without BlueJ knowing about this class.
 */
public class LifecycleHandler implements ClassListener, PackageListener {

    /** Project and class counters updated by this handler. */
    private final LifecycleData data;
    /** Event log that receives {@link Target#CLASS} and {@link Target#PROJECT} entries. */
    private final EventLog log;
    /** Last seen declared-method count per class, keyed by {@code "project::className"}. */
    private final Map<String, Integer> methodCountPerClass = new HashMap<String, Integer>();

    /**
     * Creates a handler that writes to the given lifecycle data and event log.
     *
     * @param data the lifecycle counters to update
     * @param log  the event log to record lifecycle events in
     */
    public LifecycleHandler(LifecycleData data, EventLog log) {
        this.data = data;
        this.log = log;
    }

    /**
     * Resolves the project name for a class event. Uses the current project name
     * held in {@link LifecycleData}; if none is set, reads it from the event's
     * class and stores it as the current project.
     * <p>
     * When several projects are open the stored name may not match the project
     * the event came from, so events can be attributed to the wrong project.
     * </p>
     *
     * @param event the BlueJ class event
     * @return the project name, or {@code "unknown"} if BlueJ cannot resolve it
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

    /**
     * Records a class as created the first time it is seen in a project, and as
     * modified on later state changes. Also compares the class's declared-method
     * count with the previous count and logs the first count as a baseline, then
     * any methods added or removed. Added methods (and the baseline) are added to
     * the total in {@link LifecycleData}. Failures while reading the methods are ignored.
     *
     * @param event the BlueJ class event
     */
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

    /**
     * Counts a rename as a class modification and logs the old name.
     *
     * @param event the BlueJ class event
     */
    @Override
    public void classNameChanged(ClassEvent event) {
        String project = resolveProject(event);
        data.classModify(project);
        log.record(Target.CLASS, Type.MODIFY, project,
                event.getBClass().getName(),
                "renamed from " + event.getOldName());
    }

    /**
     * Records a class deletion.
     *
     * @param event the BlueJ class event
     */
    @Override
    public void classRemoved(ClassEvent event) {
        String project = resolveProject(event);
        String className = event.getBClass().getName();
        data.classDelete(project, className);
        log.record(Target.CLASS, Type.DELETE, project, className, null);
    }

    /**
     * Stores the opened project's name and directory as current, counts the open
     * and logs a {@link Type#OPEN} event. Does nothing if the project is not open.
     *
     * @param event the BlueJ package event
     */
    @Override
    public void packageOpened(PackageEvent event) {
        String projectName;
        File projectDir;
        try {
            BProject project = event.getPackage().getProject();
            projectName = project.getName();
            projectDir = project.getDir();
        } catch (ProjectNotOpenException e) {
            return;
        }
        data.setCurrentProjectName(projectName);
        data.setProjectDir(projectDir);
        data.projectOpened();
        log.record(Target.PROJECT, Type.OPEN, projectName, projectName, null);
    }

    /**
     * Clears the current project name, counts the close and logs a
     * {@link Type#CLOSE} event. Does nothing if no project is current.
     *
     * @param event the BlueJ package event
     */
    @Override
    public void packageClosing(PackageEvent event) {
        String name = data.getCurrentProjectName();
        if (name == null) return;
        data.setCurrentProjectName(null);
        data.projectClosed();
        log.record(Target.PROJECT, Type.CLOSE, name, name, null);
    }
}
