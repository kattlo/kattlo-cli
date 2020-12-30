package com.github.kattlo.util;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import com.github.kattlo.EntryCommand;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.experimental.UtilityClass;
import picocli.CommandLine.IVersionProvider;

/**
 * @author fabiojose
 */
@UtilityClass
public class VersionUtil {

    private static final IVersionProvider VERSION_PROVIDER =
        new VersionUtil.QuarkusVersionProvider();


    public static String appVersion() {

        try {
            return VERSION_PROVIDER.getVersion()[0];
        }catch(Exception e) {
            return "";
        }
    }

    @RegisterForReflection
    public static class QuarkusVersionProvider implements IVersionProvider {

        @Override
        public String[] getVersion() throws Exception {
            return new String[]{
                ConfigProvider.getConfig()
                    .getValue("quarkus.application.version", String.class)};
        }

    }

    @RegisterForReflection
    public static class ManifestVersionProvider implements IVersionProvider {
        public String[] getVersion() throws Exception {
            Enumeration<URL> resources = EntryCommand.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try {
                    Manifest manifest = new Manifest(url.openStream());
                    if (isApplicableManifest(manifest)) {
                        Attributes attr = manifest.getMainAttributes();
                        return new String[] { get(attr, "Implementation-Title") + " version \"" +
                                get(attr, "Implementation-Version") + "\"" };
                    }
                } catch (IOException ex) {
                    return new String[] { "Unable to read from " + url + ": " + ex };
                }
            }
            return new String[0];
        }

        private boolean isApplicableManifest(Manifest manifest) {
            Attributes attributes = manifest.getMainAttributes();
            return "kattlo".equals(get(attributes, "Implementation-Title"));
        }

        private static Object get(Attributes attributes, String key) {
            return attributes.get(new Attributes.Name(key));
        }
    }
}
