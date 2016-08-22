package com.push.filter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.push.util.Platform;

/**
 *统计数据过滤器
 */
public class OperFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public OperFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here

		// pass the request along the filter chain
		HttpServletRequest req = (HttpServletRequest) request;
		Pattern p = Platform.AndroidPattern;
		String userAgent = req.getHeader("User-Agent");//获取用户代理
		String Tags = req.getHeader("OS-Build-Tags");
		String ip = Platform.findIp(req);//分析用户ip地址
		try {
			if (userAgent != null) {
				Matcher m = p.matcher(userAgent);
				if (m.find()) {//是android用户代理统计
					Platform.log.info("Android;" + userAgent + ";operIp:" + ip + ";Tags：" + Tags);
					chain.doFilter(request, response);
				} else {//非android，直接退回
					Platform.log.info("not Android;" + userAgent + ";operIp:" + ip + ";Tags：" + Tags);
					response.getWriter().print("{\"flag\":0}");
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
