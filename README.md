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
                <version>0.8.1-patch01</version>
                <configuration>
                   <errors>
                      <error/>
                      ...
                   </errors>

                   <warnings>
                     <warning/>
                     ...
                   </warnings>

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

##### errors

List of rules that the tool uses. If the rule is matched, an error message is produced. Errors cause build failure. Errors tag has higher priority then warnings tag.

Available rules:

<table>
<tr><td>adjoining-classes</td><td>Don't use adjoining classes.</td></tr>
<tr><td>box-model</td><td>Don't use width or height when using padding or border.</td></tr>
<tr><td>box-sizing</td><td>The box-sizing properties isn't supported in IE6 and IE7.</td></tr>
<tr><td>compatible-vendor-prefixes</td><td>Include all compatible vendor prefixes to reach a wider range of users.</td></tr>
<tr><td>display-property-grouping</td><td>Certain properties shouldn't be used with certain display property values.</td></tr>
<tr><td>duplicate-properties</td><td>Duplicate properties must appear one after the other.</td></tr>
<tr><td>empty-rules</td><td>Rules without any properties specified should be removed.</td></tr>
<tr><td>errors</td><td>This rule looks for recoverable syntax errors.</td></tr>
<tr><td>floats</td><td>This rule tests if the float property is used too many times</td></tr>
<tr><td>font-faces</td><td>Too many different web fonts in the same stylesheet.</td></tr>
<tr><td>font-sizes</td><td>Checks the number of font-size declarations.</td></tr>
<tr><td>gradients</td><td>When using a vendor-prefixed gradient, make sure to use them all.</td></tr>
<tr><td>ids</td><td>Selectors should not contain IDs.</td></tr>
<tr><td>import</td><td>Don't use @import, use <link> instead.</td></tr>
<tr><td>important</td><td>Be careful when using !important declaration</td></tr>
<tr><td>known-properties</td><td>Properties should be known (listed in CSS specification) or be a vendor-prefixed property.</td></tr>
<tr><td>outline-none</td><td>Use of outline: none or outline: 0 should be limited to :focus rules.</td></tr>
<tr><td>overqualified-elements</td><td>Don't use classes or IDs with elements (a.foo or a#foo).</td></tr>
<tr><td>qualified-headings</td><td>Headings should not be qualified (namespaced).</td></tr>
<tr><td>regex-selectors</td><td>Selectors that look like regular expressions are slow and should be avoided.</td></tr>
<tr><td>rules-count</td><td>Track how many rules there are.</td></tr>
<tr><td>shorthand</td><td>Use shorthand properties where possible.</td></tr>
<tr><td>text-indent</td><td>Checks for text indent less than -99px</td></tr>
<tr><td>unique-headings</td><td>Headings should be defined only once.</td></tr>
<tr><td>universal-selector</td><td>The universal selector (*) is known to be slow.</td></tr>
<tr><td>vendor-prefix</td><td>When using a vendor-prefixed property, make sure to include the standard one.</td></tr>
<tr><td>zero-units</td><td>You don't need to specify units when a value is 0.</td></tr>
</table>

* Type: _List_
* Required: _No_

##### warnings

List of rules that the tool uses (if not specified all available rules are applied). If the rule is matched, a warning message is produced. Warnings don't cause build failure.

Available rules:

<table>
<tr><td>adjoining-classes</td><td>Don't use adjoining classes.</td></tr>
<tr><td>box-model</td><td>Don't use width or height when using padding or border.</td></tr>
<tr><td>box-sizing</td><td>The box-sizing properties isn't supported in IE6 and IE7.</td></tr>
<tr><td>compatible-vendor-prefixes</td><td>Include all compatible vendor prefixes to reach a wider range of users.</td></tr>
<tr><td>display-property-grouping</td><td>Certain properties shouldn't be used with certain display property values.</td></tr>
<tr><td>duplicate-properties</td><td>Duplicate properties must appear one after the other.</td></tr>
<tr><td>empty-rules</td><td>Rules without any properties specified should be removed.</td></tr>
<tr><td>errors</td><td>This rule looks for recoverable syntax errors.</td></tr>
<tr><td>floats</td><td>This rule tests if the float property is used too many times</td></tr>
<tr><td>font-faces</td><td>Too many different web fonts in the same stylesheet.</td></tr>
<tr><td>font-sizes</td><td>Checks the number of font-size declarations.</td></tr>
<tr><td>gradients</td><td>When using a vendor-prefixed gradient, make sure to use them all.</td></tr>
<tr><td>ids</td><td>Selectors should not contain IDs.</td></tr>
<tr><td>import</td><td>Don't use @import, use <link> instead.</td></tr>
<tr><td>important</td><td>Be careful when using !important declaration</td></tr>
<tr><td>known-properties</td><td>Properties should be known (listed in CSS specification) or be a vendor-prefixed property.</td></tr>
<tr><td>outline-none</td><td>Use of outline: none or outline: 0 should be limited to :focus rules.</td></tr>
<tr><td>overqualified-elements</td><td>Don't use classes or IDs with elements (a.foo or a#foo).</td></tr>
<tr><td>qualified-headings</td><td>Headings should not be qualified (namespaced).</td></tr>
<tr><td>regex-selectors</td><td>Selectors that look like regular expressions are slow and should be avoided.</td></tr>
<tr><td>rules-count</td><td>Track how many rules there are.</td></tr>
<tr><td>shorthand</td><td>Use shorthand properties where possible.</td></tr>
<tr><td>text-indent</td><td>Checks for text indent less than -99px</td></tr>
<tr><td>unique-headings</td><td>Headings should be defined only once.</td></tr>
<tr><td>universal-selector</td><td>The universal selector (*) is known to be slow.</td></tr>
<tr><td>vendor-prefix</td><td>When using a vendor-prefixed property, make sure to include the standard one.</td></tr>
<tr><td>zero-units</td><td>You don't need to specify units when a value is 0.</td></tr>
</table>

* Type: _List_
* Required: _No_

##### baseDirectory

The directory to scan. The directory used by 'includes' and 'excludes' options.

* Type: _List_
* Required: _No_
* Default: _${project.basedir}_

##### includes

List of includes patterns (Ant patterns). For more information about pattern's syntax check [documentation](http://ant.apache.org/manual/dirtasks.html#patterns).

* Type: _List_
* Required: _Yes_

##### excludes

List of excludes patterns (Ant patterns). For more information about pattern's syntax check [documentation](http://ant.apache.org/manual/dirtasks.html#patterns).

* Type: _List_
* Required: _No_

##### format

The output format.

Console formats:

<table>
<tr><td>text</td><td>the default format</td></tr>
<tr><td>compact</td><td>a more condensed output where each warning takes only one line of output</td></tr>
</table>

External file formats (the location of the file is `${project.build.directory}/csslint.xml`):

<table>
<tr><td>lint-xml</td><td>an XML format that can be consumed by other utilities</td></tr>
<tr><td>csslint-xml</td><td>same as lint-xml except the document element is &lt;csslint&gt;</td></tr>
<tr><td>checkstyle-xml</td><td>a format appropriate for consumption by Checkstyle</td></tr>
</table>

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
                <version>0.8.1-patch01</version>
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
