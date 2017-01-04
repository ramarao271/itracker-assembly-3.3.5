package org.itracker.web.servlets;

import com.sun.syndication.feed.module.DCModuleImpl;
import com.sun.syndication.feed.module.SyModuleImpl;
import com.sun.syndication.feed.rss.*;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.config.ForwardConfig;
import org.apache.struts.config.ModuleConfig;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.util.ModuleUtils;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.Issue;
import org.itracker.model.IssueHistory;
import org.itracker.model.PermissionType;
import org.itracker.model.Project;
import org.itracker.model.util.IssueUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.services.IssueService;
import org.itracker.services.ProjectService;
import org.itracker.web.util.Constants;
import org.itracker.web.util.HTMLUtilities;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RssFeedController extends GenericController {

    final private static Pattern URL_PATTERN = Pattern.compile("(?i).*/issues/p?([0-9]*)/?i?([0-9]*)");
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {


        RssChannelHttpMessageConverter conv = new RssChannelHttpMessageConverter();
        try (ServletServerHttpResponse sr = new ServletServerHttpResponse(resp)) {
            Matcher uriMatcher = URL_PATTERN.matcher(req.getRequestURI());

            Integer projectId = null;
            Integer issueId = null;
            try {
                if (uriMatcher.matches()) {
                    projectId = uriMatcher.group(1).equals("") ? null : Integer.valueOf(uriMatcher.group(1));
                    issueId = uriMatcher.group(2).equals("") ? null : Integer.valueOf(uriMatcher.group(2));

                }

            } catch (RuntimeException e) {
                deny(sr);
                return;
            }

            final IssueService is = ServletContextUtils.getItrackerServices().getIssueService();
            final ProjectService ps = ServletContextUtils.getItrackerServices().getProjectService();
            final ConfigurationService cs = ServletContextUtils.getItrackerServices().getConfigurationService();

            final String baseURL = cs.getSystemBaseURL();
            final String generator = cs.getProperty("notification_from_text",
                    "iTracker "
                            + cs.getProperty(Constants.VERSION_KEY, "3"));


            Channel c = new Channel(conv.getSupportedMediaTypes().get(0).getType());


            c.setGenerator(generator);


            c.setModules(Arrays.asList(
                    new DCModuleImpl(),
                    new SyModuleImpl()
            ));


            c.setFeedType("rss_2.0");
            c.setLink(cs.getSystemBaseURL() + req.getServletPath());

            if (null != issueId) {
                Issue i = is.getIssue(issueId);
                if (!LoginUtilities.canViewIssue(i)) {
                    deny(sr);
                    return;
                }

                toChannel(c, i, baseURL);
            } else if (null != projectId) {
                Project p = ps.getProject(projectId);
                if (null == p) {
                    deny(sr);
                    return;
                }
                if (!LoginUtilities.hasPermission(p, PermissionType.ISSUE_VIEW_USERS)) {
                    deny(sr);
                    return;
                }

                toChannel(c, p, is, req, baseURL);

            } else {

                toChannel(c, ps, req, baseURL);
            }

            conv.write(c, conv.getSupportedMediaTypes().get(0), sr);
        }



    }

    private void toChannel(Channel c, ProjectService ps, HttpServletRequest req, String baseURL) {
        List<Project> projects = ps.getAllAvailableProjects();
        c.setTitle(ITrackerResources.getString("itracker.feed.projects.title"));
        c.setDescription(ITrackerResources.getString("itracker.feed.projects.description", baseURL));
        c.setLink(baseURL);
        c.setUri(baseURL);
        for (Project p : projects) {
            if (LoginUtilities.hasAnyPermission(p, null)) {
                Item pi = new Item();
                pi.setLink(getProjectURL(p, req, getServletContext(), baseURL));

                pi.setTitle(p.getName());
                Guid guid = new Guid();
                guid.setValue(pi.getLink());
                guid.setPermaLink(true);

                pi.setGuid(guid);

                Description desc = new Description();
                desc.setType(Content.TEXT);
                desc.setValue(p.getDescription());
                pi.setDescription(desc);

                Content content = new Content();
                content.setType(Content.HTML);

                content.setValue(
                        p.getDescription()
                );
                pi.setPubDate(ps.getLatestIssueUpdatedDateByProjectId(p.getId()));

                c.getItems().add(pi);
            }
        }
    }

    private void toChannel(Channel channel, Project project, IssueService is, HttpServletRequest req, String baseURL) throws MalformedURLException {
        List<Issue> listIssues;
        if (!LoginUtilities.hasPermission(project, PermissionType.ISSUE_VIEW_ALL)) {
            listIssues = new ArrayList<>();
            for (Issue issue : is.getIssuesByProjectId(project.getId())) {
                if (LoginUtilities.canViewIssue(issue)) {
                    listIssues.add(issue);
                }
            }
        } else {
            listIssues = is.getIssuesByProjectId(project.getId());
        }

        channel.setDescription(ITrackerResources.getString("itracker.feed.project.description", project.getName()));
        channel.setTitle(ITrackerResources.getString("itracker.feed.project.title ", project.getName(), project.getDescription()));
        channel.setLink(getProjectURL(project, req, getServletContext(), baseURL));

        is.getIssuesByProjectId(project.getId());
        List<Item> sItems = projectIssueItems(req, baseURL, listIssues);

        channel.setItems(sItems);
    }

    private void toChannel(Channel channel, Issue issue, String baseURL) throws MalformedURLException {
        channel.setDescription( ITrackerResources.getString("itracker.feed.issue.description.description", String.valueOf(issue.getId())));
        channel.setTitle(ITrackerResources.getString("itracker.feed.issue.title",
                String.valueOf(issue.getId()), issue.getDescription()));
        channel.setLink(getIssueURL(issue, baseURL));

        channel.setTitle(issue.getId() + " - " + issue.getDescription());
        List<Item> sItems = issueHistoryItems(baseURL, issue);

        channel.setItems(sItems);
    }


    private void deny(ServletServerHttpResponse sr) {
        sr.getHeaders().clear();
        sr.setStatusCode(HttpStatus.FORBIDDEN);
    }

    private List<Item> projectIssueItems(HttpServletRequest req, String baseURL, List<Issue> listIssues) throws MalformedURLException {

        List<Item> sItems = new ArrayList<>(listIssues.size());

        final Iterator<Issue> issuesIt = listIssues.iterator();
// start copying from Models to PTOs
        Issue issue;
        Item current;

        while (issuesIt.hasNext()) {

            issue = issuesIt.next();
            current = new Item();
            current.setUri(baseURL + req.getServletPath());
            current.setLink(getIssueURL(issue, baseURL));

            Guid guid = new Guid();
            guid.setValue(current.getLink());
            guid.setPermaLink(true);

            current.setGuid(guid);

            Description desc = new Description();
            desc.setType(Content.TEXT);
            desc.setValue("Issue " + issue.getId());
            current.setDescription(desc);

            current.setTitle(issue.getDescription());
            current.setPubDate(issue.getLastModifiedDate());
            Content c = new Content();
            c.setType(Content.HTML);

            c.setValue(
                    issue.getHistory().get(issue.getHistory().size() - 1).getDescription()
            );
            current.setContent(c);

            sItems.add(current);
        }
        return sItems;
    }

    private List<Item> issueHistoryItems(String baseURL, Issue i) throws MalformedURLException {
        List<Item> sItems = new ArrayList<>(i.getHistory().size());
        Item current;

        StringBuilder author = new StringBuilder();
        List<IssueHistory> history = new LinkedList<>(i.getHistory());
        Collections.reverse(history);
        for (IssueHistory ih : history) {
            int index = ( history.indexOf(ih)+1 );
            if (index > 10) {
                // limited to newest 10 issues
                break;
            }

            current = new Item();
            Content content = new Content();

            current.setLink(getIssueURL(i, baseURL));

            Guid guid = new Guid();
            guid.setValue(current.getLink() + "#h" + index);
            guid.setPermaLink(true);
            current.setGuid(guid);

            current.setTitle(ITrackerResources.getString("itracker.feed.history.title",
                    String.valueOf(history.size() + 1 - index)));

            Description desc = new Description();
            desc.setType(Content.TEXT);
            desc.setValue(HTMLUtilities.removeMarkup( ih.getDescription() ));
            current.setDescription(desc);

            content.setType(Content.HTML);

            content.setValue(
                    "<p>" + HTMLUtilities.removeMarkup( ih.getDescription() ) + "</p>"
            );
            current.setContent(content);
            current.setPubDate(ih.getCreateDate());

            author.setLength(0);
            InternetAddress authorAddress = ih.getUser().getEmailAddress();
            if (StringUtils.isNotBlank(authorAddress.getAddress())) {
                author.append(authorAddress.getAddress());
                if (StringUtils.isNotBlank(authorAddress.getPersonal())) {
                    author.append(' ')
                            .append('(')
                            .append(authorAddress.getPersonal())
                            .append(')');
                }
                current.setAuthor(String.valueOf(author));
            }


            sItems.add(current);
        }
        return sItems;
    }


    private String getIssueURL(Issue i, String baseUrl) throws MalformedURLException {
        return String.valueOf(IssueUtilities.getIssueURL(i, baseUrl));

    }

    private String getProjectURL(Project p, HttpServletRequest req, ServletContext context, String baseUrl) {
        ModuleConfig conf = ModuleUtils.getInstance().getModuleConfig(
                "/module-projects",
                req,
                context);

        ForwardConfig forwardConfig = conf.findForwardConfig("listissues");
        return baseUrl + TagUtils.getInstance().pageURL(req, forwardConfig.getPath(), conf) + "?projectId=" + p.getId();
    }
}
