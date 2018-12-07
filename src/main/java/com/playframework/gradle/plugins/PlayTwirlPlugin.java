package com.playframework.gradle.plugins;

import com.playframework.gradle.extensions.PlayExtension;
import com.playframework.gradle.extensions.PlayPluginConfigurations;
import com.playframework.gradle.sourcesets.internal.DefaultTwirlSourceSet;
import com.playframework.gradle.sourcesets.TwirlSourceSet;
import com.playframework.gradle.tasks.TwirlCompile;
import com.playframework.gradle.tools.twirl.TwirlCompilerFactory;
import com.playframework.gradle.tools.twirl.TwirlImports;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskProvider;

import java.util.List;

import static com.playframework.gradle.plugins.PlayApplicationPlugin.PLAY_CONFIGURATIONS_EXTENSION_NAME;
import static com.playframework.gradle.plugins.PlayApplicationPlugin.PLAY_EXTENSION_NAME;
import static com.playframework.gradle.plugins.PlayPluginHelper.createCustomSourceSet;

/**
 * Plugin for compiling Twirl sources in a Play application.
 */
public class PlayTwirlPlugin implements PlayGeneratedSourcePlugin {

    public static final String TWIRL_COMPILER_CONFIGURATION_NAME = "twirlCompiler";
    public static final String TWIRL_COMPILE_TASK_NAME = "compilePlayTwirlTemplates";

    @Override
    public void apply(Project project) {
        PlayExtension playExtension = (PlayExtension) project.getExtensions().getByName(PLAY_EXTENSION_NAME);
        PlayPluginConfigurations configurations = (PlayPluginConfigurations) project.getExtensions().getByName(PLAY_CONFIGURATIONS_EXTENSION_NAME);

        Configuration twirlCompilerConfiguration = createTwirlCompilerConfiguration(project);
        declareDefaultDependencies(project, twirlCompilerConfiguration, playExtension);
        TwirlSourceSet twirlSourceSet = createCustomSourceSet(project, DefaultTwirlSourceSet.class, "twirl");
        TaskProvider<TwirlCompile> twirlCompile = createDefaultTwirlCompileTask(project, twirlSourceSet, twirlCompilerConfiguration, playExtension);

        project.afterEvaluate(project1 -> {
            if (hasTwirlSourceSetsWithJavaImports(twirlCompile)) {
                configurations.getPlay().addDependency(playExtension.getPlatform().getDependencyNotation("play-java").get());
            }
        });
    }

    private Configuration createTwirlCompilerConfiguration(Project project) {
        Configuration twirlCompilerConfiguration = project.getConfigurations().create(TWIRL_COMPILER_CONFIGURATION_NAME);
        twirlCompilerConfiguration.setVisible(false);
        twirlCompilerConfiguration.setTransitive(true);
        twirlCompilerConfiguration.setDescription("The Twirl compiler library used to generate Scala source from Twirl templates.");
        return twirlCompilerConfiguration;
    }

    private void declareDefaultDependencies(Project project, Configuration configuration, PlayExtension playExtension) {
        configuration.defaultDependencies(dependencies -> {
            List<String> dependencyNotations = TwirlCompilerFactory.createAdapter(playExtension.getPlatform()).getDependencyNotation();

            for (String dependencyNotation : dependencyNotations) {
                dependencies.add(project.getDependencies().create(dependencyNotation));
            }
        });
    }

    private TaskProvider<TwirlCompile> createDefaultTwirlCompileTask(Project project, TwirlSourceSet twirlSourceSet, Configuration compilerConfiguration, PlayExtension playExtension) {
        return project.getTasks().register(TWIRL_COMPILE_TASK_NAME, TwirlCompile.class, twirlCompile -> {
            twirlCompile.setDescription("Compiles Twirl templates for the '" + twirlSourceSet.getTwirl().getDisplayName() + "' source set.");
            twirlCompile.getPlatform().set(project.provider(() -> playExtension.getPlatform()));
            twirlCompile.setSource(twirlSourceSet.getTwirl());
            twirlCompile.getOutputDirectory().set(getOutputDir(project, twirlSourceSet.getTwirl()));
            twirlCompile.getDefaultImports().set(twirlSourceSet.getDefaultImports());
            twirlCompile.getUserTemplateFormats().set(twirlSourceSet.getUserTemplateFormats());
            twirlCompile.getAdditionalImports().set(twirlSourceSet.getAdditionalImports());
            twirlCompile.getTwirlCompilerClasspath().setFrom(compilerConfiguration);
        });
    }

    private boolean hasTwirlSourceSetsWithJavaImports(TaskProvider<TwirlCompile> twirlCompile) {
        return twirlCompile.get().getDefaultImports().get() == TwirlImports.JAVA;
    }
}
