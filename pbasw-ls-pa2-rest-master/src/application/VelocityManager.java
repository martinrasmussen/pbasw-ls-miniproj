package application;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class VelocityManager {

    private VelocityEngine velocityEngine;

    public VelocityManager() {
        initializeVelocityEngine();
    }

    public Template getTemplate(String pathToTemplate) {
        return velocityEngine.getTemplate(pathToTemplate);
    }

    private void initializeVelocityEngine() {
        velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();
    }
}
