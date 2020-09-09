import it.jnrpe.engine.services.config.IConfigProvider;
import it.jnrpe.engine.services.config.IConfigSource;
import it.jnrpe.services.config.ini.IniJnrpeConfigProvider;
import it.jnrpe.services.config.xml.XmlJnrpeConfigProvider;
import it.jnrpe.services.config.yaml.YamlJnrpeConfigProvider;

module it.jnrpe.services.config {
  requires it.jnrpe.engine;
  requires org.yaml.snakeyaml;

  provides IConfigProvider with
      IniJnrpeConfigProvider,
      XmlJnrpeConfigProvider,
      YamlJnrpeConfigProvider;

  uses IConfigSource;

  exports it.jnrpe.services.config.yaml to
      org.yaml.snakeyaml;
}
