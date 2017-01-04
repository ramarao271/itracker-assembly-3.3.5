/*
 * This software was designed and created by Jason Carroll.
 * Copyright (c) 2002, 2003, 2004 Jason Carroll.
 * The author can be reached at jcarroll@cowsultants.com
 * ITracker website: http://www.cowsultants.com
 * ITracker forums: http://www.cowsultants.com/phpBB/index.php
 *
 * This program is free software; you can redistribute it and/or modify
 * it only under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.itracker.web.filters;

import javax.servlet.*;
import java.io.IOException;


/**
 * This class will set the chracter encoding of each request that uses the filter.  It
 * will use the encoding specifried in the init parameter, or if that is not present,
 * fall back to a default value of UTF-8.
 */
public class SetRequestCharacterEncoding implements Filter {

    public static final String DEFAULT_ENCODING = "UTF-8";

    private FilterConfig filterConfig = null;
    private String encoding = null;

    /**
     * Set the character encoding in the request.
     *
     * @param request     the current ServletRequest object
     * @param response    the current ServletResponse object
     * @param filterChain the current FilterChain
     * @throws IOException      if any io error occurs
     * @throws ServletException any other servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        request.setCharacterEncoding(getEncoding());
        filterChain.doFilter(request, response);
    }

    /**
     * Initialize the filter.
     *
     * @param filterConfig the current filter configuration
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        setEncoding(filterConfig.getInitParameter("encoding"));
    }

    /**
     * Returns the encoding of the request.
     */
    public String getEncoding() {
        return (encoding == null ? DEFAULT_ENCODING : encoding);
    }

    /**
     * Sets the encoding of the request.
     */
    public void setEncoding(String value) {
        if (value != null) {
            encoding = value;
        }
    }

    /**
     * Reset the filter settings.
     */
    public void destroy() {
        encoding = null;
        filterConfig = null;
    }

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }
}

