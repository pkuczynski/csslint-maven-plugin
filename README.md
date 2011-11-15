# Maven CSS Lint plugin

The plugin has only one goal, called `check`. This action verifies all CSS files and fails a compilation process in case of an error.

_Note:_ Plugin isn't available in Maven central repository yet.

## Configuration

#### Schema

    <project>
       ...
       <build>
          ...
          <plugins>
             ...
             <plugin>
                <groupId>net.csslint</groupId>
                <artifactId>csslint-maven-plugin</artifactId>
                <version>0.7.0</version>
                <configuration>
                   <rules>
                      <rule/>
                      ...
                   </rules>

                   <baseDirectory/>

                   <includes>
                      <include/>
                      ...
                   </includes>

                   <excludes>
                      <exclude/>
                      ...
                   </excludes>

                   <format/>
                </configuration>
                <executions>
                   <execution>
                      <goals>
                         <goal>check</goal>
                      </goals>
                   </execution>
                </executions>
             </plugin>
             ...
          </plugins>
          ...
       </build>
       ...
    </project>

## Parameters

##### rules

List of rules that the tool uses (if not specified all available rules are applied). Available rules:

<table>
<tr><td><strong>ID</strong></td><td><strong>Description</strong></td></tr>
<tr><td>adjoining-classes</td><td>Don't use adjoining classes</td></tr>
<tr><td>box-model</td><td>Beware of broken box model</td></tr>
<tr><td>compatible-vendor-prefixes</td><td>Use compatible vendor prefixes</td></tr>
<tr><td>display-property-grouping</td><td>Use properties appropriate for <code>display</code></td></tr>
<tr><td>duplicate-properties</td><td>Avoid duplicate properties</td></tr>
<tr><td>empty-rules</td><td>Disallow empty rules</td></tr>
<tr><td>floats</td><td>Don't use too many floats</td></tr>
<tr><td>font-faces</td><td>Don't use too many web fonts</td></tr>
<tr><td>font-sizes</td><td>Don't use too many font sizes</td></tr>
<tr><td>gradients</td><td>Include all gradient definitions</td></tr>
<tr><td>ids</td><td>Don't use IDs</td></tr>
<tr><td>import</td><td>Avoid <code>@import</code></td></tr>
<tr><td>important</td><td>Disallow <code>!important</code></td></tr>
<tr><td>overqualified-elements</td><td>Don't use overqualified elements</td></tr>
<tr><td>qualified-headings</td><td>Don't qualify headings</td></tr>
<tr><td>regex-selectors</td><td>Don't use selectors that look like regexs</td></tr>
<tr><td>text-indent</td><td>Don't use negative <code>text-indent</code></td></tr>
<tr><td>unique-headings</td><td>Heading should only be defined once</td></tr>
<tr><td>vendor-prefix</td><td>Use vendor prefix properties correctly</td></tr>
<tr><td>zero-units</td><td>Don't use units for 0 values</td></tr>
<tr><td>known-properties</td><td>Require use of known properties</td></tr>
<tr><td>universal-selector</td><td>Disallow universal selector</td></tr>
</table>

* Type: _List_
* Required: _No_

##### baseDirectory

The directory to scan. The directory used by 'includes' and 'excludes' options.

* Type: _List_
* Required: _No_
* Default: _${project.basedir}_

##### includes

List of includes patterns (Ant patterns). For more information about pattern format check [documentation]:(http://ant.apache.org/manual/dirtasks.html#patterns).

* Type: _List_
* Required: _Yes_

##### excludes

List of excludes patterns (Ant patterns). For more information about pattern format check [documentation]:(http://ant.apache.org/manual/dirtasks.html#patterns).

* Type: _List_
* Required: _No_

##### format

The output format. Choose between `text` and `lint-xml`. `text` is console logger. `lint-xml` is XML file logger (the location of the file is `${project.build.directory}/csslint.xml`).

* Type: _String_
* Required: _No_
* Default: _text_

#### Additional information

* `check` goal is attached to `verify` phase;

## Usage

#### Verifying CSS files with all available rules

<p/>

##### Definition

    <project>
       ...
       <build>
          ...
          <plugins>
             ...
             <plugin>
                <groupId>net.csslint</groupId>
                <artifactId>csslint-maven-plugin</artifactId>
                <version>0.7.0</version>
                <configuration>
                   <includes>
                      <include>**/*.css</include>
                   </includes>
                </configuration>
                <executions>
                   <execution>
                      <goals>
                         <goal>check</goal>
                      </goals>
                   </execution>
                </executions>
             </plugin>
             ...
          </plugins>
          ...
       </build>
       ...
    </project>

##### Command

To check for CSS issues execute:

    mvn verify

## Contributors

### Creators

1. Tomasz Oponowicz

### Contributors

1. Piotr Kuczyński
