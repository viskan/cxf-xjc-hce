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
