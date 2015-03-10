package org.amaze.web.security.handlers;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
* Authentication entry point for REST services
*/
public final class AmazeAuthenticationEntryPoint implements AuthenticationEntryPoint
{
	@Override
	public void commence( HttpServletRequest request, HttpServletResponse response, AuthenticationException authException ) throws IOException
	{
		response.sendError( HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized" );
	}
}