package com.lightbend.play.plugins;

import com.lightbend.play.extensions.Platform;
import com.lightbend.play.extensions.PlayExtension;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.internal.ConventionMapping;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.language.scala.internal.DefaultScalaPlatform;
import org.gradle.play.tasks.PlayCoffeeScriptCompile;
import org.gradle.play.tasks.RoutesCompile;
import org.gradle.plugins.ide.idea.GenerateIdeaModule;
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel;
import org.gradle.plugins.ide.idea.model.IdeaModule;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import static com.lightbend.play.plugins.PlayApplicationPlugin.PLAY_EXTENSION_NAME;
import static com.lightbend.play.plugins.PlayCoffeeScriptPlugin.COFFEESCRIPT_COMPILE_TASK_NAME;
import static com.lightbend.play.plugins.PlayRoutesPlugin.ROUTES_COMPILE_TASK_NAME;

public class PlayIdeaPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        GenerateIdeaModule ideaModule = (GenerateIdeaModule) project.getTasks().getByName("ideaModule");
        IdeaModule module = ideaModule.getModule();

        ConfigurationContainer configurations = project.getConfigurations();
        module.setScopes(buildScopes(configurations));
        ConventionMapping conventionMapping = conventionMappingFor(module);

        conventionMapping.map("sourceDirs", (Callable<Set<File>>) () -> {
            // TODO: Assets should probably be a source set too
            Set<File> sourceDirs = new HashSet<>();
            sourceDirs.add(new File(project.getProjectDir(), "public"));
            RoutesCompile routesCompile = (RoutesCompile) project.getTasks().getByName(ROUTES_COMPILE_TASK_NAME);
            sourceDirs.add(routesCompile.getOutputDirectory());
            PlayCoffeeScriptCompile playCoffeeScriptCompile = (PlayCoffeeScriptCompile) project.getTasks().getByName(COFFEESCRIPT_COMPILE_TASK_NAME);
            sourceDirs.add(playCoffeeScriptCompile.getDestinationDir());
            return Collections.unmodifiableSet(sourceDirs);
        });

        conventionMapping.map("testSourceDirs", (Callable<Set<File>>) () -> {
            // TODO: This should be modeled as a source set
            return Collections.singleton(new File(project.getBuildDir(), "test"));
        });

        SourceSet mainSourceSet = getMainSourceSet(project);

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
        ideaModule.dependsOn("classes");
    }

    private ConventionMapping conventionMappingFor(IdeaModule module) {
        return new DslObject(module).getConventionMapping();
    }

    private JavaVersion getTargetJavaVersion(Platform Platform) {
        return Platform.getJavaVersion().get();
    }

    private Map<String, Map<String, Collection<Configuration>>> buildScopes(ConfigurationContainer configurations) {
        Map<String, Map<String, Collection<Configuration>>> scopes = new HashMap<>();
        scopes.put("PROVIDED", buildScope());
        scopes.put("COMPILE", buildScope(configurations.getByName(PlayPluginConfigurations.COMPILE_CONFIGURATION)));
        scopes.put("RUNTIME", buildScope(configurations.getByName(PlayPluginConfigurations.RUN_CONFIGURATION)));
        scopes.put("TEST", buildScope(configurations.getByName(PlayPluginConfigurations.TEST_COMPILE_CONFIGURATION)));
        return Collections.unmodifiableMap(scopes);
    }

    private Map<String, Collection<Configuration>> buildScope() {
        return buildScope(null);
    }

    private Map<String, Collection<Configuration>> buildScope(Configuration plus) {
        Map<String, Collection<Configuration>> scopes = new HashMap<>();
        scopes.put("plus", plus==null ? Collections.emptyList() : Collections.singletonList(plus));
        scopes.put("minus", Collections.emptyList());
        return Collections.unmodifiableMap(scopes);
    }

    private static SourceSet getMainSourceSet(Project project) {
        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        return javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    }
}
