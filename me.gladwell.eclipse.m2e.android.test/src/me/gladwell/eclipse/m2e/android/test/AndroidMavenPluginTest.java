package me.gladwell.eclipse.m2e.android.test;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.android.ide.eclipse.adt.AdtConstants;

public class AndroidMavenPluginTest extends AndroidMavenPluginTestCase {

	private static final String SIMPLE_PROJECT_NAME = "simple-project";
	static final String ISSUE_6_PROJECT_NAME = "issue-6";

	public void testConfigureNonAndroidProject() throws Exception {
		deleteProject(SIMPLE_PROJECT_NAME);
		IProject project = importAndroidProject(SIMPLE_PROJECT_NAME);

	    assertFalse("configurer added android nature", project.hasNature(AdtConstants.NATURE_DEFAULT));
		IJavaProject javaProject = JavaCore.create(project);
		assertFalse("output location set to android value for non-android project", javaProject.getOutputLocation().toString().equals("/"+SIMPLE_PROJECT_NAME+"/target/android-classes"));

		for(IClasspathEntry entry : javaProject.getRawClasspath()) {
			assertFalse("classpath contains reference to gen directory", entry.getPath().toOSString().contains("gen"));
		}
	}

	public void testConfigureAddsWorkspaceProjectDepsToClasspath() throws Exception {
		importAndroidProject(SIMPLE_PROJECT_NAME);
		IProject project = importAndroidProject("test-project-workspace-deps");
		assertClasspathContains(JavaCore.create(project), SIMPLE_PROJECT_NAME);
	}

	public void testBuildDirectoryContainsCompiledClassesFromWorkspaceProject() throws Exception {
		importAndroidProject(SIMPLE_PROJECT_NAME);
		IProject project = importAndroidProject("test-project-workspace-deps");
		IJavaProject javaProject = JavaCore.create(project);
		File outputLocation = new File(ResourcesPlugin.getWorkspace().getRoot().getRawLocation().toOSString(), javaProject.getPath().toOSString());
		File classFile  = new File(outputLocation, "bin/classes/com/urbanmania/eclipse/maven/android/test/App.class");
		
		buildAndroidProject(project, IncrementalProjectBuilder.FULL_BUILD);

		assertTrue(classFile.exists());
	}
}
