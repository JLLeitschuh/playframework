package com.lightbend.play.plugins;

import com.lightbend.play.extensions.PlayExtension;
import com.lightbend.play.sourcesets.DefaultRoutesSourceSet;
import com.lightbend.play.sourcesets.RoutesSourceSet;
import com.lightbend.play.tasks.RoutesCompile;
import com.lightbend.play.tools.routes.RoutesCompilerFactory;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.Provider;
import org.gradle.play.platform.PlayPlatform;

import java.util.ArrayList;

import static com.lightbend.play.plugins.PlayApplicationPlugin.PLAY_EXTENSION_NAME;
import static com.lightbend.play.plugins.PlayPluginHelper.createCustomSourceSet;

/**
 * Plugin for compiling Play routes sources in a Play application.
 */
public class PlayRoutesPlugin implements PlayGeneratedSourcePlugin {

    public static final String ROUTES_COMPILER_CONFIGURATION_NAME = "routesCompiler";
    public static final String ROUTES_COMPILE_TASK_NAME = "compilePlayRoutes";

    @Override
    public void apply(Project project) {
        PlayExtension playExtension = ((PlayExtension) project.getExtensions().getByName(PLAY_EXTENSION_NAME));
        PlayPlatform playPlatform = playExtension.getPlatform().asPlayPlatform();

        Configuration routesCompilerConfiguration = createRoutesCompilerConfiguration(project);
        declareDefaultDependencies(project, routesCompilerConfiguration, playPlatform);
        RoutesSourceSet routesSourceSet = createCustomSourceSet(project, DefaultRoutesSourceSet.class, "routes");
        createDefaultRoutesCompileTask(project, routesSourceSet.getRoutes(), routesCompilerConfiguration, playPlatform, playExtension.getInjectedRoutesGenerator());
    }

    private Configuration createRoutesCompilerConfiguration(Project project) {
        Configuration compilerConfiguration = project.getConfigurations().create(ROUTES_COMPILER_CONFIGURATION_NAME);
        compilerConfiguration.setVisible(false);
        compilerConfiguration.setTransitive(true);
        compilerConfiguration.setDescription("The routes compiler library used to generate Scala source from routes templates.");
        return compilerConfiguration;
    }

    private void declareDefaultDependencies(Project project, Configuration configuration, PlayPlatform playPlatform) {
        configuration.defaultDependencies(dependencies -> {
            String dependencyNotation = RoutesCompilerFactory.createAdapter(playPlatform).getDependencyNotation();
            dependencies.add(project.getDependencies().create(dependencyNotation));
        });
    }

    private RoutesCompile createDefaultRoutesCompileTask(Project project, SourceDirectorySet sourceDirectory, Configuration compilerConfiguration, PlayPlatform playPlatform, Provider<Boolean> injectedRoutesGenerator) {
        return project.getTasks().create(ROUTES_COMPILE_TASK_NAME, RoutesCompile.class, routesCompile -> {
            routesCompile.setDescription("Generates routes for the '" + sourceDirectory.getDisplayName() + "' source set.");
            routesCompile.setPlatform(playPlatform);
            routesCompile.setAdditionalImports(new ArrayList<>());
            routesCompile.setSource(sourceDirectory);
            routesCompile.setOutputDirectory(getOutputDir(project, sourceDirectory));
            routesCompile.setInjectedRoutesGenerator(injectedRoutesGenerator);
            routesCompile.setRoutesCompilerClasspath(compilerConfiguration);
        });
    }
}
