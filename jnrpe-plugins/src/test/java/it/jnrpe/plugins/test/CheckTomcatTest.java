package it.jnrpe.plugins.test;

import it.jnrpe.ReturnValue;
import it.jnrpe.Status;
import it.jnrpe.client.JNRPEClient;
import it.jnrpe.commands.CommandDefinition;
import it.jnrpe.commands.CommandOption;
import it.jnrpe.commands.CommandRepository;
import it.jnrpe.plugins.PluginDefinition;
import it.jnrpe.utils.PluginRepositoryUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.installer.ZipURLInstaller;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.container.tomcat.TomcatPropertySet;
import org.codehaus.cargo.generic.DefaultContainerFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test for the checktomcat plugin.
 *
 * @author Massimiliano Ziccardi
 */
@Test
public class CheckTomcatTest implements Constants {

    /**
     * The port where tomcat listens.
     */
    private static final String TOMCAT_PORT = "7070";

    /**
     * The full path to the already existing tomcat installation.
     * If this variable is null, than tomcat gets automatically
     * downloaded during test execution phase.
     */
    private String existingTomcatInstance = null;

    /**
     * The tomcat download url.
     */
    private static final String TOMCAT_DOWNLOAD_URL =
            "https://archive.apache.org/dist/tomcat/"
            + "tomcat-7/v7.0.40/bin/apache-tomcat-7.0.40.zip";

    /**
     * The tomcat container.
     */
    private InstalledLocalContainer container = null;


    /**
     * running single unit test.
     */
    private boolean single = false;


    /**
     * Writes the tomcat-users.xml giving all the privileges to the user
     * tomcat/tomcat.
     *
     * @param f
     *            The destination file
     * @throws IOException
     *             -
     */
    private void enableTomcatConsole(final File f) throws IOException {
        String xml =
                "<?xml version='1.0' encoding='utf-8'?>"
                        + "<tomcat-users>"
                        + "<role rolename=\"manager-gui\"/>"
                        + "<role rolename=\"manager-script\"/>"
                        + "<role rolename=\"manager-jmx\"/>"
                        + "<role rolename=\"manager-status\"/>"
                        + "<user username=\"tomcat\" password=\"tomcat\" "
                        + "roles=\"manager-gui,manager-script,manager-jmx,"
                        + "manager-status\"/>"
                        + "</tomcat-users>";

        FileUtils.writeStringToFile(f, xml);
    }

    /**
     * Starts tomcat.
     */
    @BeforeClass
    public final void setup() {
        try {
            if (SetupTest.getPluginRepository() == null){
                SetupTest.setUp();
                this.single = true;
            }

            ClassLoader cl = CheckTomcatTest.class.getClassLoader();

            PluginDefinition checkTomcat =
                    PluginRepositoryUtil.parseXmlPluginDefinition(cl,
                            cl.getResourceAsStream("check_tomcat_plugin.xml"));

            SetupTest.getPluginRepository().addPluginDefinition(checkTomcat);


            String tomcatHome = null;

            if (existingTomcatInstance == null) {
                File tomcatDir = new File("./target/tomcat");

                tomcatDir.mkdirs();

                ZipURLInstaller installer =
                        new ZipURLInstaller(new URL(TOMCAT_DOWNLOAD_URL));
                installer.setExtractDir(tomcatDir.getAbsolutePath());
                installer.install();
                tomcatHome = installer.getHome();
            } else {
                tomcatHome = existingTomcatInstance;
            }

            DefaultConfigurationFactory factory =
                    new DefaultConfigurationFactory();
            factory.registerConfiguration("tomcat7xJnrpeTest",
                    ContainerType.INSTALLED,
                    ConfigurationType.STANDALONE, Tomcat7xConf.class);

            LocalConfiguration configuration =
                    (LocalConfiguration) factory
                            .createConfiguration(
                                    "tomcat7xJnrpeTest",
                                    ContainerType.INSTALLED,
                                    ConfigurationType.STANDALONE);

            configuration.setProperty("cargo.servlet.port", "7070");
            configuration.setProperty(TomcatPropertySet.AJP_PORT, "7009");

            container =
                    (InstalledLocalContainer) new DefaultContainerFactory()
                            .createContainer(
                                    "tomcat7x", ContainerType.INSTALLED,
                                    configuration);

            container.setHome(tomcatHome);

            ((Tomcat7xConf) configuration).realConfigure(container);
            enableTomcatConsole(new File(new File(configuration.getHome(),
                    "conf"), "tomcat-users.xml"));

            container.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Executes the test.
     * @throws Exception -
     */
    @Test
    public final void checkTomcatOK() throws Exception {
        CommandRepository cr = SetupTest.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_TOMCAT",
                "CHECK_TOMCAT")
                .addArgument(new CommandOption("hostname", "$ARG1$"))
                .addArgument(new CommandOption("port", "$ARG2$"))
                .addArgument(new CommandOption("username", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                );

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret =
                client.sendCommand("CHECK_TOMCAT",
                        "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat");

        Assert.assertEquals(ret.getStatus(), Status.OK, ret.getMessage());
    }

    /**
     * check if 1 or more of threads are available, assert status is warning
     * @throws Exception
     * void
     */
    public final void checkTomcatThreadsWarning() throws Exception {
        CommandRepository cr = SetupTest.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_THREAD_WARN", "CHECK_TOMCAT")
                .addArgument(new CommandOption("threads"))
                .addArgument(new CommandOption("hostname", "$ARG1$"))
                .addArgument(new CommandOption("port", "$ARG2$"))
                .addArgument(new CommandOption("username", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                .addArgument(new CommandOption("warning", "$ARG5$"))
                );

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_THREAD_WARN", "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat", "1:");

        Assert.assertEquals(ret.getStatus(), Status.WARNING, ret.getMessage());
    }


    /**
     * check 1 or more threads are available, assert status is critical
     * @throws Exception
     * void
     */
    public final void checkTomcatThreadsCritical() throws Exception {
        try{
            CommandRepository cr = SetupTest.getCommandRepository();

            cr.addCommandDefinition(new CommandDefinition("CHECK_THREAD_CRIT", "CHECK_TOMCAT")
                    .addArgument(new CommandOption("threads"))
                    .addArgument(new CommandOption("hostname", "$ARG1$"))
                    .addArgument(new CommandOption("port", "$ARG2$"))
                    .addArgument(new CommandOption("username", "$ARG3$"))
                    .addArgument(new CommandOption("password", "$ARG4$"))
                    .addArgument(new CommandOption("critical", "$ARG5$"))
                    );

            JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
            ReturnValue ret = client.sendCommand("CHECK_THREAD_CRIT", "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat", "1:");

            Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * check if 99.9%  or less of maximum threads are available, assert status is warning
     * @throws Exception
     * void
     */
    public final void checkTomcatThreadsWarningPercentage() throws Exception {
        CommandRepository cr = SetupTest.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_THREAD_PERC_WARN", "CHECK_TOMCAT")
                .addArgument(new CommandOption("threads"))
                .addArgument(new CommandOption("hostname", "$ARG1$"))
                .addArgument(new CommandOption("port", "$ARG2$"))
                .addArgument(new CommandOption("username", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                .addArgument(new CommandOption("warning", "$ARG5$"))
                );

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_THREAD_PERC_WARN", "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat", ":99.9%");

        Assert.assertEquals(ret.getStatus(), Status.WARNING, ret.getMessage());
    }

    /**
     * check if 99.9% or less of maximum threads are available, assert status is critical
     * @throws Exception
     * void
     */
    public final void checkTomcatThreadsCriticalPercentage() throws Exception {
        CommandRepository cr = SetupTest.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_THREAD_PERC_CRIT", "CHECK_TOMCAT")
                .addArgument(new CommandOption("threads"))
                .addArgument(new CommandOption("hostname", "$ARG1$"))
                .addArgument(new CommandOption("port", "$ARG2$"))
                .addArgument(new CommandOption("username", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                .addArgument(new CommandOption("critical", "$ARG5$"))
                );

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_THREAD_PERC_CRIT", "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat", ":99.9%");

        Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    }


    public final void checkTomcatMemoryWarningPercentage() throws Exception {
        CommandRepository cr = SetupTest.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_MEM_PERC_WARNING", "CHECK_TOMCAT")
                .addArgument(new CommandOption("memory"))
                .addArgument(new CommandOption("hostname", "$ARG1$"))
                .addArgument(new CommandOption("port", "$ARG2$"))
                .addArgument(new CommandOption("username", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                .addArgument(new CommandOption("warning", "$ARG5$"))
                );

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_MEM_PERC_WARNING", "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat", ":99.9%");

        Assert.assertEquals(ret.getStatus(), Status.WARNING, ret.getMessage());
    }

    /**
     * check if less than 99.9% of maximum memory is avaliable, assert status is critical
     * @throws Exception
     * void
     */
    public final void checkTomcatMemoryCriticalPercentage() throws Exception {
        CommandRepository cr = SetupTest.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_MEM_PERC_WARNING", "CHECK_TOMCAT")
                .addArgument(new CommandOption("memory"))
                .addArgument(new CommandOption("hostname", "$ARG1$"))
                .addArgument(new CommandOption("port", "$ARG2$"))
                .addArgument(new CommandOption("username", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                .addArgument(new CommandOption("critical", "$ARG5$"))
                );

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_MEM_PERC_WARNING", "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat", ":99.9%");

        Assert.assertEquals(ret.getStatus(), Status.CRITICAL, ret.getMessage());
    }
    /**
     * Check 50% or less of memory is available
     * @throws Exception
     * void
     */
    public final void checkTomcatMemoryOKPercentage() throws Exception {
        CommandRepository cr = SetupTest.getCommandRepository();

        cr.addCommandDefinition(new CommandDefinition("CHECK_MEM_PERC_OK", "CHECK_TOMCAT")
                .addArgument(new CommandOption("memory"))
                .addArgument(new CommandOption("hostname", "$ARG1$"))
                .addArgument(new CommandOption("port", "$ARG2$"))
                .addArgument(new CommandOption("username", "$ARG3$"))
                .addArgument(new CommandOption("password", "$ARG4$"))
                .addArgument(new CommandOption("critical", "$ARG5$"))
                );

        JNRPEClient client = new JNRPEClient(BIND_ADDRESS, JNRPE_PORT, false);
        ReturnValue ret = client.sendCommand("CHECK_MEM_PERC_OK", "127.0.0.1", TOMCAT_PORT, "tomcat", "tomcat", ":50%");

        Assert.assertEquals(ret.getStatus(), Status.OK, ret.getMessage());
    }


    /**
     * Stop tomcat.
     */
    @AfterClass
    public final void tearDown() throws Exception {
        if (container != null) {
            container.stop();
        }
        if (single){
            SetupTest.shutDown();
        }
    }

}
