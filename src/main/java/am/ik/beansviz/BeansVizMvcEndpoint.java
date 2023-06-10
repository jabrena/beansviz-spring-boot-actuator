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

//import org.springframework.boot.actuate.endpoint.mvc.AbstractMvcEndpoint;
//import org.springframework.boot.actuate.endpoint.mvc.HypermediaDisabled;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController()
//@HypermediaDisabled
public class BeansVizMvcEndpoint  { // extends AbstractMvcEndpoint

	@Autowired
	private BeansVizMvcHandler2 beansVizMvcHandler;

	@Autowired
	private ApplicationContext applicationContext;

	@GetMapping(path= "/beansviz", produces = MediaType.APPLICATION_JSON_VALUE) //beansviz
	ResponseEntity<String> beansviz(
			@RequestParam(name = "all", defaultValue = "false") boolean all) {

		try {

			BeanDefinition beanDefinition = ((GenericApplicationContext) applicationContext)
					.getBeanFactory().getBeanDefinition("ObjectMapper");

			String resourceDescription = beanDefinition.getResourceDescription();
			System.out.println(resourceDescription);

			/*
			SpringJarLocator jarLocator = new SpringJarLocator(applicationContext);
			String beanName = "LogbackMetrics";
			File jarFile = jarLocator.getJarFileForBean(beanName);
			System.out.println("JAR file for bean '" + beanName + "': " + jarFile.getAbsolutePath());


			 */
			System.out.println("########");
		} catch (NoSuchBeanDefinitionException e) {
			System.out.println("Katakroker");
		}
		return beansVizMvcHandler.beansviz();
	}

}
