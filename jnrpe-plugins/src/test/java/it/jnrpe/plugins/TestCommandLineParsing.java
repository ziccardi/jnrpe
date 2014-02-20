package it.jnrpe.plugins;

import it.jnrpe.utils.PluginRepositoryUtil;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestCommandLineParsing {
    @Test
    public void testNoArgumentsOption() throws Exception {
        ClassLoader cl = TestCommandLineParsing.class.getClassLoader();
        PluginDefinition pluginDef =
                PluginRepositoryUtil.parseXmlPluginDefinition(cl,
                        cl.getResourceAsStream("check_mysql_plugin.xml"));

        GroupBuilder gBuilder = new GroupBuilder();

        for (PluginOption po : pluginDef.getOptions()) {
            gBuilder = gBuilder.withOption(po.toOption());
        }

        Group group = gBuilder.create();
        Parser p = new Parser();
        p.setGroup(group);
        CommandLine cli =
                p.parse(new String[] { "--hostname", "$ARG1$", "--port",
                        "$ARG2$", "--database", "$ARG3$", "--user", "$ARG4$",
                        "--password", "$ARG5$", "--check-slave" });

        Assert.assertTrue(cli.hasOption("--check-slave"));
    }
}
