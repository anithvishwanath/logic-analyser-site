package logicanalyser;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Prevents users from accessing the dashboard pages without having uploaded a file
 */
@WebFilter("/view/*")
public class SessionFilter implements Filter {
	@Inject
	private FileSessionBean bean;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest rawRequest, ServletResponse rawResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)rawRequest;
		
		// If the session is all good, dont do anything
		if (bean.isValid()) {
			chain.doFilter(request, rawResponse);
			return;
		}
		
		// No valid session
		HttpServletResponse response = (HttpServletResponse)rawResponse;
		response.sendRedirect(request.getContextPath() + "/index.xhtml");
	}

	@Override
	public void destroy() {
	}
}
