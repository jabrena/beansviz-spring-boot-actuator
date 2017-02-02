package am.ik.beansviz;

import org.springframework.boot.actuate.endpoint.mvc.AbstractMvcEndpoint;
import org.springframework.boot.actuate.endpoint.mvc.HypermediaDisabled;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@HypermediaDisabled
public class BeansVizMvcEndpoint extends AbstractMvcEndpoint {
	public final BeansVizMvcHandler beansVizMvcHandler;

	public BeansVizMvcEndpoint(BeansVizMvcHandler beansVizMvcHandler) {
		super("/beansviz", true);
		this.beansVizMvcHandler = beansVizMvcHandler;
	}

	@GetMapping(produces = BeansVizMvcHandler.IMAGE_SVG_VALUE)
	ResponseEntity<String> beansviz(
			@RequestParam(name = "all", defaultValue = "false") boolean all) {
		return beansVizMvcHandler.beansviz(all);
	}

}
