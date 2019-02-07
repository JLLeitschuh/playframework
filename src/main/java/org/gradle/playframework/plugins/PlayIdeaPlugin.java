package org.gradle.playframework.plugins;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class PlayIdeaPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
       /*
        project.getTasks().named("ideaModule", GenerateIdeaModule.class, ideaModuleTask -> {
            IdeaModule module = ideaModuleTask.getModule();

            ConfigurationContainer configurations = project.getConfigurations();
            module.setScopes(buildScopes(configurations));
            ConventionMapping conventionMapping = conventionMappingFor(module);

            TaskProvider<Task> classesTask = project.getTasks().named(CLASSES_TASK_NAME);
            TaskProvider<JavaScriptMinify> javaScriptMinifyTask = project.getTasks().named(PlayJavaScriptPlugin.JS_MINIFY_TASK_NAME, JavaScriptMinify.class);
            SourceSet mainSourceSet = getMainJavaSourceSet(project);

            conventionMapping.map("sourceDirs", (Callable<Set<File>>) () -> {
                // TODO: Assets should probably be a source set too
                Set<File> sourceDirs = new HashSet<>();
                sourceDirs.add(new File(project.getProjectDir(), "public"));

                SourceDirectorySet scalaSourceDirectorySet = getScalaSourceDirectorySet(project);
                sourceDirs.addAll(scalaSourceDirectorySet.getSrcDirs());
                sourceDirs.add(javaScriptMinifyTask.get().getDestinationDir().get().getAsFile());
                return Collections.unmodifiableSet(sourceDirs);
            });

            conventionMapping.map("testSourceDirs", (Callable<Set<File>>) () -> {
                // TODO: This should be modeled as a source set
                return Collections.singleton(new File(project.getProjectDir(), "test"));
            });

            conventionMapping.map("singleEntryLibraries", (Callable<Map<String, Iterable<File>>>) () -> {
                Map<String, Iterable<File>> libs = new HashMap<>();
                libs.put("COMPILE", mainSourceSet.getOutput().getClassesDirs());
                libs.put("RUNTIME", Collections.singleton(mainSourceSet.getOutput().getResourcesDir()));
                // TODO: This should be modeled as a source set
                libs.put("TEST", Collections.singleton(new File(project.getBuildDir(), "testClasses")));
                return Collections.unmodifiableMap(libs);
            });

            PlayExtension playExtension = (PlayExtension) project.getExtensions().getByName(PLAY_EXTENSION_NAME);
            module.setScalaPlatform(new DefaultScalaPlatform(playExtension.getPlatform().getScalaVersion().get()));

            conventionMapping.map("targetBytecodeVersion", (Callable<JavaVersion>) () -> getTargetJavaVersion(playExtension.getPlatform()));
            conventionMapping.map("languageLevel", (Callable<IdeaLanguageLevel>) () -> new IdeaLanguageLevel(getTargetJavaVersion(playExtension.getPlatform())));

            ideaModuleTask.dependsOn(classesTask);
            ideaModuleTask.dependsOn(javaScriptMinifyTask);
        });
*/
    }
//
//    private ConventionMapping conventionMappingFor(IdeaModule module) {
//        return new DslObject(module).getConventionMapping();
//    }
//
//    private JavaVersion getTargetJavaVersion(PlayPlatform Platform) {
//        return Platform.getJavaVersion().get();
//    }
//
//    private Map<String, Map<String, Collection<Configuration>>> buildScopes(ConfigurationContainer configurations) {
//        Map<String, Map<String, Collection<Configuration>>> scopes = new HashMap<>();
//        scopes.put("PROVIDED", buildScope());
//        scopes.put("COMPILE", buildScope(configurations.getByName(COMPILE_CONFIGURATION)));
//        scopes.put("RUNTIME", buildScope(configurations.getByName(RUN_CONFIGURATION)));
//        scopes.put("TEST", buildScope(configurations.getByName(TEST_COMPILE_CONFIGURATION)));
//        return Collections.unmodifiableMap(scopes);
//    }
//
//    private Map<String, Collection<Configuration>> buildScope() {
//        return buildScope(null);
//    }
//
//    private Map<String, Collection<Configuration>> buildScope(Configuration plus) {
//        Map<String, Collection<Configuration>> scopes = new HashMap<>();
//        scopes.put("plus", plus==null ? Collections.emptyList() : Collections.singletonList(plus));
//        scopes.put("minus", Collections.emptyList());
//        return Collections.unmodifiableMap(scopes);
//    }
}
