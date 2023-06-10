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

import static guru.nidi.graphviz.attribute.Rank.RankDir.LEFT_TO_RIGHT;
import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import guru.nidi.graphviz.attribute.Rank;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.actuate.beans.BeansEndpoint.BeanDescriptor;
import org.springframework.boot.actuate.beans.BeansEndpoint.BeansDescriptor;
import org.springframework.boot.actuate.beans.BeansEndpoint.ContextBeansDescriptor;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

@Service
public class BeansVizMvcHandler {
	public static final String IMAGE_SVG_VALUE = "image/svg+xml";
	private final BeansEndpoint beansEndpoint;
	
	public BeansVizMvcHandler(BeansEndpoint beansEndpoint) {
		this.beansEndpoint = beansEndpoint;
	}

	/*
			List<Object> info = beansEndpoint.invoke();
		MutableGraph graphs = mutGraph().setDirected();
		for (Object o : info) {
			Map<String, Object> context = (Map<String, Object>) o;
			MutableGraph graph = new MutableGraph().setDirected()
					.setLabel((String) context.get("context"));
			graphs.add(graph);
			List<Map<String, Object>> beans = (List<Map<String, Object>>) context
					.get("beans");
			for (Map<String, Object> bean : beans) {
				List<String> dependencies = (List<String>) bean.get("dependencies");
				if (!dependencies.isEmpty() || all) {
					MutableNode node = mutNode(shorten((String) bean.get("bean")));
					for (String dep : dependencies) {
						node.addLink(shorten(dep));
					}
					graph.add(node);
				}
			}
		}
	 */

	@SuppressWarnings("unchecked")
	ResponseEntity<String> beansviz(boolean all) {
		Map<String, ContextBeansDescriptor> context = beansEndpoint.beans().getContexts();
		MutableGraph graphs = mutGraph()
				.setDirected(true);
				//.graphAttrs()
				//.add(Rank.dir(Rank.RankDir.TOP_TO_BOTTOM));

		context.forEach((key, value) -> {

			//MutableGraph graph = new MutableGraph().setDirected(true)
			//		.setLabel((String) key.get("context"));
			MutableGraph graph = mutGraph(value.toString())
					.setDirected(true)
					.add(mutNode(value.toString())
					.add(Color.RED));
			graphs.add(graph);

			AtomicInteger counter = new AtomicInteger(0);
			Map<String, BeanDescriptor> beans = value.getBeans();
			beans.forEach((key2, value2) -> {

				//System.out.println(value2.getScope());
				//System.out.println(key2 + " : " + value2);
				//System.out.println(counter.incrementAndGet() + " " + value2.getType().getSimpleName());

				MutableGraph graph2 = mutGraph(value2.getType().getSimpleName())
						.setDirected(true)
						.add(mutNode(value2.getType().getSimpleName())
						.add(Color.RED));
				graphs.add(graph2);

				System.out.println();
				System.out.println("Dependencies for: " + value2.getType().getSimpleName());
				List<String> dependencies = Arrays.asList(value2.getDependencies());
				dependencies.stream().forEach(System.out::println);
				//dependencies.stream().
				/*
				if (!dependencies.isEmpty() || all) {
					MutableNode node = mutNode(shorten(value.toString()));
					for (String dep : dependencies) {
						node.addLink(shorten(dep));
					}
					graph.add(node);
				}

				 */

			});
		});
		/*
		for (Object o : info) {
			Map<String, Object> context = (Map<String, Object>) o;
			MutableGraph graph = new MutableGraph().setDirected()
					.setLabel((String) context.get("context"));
			graphs.add(graph);
			List<Map<String, Object>> beans = (List<Map<String, Object>>) context
					.get("beans");
			for (Map<String, Object> bean : beans) {
				List<String> dependencies = (List<String>) bean.get("dependencies");
				if (!dependencies.isEmpty() || all) {
					MutableNode node = mutNode(shorten((String) bean.get("bean")));
					for (String dep : dependencies) {
						node.addLink(shorten(dep));
					}
					graph.add(node);
				}
			}
		}
		*/
		String svg = "";
		synchronized (this) {
			try {
				svg = Graphviz.fromGraph(graphs).render(Format.SVG).toString();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		//System.out.println(svg);
		return ResponseEntity.status(HttpStatus.OK)
				.contentType(MediaType.valueOf(IMAGE_SVG_VALUE)).body(svg);
	}

	private String shorten(String name) {
		name = name.split("@")[0];
		if (name.contains(".")) {
			String[] split = name.split("\\.");
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < split.length - 1; i++) {
				builder.append(split[i].toLowerCase().charAt(0));
				builder.append(".");
			}
			builder.append(split[split.length - 1]);
			return builder.toString();
		}
		return name;
	}
}
