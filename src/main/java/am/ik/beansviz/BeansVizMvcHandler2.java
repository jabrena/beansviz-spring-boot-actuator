/*
 * Copyright (C) 2017 Toshiaki Maki <makingx@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package am.ik.beansviz;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.actuate.beans.BeansEndpoint.BeanDescriptor;
import org.springframework.boot.actuate.beans.BeansEndpoint.ContextBeansDescriptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

@Service
public class BeansVizMvcHandler2 {

	@Autowired
	private BeansEndpoint beansEndpoint;

	//@SuppressWarnings("unchecked")
	ResponseEntity<String> beansviz() {

		StringBuilder result = new StringBuilder();

		Map<String, ContextBeansDescriptor> context = beansEndpoint.beans().getContexts();
		context.forEach((key, value) -> {

			result.append("{\n");
			result.append("    \"nodes\": [\n");
			result.append("        {\"id\": \"" + value.toString() + "\", \"group\": \"" + value + "\"},\n");

			List<String> nodes = new ArrayList<>();
			Map<String, BeanDescriptor> beans = value.getBeans();
			beans.forEach((key2, value2) -> {
				result.append("        {\"id\": \"" + value2.getType().getSimpleName() + "\", \"group\": \"" + value2.getType().getSimpleName() + "\"},\n");
				nodes.add(value2.getType().getSimpleName());
			});

			result.append("        {\"id\": \"" + "LAST_NODE" + "\", \"group\": \"" +  "LAST_NODE" + "\"}\n");
			result.append("    ],\n");
			result.append("    \"links\": [\n");

			Map<String, BeanDescriptor> beans2 = value.getBeans();
			beans2.forEach((key2, value2) -> {

				String source = value2.getType().getSimpleName();

				List<String> dependencies = Arrays.asList(value2.getDependencies());
				dependencies.stream().forEach(dep -> {
					var depParts = dep.split("\\.");
					String dependencyValue = (depParts.length > 0) ? depParts[depParts.length - 1] : dep;
					if(nodes.contains(dependencyValue)) {
						result.append("        {\"source\": \"" + source + "\", \"target\": \"" + dependencyValue + "\", \"value\": 1},\n");
					}
				});
			});

			result.append("        {\"source\": \"" + "LAST_NODE" + "\", \"target\": \"" + "LAST_NODE" + "\", \"value\": 1}\n");
			result.append("    ]\n");
			result.append("}\n");

		});

		return ResponseEntity
				.status(HttpStatus.OK)
				.contentType(MediaType.APPLICATION_JSON)
				.body(result.toString());
	}
}
