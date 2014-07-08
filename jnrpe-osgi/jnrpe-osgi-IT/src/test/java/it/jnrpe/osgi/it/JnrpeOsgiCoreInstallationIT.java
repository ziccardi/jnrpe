/*******************************************************************************
 * Copyright (c) 2007, 2014 Massimiliano Ziccardi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package it.jnrpe.osgi.it;

import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;
import it.jnrpe.ReturnValue;
import it.jnrpe.client.JNRPEClient;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.google.common.io.Files;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class JnrpeOsgiCoreInstallationIT {

	@Inject
	BundleContext context;

	private File confDir;

	private Map<String, String> getInitialConfiguration() {
		Map<String, String> res = new HashMap<String, String>();
		res.put("bind_address", "127.0.0.1:5666");
		res.put("allow_address", "127.0.0.1");
		res.put("command.check_test", "TEST -t $ARG1$");

		return res;
	}

	/**
	 * Creates the JNRPE osgi config file (it.jnrpe.osgi.cfg( inside the
	 * specified directory ).
	 * 
	 * @param confDir
	 */
	private void createConfFile(final File confDir, final Map<String, String> conf)
			throws Exception {

		File confFile = new File(confDir, "it.jnrpe.osgi.cfg");
		confFile.deleteOnExit();

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(confFile);

			for (Entry<String, String> entry : conf.entrySet()) {
				pw.println(entry.getKey() + "=" + entry.getValue());
			}
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}

	private Option[] felix() {
		return options(
				systemProperty("logback.configurationFile").value(
						"file:" + PathUtils.getBaseDir()
								+ "/src/test/resources/logback.xml"),

				// systemProperty("osgi.console").value("6666"),
				systemProperty("felix.fileinstall.dir").value(
						confDir.getAbsolutePath()),
				systemProperty("felix.fileinstall.filter").value(".*\\.cfg"),
				systemProperty("felix.fileinstall.poll").value("1000"),
				systemProperty("felix.fileinstall.noInitialDelay")
						.value("true"),
				mavenBundle("net.sf.jnrpe", "jnrpe-plugins-osgi",
						"2.0.4-SNAPSHOT").startLevel(2),

				mavenBundle("net.sf.jnrpe", "jnrpe-osgi-core", "2.0.3"),

				mavenBundle("org.slf4j", "slf4j-api"),
				mavenBundle("ch.qos.logback", "logback-core"),
				mavenBundle("ch.qos.logback", "logback-classic"),

				// jcheck_nrpe dependencies...
				wrappedBundle(mavenBundle("net.sf.jnrpe", "jcheck_nrpe",
						"2.0.3")),
				wrappedBundle(mavenBundle("commons-lang", "commons-lang", "2.6")),
				wrappedBundle(mavenBundle("org.apache.mahout.commons",
						"commons-cli", "2.0-mahout")),
				wrappedBundle(mavenBundle("net.sf.jnrpe", "jnrpe-lib", "2.0.3")),
				// end of jcheck_nrpe dependencies

				junitBundles(),

				mavenBundle("org.apache.felix", "org.apache.felix.configadmin",
						"1.8.0").startLevel(1),
				mavenBundle("org.apache.felix", "org.apache.felix.fileinstall",
						"3.2.8").startLevel(2)

		);
	}

	private Option[] equinox() {
		return options(
				systemProperty("felix.fileinstall.dir").value(
						confDir.getAbsolutePath()),
				systemProperty("felix.fileinstall.filter").value(".*\\.cfg"),
				systemProperty("felix.fileinstall.poll").value("1000"),
				systemProperty("felix.fileinstall.noInitialDelay")
						.value("true"),

				mavenBundle("net.sf.jnrpe", "jnrpe-plugins-osgi",
						"2.0.4-SNAPSHOT").startLevel(2),

				mavenBundle("net.sf.jnrpe", "jnrpe-osgi-core", "2.0.4-SNAPSHOT"),

				mavenBundle("org.slf4j", "osgi-over-slf4j"),
				mavenBundle("org.slf4j", "slf4j-api"),
				mavenBundle("ch.qos.logback", "logback-core"),
				mavenBundle("ch.qos.logback", "logback-classic"),

				mavenBundle("org.eclipse.equinox", "cm", "3.2.0-v20070116")
						.startLevel(1),
				mavenBundle("org.apache.felix", "org.apache.felix.fileinstall",
						"3.2.8").startLevel(2),
				mavenBundle("org.eclipse.equinox", "log", "1.0.100-v20070226")
						.startLevel(1),
				// jcheck_nrpe dependencies...
				wrappedBundle(mavenBundle("net.sf.jnrpe", "jcheck_nrpe",
						"2.0.3")),
				wrappedBundle(mavenBundle("commons-lang", "commons-lang", "2.6")),
				wrappedBundle(mavenBundle("org.apache.mahout.commons",
						"commons-cli", "2.0-mahout")),
				wrappedBundle(mavenBundle("net.sf.jnrpe", "jnrpe-lib", "2.0.3")),
				// end of jcheck_nrpe dependencies

				junitBundles());
	}

	@Configuration
	public Option[] config() throws Exception {

		confDir = Files.createTempDir();

		createConfFile(confDir, getInitialConfiguration());

		return felix();
		// return equinox();
	}

	@Test
	public void checkInject() {
		Assert.assertNotNull(context);
	}

	@Test
	public void checkReloadConfiguration() throws Exception {
		Map<String, String> conf = getInitialConfiguration();
		conf.put("allow_address", "127.0.0.1,10.10.10.1");
		createConfFile(confDir, conf);

		// Wait for JNRPE to reload the configuration...
		Thread.sleep(3000);

		conf.put("allow_address", "127.0.0.1,10.10.10.1");
		createConfFile(confDir, conf);

		// Wait for JNRPE to reload the configuration...
		Thread.sleep(3000);

		Assert.assertTrue("Configuration reload failed...",
				isActive("jnrpe-osgi-core"));
	}

	/**
	 * This test checks that the plugins bundle has been correctly loaded. To
	 * perform the test, the chec_test command is invoked.
	 * 
	 * @throws Exception
	 */
	@Test
	public void checkPluginsLoaded() throws Exception {

		JNRPEClient client = new JNRPEClient("127.0.0.1", 5666, false);
		ReturnValue rv = client.sendCommand("check_test", "OSGI test");

		Assert.assertEquals("TEST : OSGI test", rv.getMessage());
	}

	private boolean isActive(String bundleName) {
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			if (bundle != null) {
				if (bundle.getSymbolicName().equals(bundleName)) {
					if (bundle.getState() == Bundle.ACTIVE) {
						return true;
					}
				}
			}
		}

		return false;
	}

	private void checkBundle(String bundleName) {
		Boolean found = Boolean.FALSE;
		Boolean active = Boolean.FALSE;
		Bundle[] bundles = context.getBundles();
		for (Bundle bundle : bundles) {
			if (bundle != null) {
				if (bundle.getSymbolicName().equals(bundleName)) {
					found = Boolean.TRUE;
					if (bundle.getState() == Bundle.ACTIVE) {
						active = Boolean.TRUE;
					}
				}
			}
		}
		Assert.assertTrue(bundleName + " not found", found);
		Assert.assertTrue(bundleName + " not active", active);
	}

	/**
	 * This test checks that the core bundle is active.
	 */
	@Test
	public void checkJnrpeOsgiCoreBundle() {
		checkBundle("jnrpe-osgi-core");
	}

	/**
	 * This test checks that the plugin bundle is active.
	 */
	@Test
	public void checkJnrpePluginsOsgiBundle() {
		checkBundle("jnrpe-plugins-osgi");
	}

}
