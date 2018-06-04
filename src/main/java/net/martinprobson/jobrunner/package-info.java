/**
 * Task Runner
 *
 * <dl>
 *     <dt><span class="strong">Introduction</span></dt><dd>
 * <p>This code allows the execution and monitoring of a group of dependent or independent tasks.</p>
 * <p></p>
 * <p>It was primarily written to support running Apache Hive HQL and Python spark code against a YARN managed Hadoop cluster, but the code supports plugin tasks that could execute any
 *kind of activity.</p>
 * <p></p>
 *<p>Task templating is also supported to allow the configuration of Task contents against different execution environments (unit test/production for example).</p>
 *     </dd>
 *<p></p>
 *     <dt><span class="strong">Features</span></dt><dd>
 *<ul>
 *<p>Supports the running of the following task types out of the box: -</p><p></p>
 *
 *<li>JDBC - SQL code that can be run against a standard JDBC connection. File extension `.sql`.</li>
 *<li>Hive HQL -  Apache Hive HQL. File extension `.hql`.</li>
 *<li>Apache Spark Python. File extension `.py`.</li>
 *<li>Apache Spark Jar - Execute a Jar using {@code spark-submit}. File extension `.jar`.</li>
 *<li>Dummy Task - Does nothing! - File Extensions `.dmy` and `.txt`.</li>
 *</ul><ul>
 *<li> Templating - Tasks can be templated to support running in different environments.</li>
 *<li>Plugin architecture - New tasks types can be added to the framework using dependency injection</li>
 *</ul></dd>
 * </dl>
 */
package net.martinprobson.jobrunner;
