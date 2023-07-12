import it.jnrpe.engine.services.config.IConfigProvider;
import it.jnrpe.engine.services.config.IConfigSource;
import it.jnrpe.services.config.ini.IniJnrpeConfigProvider;
import it.jnrpe.services.config.xml.XmlJnrpeConfigProvider;
import it.jnrpe.services.config.yaml.YamlJnrpeConfigProvider;

module it.jnrpe.services.config {
  requires it.jnrpe.engine;
  requires org.yaml.snakeyaml;
  requires java.xml;

  provides IConfigProvider with
      IniJnrpeConfigProvider,
      XmlJnrpeConfigProvider,
      YamlJnrpeConfigProvider;

  exports it.jnrpe.services.config.yaml to
      org.yaml.snakeyaml;

  opens it.jnrpe.services.config.yaml to
      org.yaml.snakeyaml;

  exports it.jnrpe.services.config.yaml.internal to
      org.yaml.snakeyaml;

  opens it.jnrpe.services.config.yaml.internal to
      org.yaml.snakeyaml;

  uses IConfigSource;
}
