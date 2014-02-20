package it.jnrpe.plugins.test;

import org.codehaus.cargo.container.LocalContainer;
import org.codehaus.cargo.container.tomcat.Tomcat7xStandaloneLocalConfiguration;

public class Tomcat7xConf extends Tomcat7xStandaloneLocalConfiguration {

    public Tomcat7xConf(String dir) {
        super(dir);
    }

    @Override
    public void configure(LocalContainer container) {
        //super.configure(container);
    }

    public void realConfigure(LocalContainer container) {
        super.configure(container);
    }
}
