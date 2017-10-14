# TCK

Running the TCK
---------------

Please add the following to your settings.xml to enable the CDI RI to be sucked down from JBoss.

```xml
<profiles>
        <profile>
            <id>jboss-public-repository</id>
            <repositories>
                <repository>
                    <id>jboss-public-repository-group</id>
                    <name>JBoss Public Maven Repository Group</name>
                    <url>https://repository.jboss.org/nexus/content/groups/public-jboss/</url>
                    <layout>default</layout>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                    </snapshots>
                </repository>
            </repositories>
            <pluginRepositories>
                <pluginRepository>
                    <id>jboss-public-repository-group</id>
                    <name>JBoss Public Maven Repository Group</name>
                    <url>https://repository.jboss.org/nexus/content/groups/public-jboss/</url>
                    <layout>default</layout>
                    <releases>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                        <updatePolicy>never</updatePolicy>
                    </snapshots>
                </pluginRepository>
            </pluginRepositories>
        </profile>
</profiles>
```

Testing Implementions of JSR107
-------------------------------

See the [TCK User Guide](https://docs.google.com/document/d/1w3Ugj_oEqjMlhpCkGQOZkd9iPf955ZWHAVdZzEwYYdU/edit?usp=sharing)
for instructions on how to use this TCK.
