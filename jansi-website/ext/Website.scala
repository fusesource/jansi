/**
 * Copyright (C) 2009-2017 the original author(s).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.fusesource.scalate.RenderContext

package

/**
 * <p>
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
object Website {

  val project_name= "Jansi"
  val project_slogan= "Eliminating boring console output."
  val project_id= "jansi"
  val project_jira_key= "JANSI"
  val project_issue_url= "https://github.com/fusesource/jansi/issues"
  val project_forums_url= "http://groups.google.com/group/jansi"
  val project_wiki_url= "https://github.com/fusesource/jansi/wiki"
  val project_logo= "/images/project-logo.png"
  val project_version= "1.12"
  val project_snapshot_version= "1.13-SNAPSHOT"
  val project_versions = List(
        project_version,
        "1.12",
        "1.11",
        "1.10",
        "1.9",
        "1.8",
        "1.6",
        "1.5",
        "1.4",
        "1.3",
        "1.2",
        "1.1",
        "1.0"
        )  

  val project_keywords= "jansi,java,ansi,console,color"

  // -------------------------------------------------------------------
  val github_page= "http://github.com/fusesource/jansi"
  val git_user_url= "git://github.com/fusesource/jansi.git"
  val git_commiter_url= "git@github.com:fusesources/jansi.git"

  val project_maven_groupId= "org.fusesource.jansi"
  val project_maven_artifactId= "jansi"

  val website_base_url= "http://fusesource.github.io/jansi"
}