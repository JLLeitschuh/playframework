package com.lightbend.play.extensions;

import com.lightbend.play.platform.DefaultJavaPlatform;
import com.lightbend.play.platform.DefaultPlayPlatform;
import com.lightbend.play.platform.DefaultScalaPlatform;
import com.lightbend.play.platform.JavaPlatform;
import com.lightbend.play.platform.PlayPlatform;
import com.lightbend.play.platform.ScalaPlatform;
import org.gradle.api.JavaVersion;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

public class Platform {

    private final Property<String> playVersion;
    private final Property<String> scalaVersion;
    private final Property<JavaVersion> javaVersion;

    @Inject
    public Platform(ObjectFactory objectFactory) {
        playVersion = objectFactory.property(String.class);
        scalaVersion = objectFactory.property(String.class);
        javaVersion = objectFactory.property(JavaVersion.class);
    }

    public Property<String> getPlayVersion() {
        return playVersion;
    }

    public Property<String> getScalaVersion() {
        return scalaVersion;
    }

    public Property<JavaVersion> getJavaVersion() {
        return javaVersion;
    }

    public PlayPlatform asPlayPlatform() {
        ScalaPlatform scalaPlatform = new DefaultScalaPlatform(scalaVersion.get());
        JavaPlatform javaPlatform = new DefaultJavaPlatform(javaVersion.get());
        String name = createName(playVersion.get(), scalaVersion.get(), javaPlatform.getDisplayName());
        return new DefaultPlayPlatform(name, playVersion.get(), scalaPlatform, javaPlatform);
    }

    private String createName(String playVersion, String scalaVersion, String javaVersion) {
        StringBuilder builder = new StringBuilder("play-");
        builder.append(playVersion);
        if (scalaVersion != null) {
            builder.append("-");
            builder.append(scalaVersion);
        }
        if (javaVersion != null) {
            builder.append("_");
            builder.append(javaVersion);
        }
        return builder.toString();
    }
}
