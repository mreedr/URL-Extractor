package org.knoesis.url.extractions;

import java.net.URL;
import java.util.Set;

/**
 * The standard implementation for this is the RegexExtractor
 * @author PabloMendes
 */
public interface URLExtractor extends Extractor {

	public Set<URL> execute(String text);
	
}
