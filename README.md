# CXF XJC hashCode and equals plugin

A plugin for CXF codegen plugin that adds `#hashCode` and `#equals` implementations with zero runtime dependencies for the generated classes.

If you are looking for a library that generates `#toString`, we recommend you to take a look at [Apache CXF XJC Utils](https://github.com/apache/cxf-xjc-utils/tree/master/ts). It **does** have a dependency, but it's Apache Commons Lang3, which is commonly used anyway.


## Usage

Example profile in `pom.xml`:
```xml
    <profiles>
        <profile>
            <id>generate</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.apache.cxf</groupId>
                        <artifactId>cxf-codegen-plugin</artifactId>
                        <version>${cxf.version}</version>
                        <executions>
                            <execution>
                                <id>generate-sources</id>
                                <phase>generate-sources</phase>
                                <goals>
                                    <goal>wsdl2java</goal>
                                </goals>
                                <configuration>
                                    <sourceRoot>${basedir}/src/main/java</sourceRoot>
                                    <wsdlOptions>
                                        <wsdlOption>
                                            <wsdl>...</wsdl>
                                            <extraargs>
                                                <extraarg>-xjc-Xhashcode-equals</extraarg>
                                            </extraargs>
                                        </wsdlOption>
                                    </wsdlOptions>
                                </configuration>
                            </execution>
                        </executions>
                        <dependencies>
                            <dependency>
                                <groupId>com.viskan</groupId>
                                <artifactId>cxf-xjc-hce</artifactId>
                                <version>1.0.0</version>
                            </dependency>
                        </dependencies>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
```

And generate as usual:

```shell
$ mvn generate-sources -Pgenerate
```


## Releasing

Majors and minors:

```shell
$ mvn versions:set versions:set-scm-tag -DnewVersion=1.0.0 -DnewTag=v1.0.0 -DgenerateBackupPoms=false
$ git add pom.xml && commit -m "release: 1.0.0"
$ git tag v1.0.0
$ mvn deploy -Psonatype-oss-release
$ mvn versions:set versions:set-scm-tag -DnewVersion=1.1.0-SNAPSHOT -DnewTag=HEAD -DgenerateBackupPoms=false
$ git add pom.xml && commit -m "chore: Prepare for the next development iteration"
$ git push origin master && git push origin v1.0.0
```

Patches:

```shell
$ git checkout v1.0.0
$ git cherry-pick <Commit SHA of whatever commit you want to patch in>
$ mvn versions:set versions:set-scm-tag -DnewVersion=1.0.1 -DnewTag=v1.0.1 -DgenerateBackupPoms=false
$ git add pom.xml && commit -m "release: 1.0.1"
$ git tag v1.0.1
$ mvn deploy -Psonatype-oss-release
$ git push origin v1.0.1
```


## License

Apache License 2.0 © [Viskan System AB](http://www.viskan.com)
