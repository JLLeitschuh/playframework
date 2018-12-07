package com.playframework.gradle.tools.twirl;

import com.playframework.gradle.extensions.PlayPlatform;
import com.playframework.gradle.extensions.internal.PlayMajorVersion;

public class TwirlCompilerFactory {

    public static TwirlCompiler create(PlayPlatform playPlatform) {
        return new TwirlCompiler(createAdapter(playPlatform));
    }

    public static VersionedTwirlCompilerAdapter createAdapter(PlayPlatform playPlatform) {
        String playVersion = playPlatform.getPlayVersion().get();
        String scalaCompatibilityVersion = playPlatform.getScalaCompatibilityVersion().get();
        VersionedPlayTwirlAdapter playTwirlAdapter = createPlayTwirlAdapter(playPlatform);
        switch (PlayMajorVersion.forPlatform(playPlatform)) {
            case PLAY_2_3_X:
                return new TwirlCompilerAdapterV10X("1.0.4", scalaCompatibilityVersion, playTwirlAdapter);
            case PLAY_2_4_X:
            case PLAY_2_5_X:
                return new TwirlCompilerAdapterV10X("1.1.1", scalaCompatibilityVersion, playTwirlAdapter);
            case PLAY_2_6_X:
                return new TwirlCompilerAdapterV13X("1.3.13", scalaCompatibilityVersion, playTwirlAdapter);
            default:
                throw new RuntimeException("Could not create Twirl compile spec for Play version: " + playVersion);
        }
    }

    public static VersionedPlayTwirlAdapter createPlayTwirlAdapter(PlayPlatform playPlatform) {
        String playVersion = playPlatform.getPlayVersion().get();
        switch (PlayMajorVersion.forPlatform(playPlatform)) {
            case PLAY_2_3_X:
            case PLAY_2_4_X:
            case PLAY_2_5_X:
                return new PlayTwirlAdapterV23X();
            case PLAY_2_6_X:
                return new PlayTwirlAdapterV26X();
            default:
                throw new RuntimeException("Could not create Twirl adapter spec for Play version: " + playVersion);
        }
    }

}
