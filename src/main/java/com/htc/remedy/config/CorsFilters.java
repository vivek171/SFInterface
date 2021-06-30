package com.htc.remedy.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static com.htc.remedy.constants.SFInterfaceConstants.CLIENT_INSTANCE;

/**
 * Created by kvivek on 11/2/2017.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilters implements Filter {

    /*  @Autowired
      RedisTemplate redisTemplate;
      private static final Logger logger = LogManager.getLogger(CorsFilters.class);
  */
    public CorsFilters() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        /*long requestdate = new Date().getTime();*/
        HttpServletMutable httpServletMutable = new HttpServletMutable((HttpServletRequest) req, req.getParameterMap());

        // httpServletMutable.addHeader("atoken", SFInterfaceConstants.getaTokenValue());
        if (((HttpServletRequest) req).getHeader("access_token") != null)
            httpServletMutable.addHeader("Authorization", "Bearer " + ((HttpServletRequest) req).getHeader("access_token"));

        httpServletMutable.addHeader("XRequestId", UUID.randomUUID().toString());

        if (httpServletMutable.getRequestURI().endsWith("/auth/v1/token")) {
            httpServletMutable.addParameter(CLIENT_INSTANCE, ((HttpServletRequest) req).getHeader(CLIENT_INSTANCE));
        }


        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.addHeader("Access-Control-Allow-Headers", "origin,Origin,contentType,Content-Type,type,accept,x-requested-with,Authorization,username,password,atoken,Content,refresh_token,access_token,ClientInstance");
            response.addHeader("Access-Control-Max-Age", "3600");
            response.addHeader("Access-Control-Expose-Headers", "Cache-Control,Content-Language,Content-Length,Expires,Last-Modified,Pragma,Content-Type,TotalCount,RecordCount,PageNum,TotalPages");
            response.setStatus(HttpServletResponse.SC_OK);

        } else {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Headers", "origin,Origin,Content-Type,type,contentType,accept,x-requested-with,Authorization,username,password,atoken,Content,refresh_token,access_token,ClientInstance");
            response.setHeader("Access-Control-Request-Headers", "*");
            response.setHeader("Access-Control-Request-Method", "*");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.addHeader("Access-Control-Expose-Headers", "Cache-Control,Content-Language,Content-Length,Expires,Last-Modified,Pragma,Content-Type,TotalCount,RecordCount,PageNum,TotalPages");


            try {
                chain.doFilter(httpServletMutable, res);
            } finally {
              /*  long responsedate = new Date().getTime();
                User user = SFInterfaceServices.getUser(request);
                redisTemplate.convertAndSend("Logger", new Gson().toJson(new EndpointModel(request
                        .getRequestURI()
                        .substring(request.getContextPath().length() + "/V1/".length()), requestdate, responsedate, user.getUsername(), user.getUserID()))
                );*/
            }


        }

//        if(request.getMethod().equalsIgnoreCase("options")) {
//            response.setStatus(HttpServletResponse.SC_OK);
//        } else {
//            chain.doFilter(req, res);
//        }
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
