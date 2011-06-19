/**
 * Copyright (C) 2009-2011 the original author or authors.
 * See the notice.md file distributed with this work for additional
 * information regarding copyright ownership.
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
 */
package scalate

import org.fusesource.scalate.util.Logging
import java.util.concurrent.atomic.AtomicBoolean
import _root_.Website._
import org.fusesource.scalate._
import org.fusesource.scalamd.{MacroDefinition, Markdown}
import java.util.regex.Matcher
import org.fusesource.scalate.wikitext.Pygmentize

class Boot(engine: TemplateEngine) extends Logging {

  private var _initialised = new AtomicBoolean(false)

  def run: Unit = {
    if (_initialised.compareAndSet(false, true)) {

      def filter(m:Matcher):String = {
        val filter_name = m.group(1)
        val body = m.group(2)
        engine.filter(filter_name) match {
          case Some(filter)=>
            filter.filter(RenderContext(), body)
          case None=> "<div class=\"macro error\"><p>filter not found: %s</p><pre>%s</pre></div>".format(filter_name, body)
        }
      }
      
      def pygmentize(m:Matcher):String = Pygmentize.pygmentize(m.group(2), m.group(1))

      // add some macros to markdown.
      Markdown.macros :::= List(
        MacroDefinition("""\{filter::(.*?)\}(.*?)\{filter\}""", "s", filter, true),
        MacroDefinition("""\{pygmentize::(.*?)\}(.*?)\{pygmentize\}""", "s", pygmentize, true),
        MacroDefinition("""\{pygmentize\_and\_compare::(.*?)\}(.*?)\{pygmentize\_and\_compare\}""", "s", pygmentize, true),
        MacroDefinition("""\$\{project_version\}""", "", _ => project_version.toString, true),
        MacroDefinition("""\$\{project_name\}""", "", _ => project_name.toString, true),
        MacroDefinition("""\$\{project_id\}""", "", _ => project_id.toString, true),
        MacroDefinition("""\$\{project_issue_url\}""", "", _ => project_issue_url.toString, true),
        MacroDefinition("""\$\{website_base_url\}""", "", _ => website_base_url.toString, true)
      )

      for( ssp <- engine.filter("ssp"); md <- engine.filter("markdown") ) {
        engine.pipelines += "ssp.md"-> List(ssp, md)
        engine.pipelines += "ssp.markdown"-> List(ssp, md)
      }
      info("Bootstrapped website gen for: %s".format(project_name))
    }
  }
}