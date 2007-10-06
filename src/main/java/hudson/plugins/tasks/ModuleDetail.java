package hudson.plugins.tasks;

import hudson.model.ModelObject;
import hudson.plugins.tasks.Task.Priority;
import hudson.plugins.tasks.util.ChartBuilder;
import hudson.util.ChartUtil;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jfree.chart.JFreeChart;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

/**
 * Represents the details of a maven module.
 */
public class ModuleDetail implements ModelObject, Serializable {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = -3743047168363581305L;
    /** The current build as owner of this object. */
    @SuppressWarnings("Se")
    private final TasksResult owner;
    /** The selected module to show. */
    private final MavenModule module;

    /**
     * Creates a new instance of <code>TaskDetail</code>.
     *
     * @param tasksResult
     *            the current build as owner of this action
     * @param module
     *            the selected package to show
     */
    public ModuleDetail(final TasksResult tasksResult, final MavenModule module) {
        owner = tasksResult;
        this.module = module;
    }

    /**
     * Returns the owner.
     *
     * @return the owner
     */
    public TasksResult getOwner() {
        return owner;
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return module.getName();
    }

    /**
     * Gets the modules of this project that have open tasks.
     *
     * @return the modules
     */
    public Collection<JavaPackage> getPackages() {
        return module.getPackages();
    }

    /**
     * Returns the files.
     *
     * @return the files
     */
    public Set<WorkspaceFile> getFiles() {
        return module.getFiles();
    }

    /**
     * Returns the defined priorities.
     *
     * @return the defined priorities.
     */
    public List<String> getPriorities() {
        return owner.getPriorities();
    }

    /**
     * Returns the tags for the specified priority.
     *
     * @param priority
     *            the priority
     * @return the tags for priority high
     */
    public String getTags(final String priority) {
        return owner.getTags(priority);
    }

    /**
     * Returns the total number of tasks in this project.
     *
     * @return total number of tasks in this project.
     */
    public int getNumberOfTasks() {
        return module.getNumberOfTasks();
    }

    /**
     * Returns the number of tasks with the specified priority in this project.
     *
     * @param  priority the priority
     *
     * @return the number of tasks with the specified priority in this project.
     */
    public int getNumberOfTasks(final String priority) {
        return module.getNumberOfTasks(priority);
    }

    /**
     * Generates a PNG image for high/normal/low distribution of a java package.
     *
     * @param request
     *            Stapler request
     * @param response
     *            Stapler response
     * @throws IOException
     *             in case of an error
     */
    public final void doPackageStatistics(final StaplerRequest request,
            final StaplerResponse response) throws IOException {
        if (ChartUtil.awtProblem) {
            response.sendRedirect2(request.getContextPath() + "/images/headless.png");
            return;
        }
        String packageName = request.getParameter("package");
        JavaPackage javaPackage = module.getPackage(packageName);
        Logger.getLogger(ModuleDetail.class.getName()).log(Level.WARNING, "Package name: " + packageName);
        ChartBuilder chartBuilder = new ChartBuilder();
        JFreeChart chart = chartBuilder.createHighNormalLowChart(
                javaPackage.getNumberOfTasks(Priority.HIGH),
                javaPackage.getNumberOfTasks(Priority.NORMAL),
                javaPackage.getNumberOfTasks(Priority.LOW), module.getTaskBound());
        ChartUtil.generateGraph(request, response, chart, 500, 20);
    }

    /**
     * Returns the dynamic result of the FindBugs analysis (detail page for a package).
     *
     * @param link the link to the source code
     * @param request
     *            Stapler request
     * @param response
     *            Stapler response
     * @return the dynamic result of the FindBugs analysis (detail page for a package).
     */
    public Object getDynamic(final String link, final StaplerRequest request, final StaplerResponse response) {
        String type = request.getParameter("type");
        Logger.getLogger(ModuleDetail.class.getName()).log(Level.INFO, "Link: " + link + ", type: " + type);
        if ("package".equals(type)) {
            return new PackageDetail(getOwner(), module.getPackage(link));
        }
        else {
            return new TaskDetail(owner.getOwner(), link);
        }
    }
}
