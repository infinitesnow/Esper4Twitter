package main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;

public class QueryParser {
	
	private static final Logger logger = LogManager.getLogger("AppLogger");
	
	private String query=null;
	public String getFormat() {
		return format;
	}
	
	private String format=null;
	public String getQuery() {
		return query;
	}
	
	private List<String> argumentNames=null;
	public List<String> getArgumentNames() {
		return argumentNames;
	}

	public QueryParser(String query) {
		logger.debug("Parsing query: " + query);
		try {
			logger.debug("Parsing format...");
			parseFormat(query);
		} catch (Exception e) {
			logger.warn("Could not find a valid format. Setting it to null.");
			logger.debug(Throwables.getStackTraceAsString(e));
			logger.warn("Trying query as-is");
			this.query=query;
			return;
		}
		logger.debug("Format parsing completed successfully");
		try {
			parseArgumentNames(query);
		} catch (Exception e) {
			// If no valid argument is found, FORMAT is ignored
			logger.warn("Could not find valid arguments. Setting format to null.");
			logger.debug(Throwables.getStackTraceAsString(e));
			this.format=null;
		}
		// Prepare query
		logger.trace("Preparing query for inserting.");
		this.query=query.replaceAll("FORMAT\\s+\\\".*?\\\"\\s+","");
		logger.debug("Parse OK.");
	}

	
	private void parseArgumentNames(String query) throws Exception{
		logger.debug("Parsing arguments...");
		// Get arguments
		String patternString="MYVALUE_\\w+";
		Pattern pattern = Pattern.compile(patternString);
		Matcher  matcher = pattern.matcher(query);
		matcher.find();
		this.argumentNames = new ArrayList<String>();
		do{
			 this.argumentNames.add(matcher.group());
		}while(matcher.find());
		// Print them if tracing enabled
		logger.trace("Arguments are: ");
		if(logger.isTraceEnabled()){
			for(String argumentName : argumentNames){
				logger.trace("Argument: " + argumentName);
			}
		}
		logger.debug("Arguments parsing completed successfully");
	}
	
	public void parseFormat(String query) throws Exception{
		// Create listener output header
		String patternString="FORMAT\\s+\\\"(.*?)\\\"\\s+";
		logger.trace("Searching for format with pattern string: " + patternString);
		Pattern pattern = Pattern.compile(patternString);
		Matcher  matcher = pattern.matcher(query);
		matcher.find();
		this.format = matcher.group(1);
		logger.debug("Found format: " + format);
	}
}
