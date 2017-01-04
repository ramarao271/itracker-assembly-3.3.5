<%@ include file="/common/taglibs.jsp" %>
<%@ page pageEncoding="UTF-8" %>

<ul>
    <li><a href="#create">Creating an Issue</a></li>
    <li><a href="#edit">Editing an Issue</a></li>
    <li><a href="#list">Listing Issues</a></li>
    <li><a href="#search">Searching for Issues</a></li>
    <li><a href="#report">Running Reports</a></li>
    <li><a href="#prefs">Editing Preferences</a></li>
</ul>

<hr width="75%" noshade height="1"/>
<a name="create"></a><span class="editColumnTitle">Creating an Issue</span>
<a href="#top" class="headerLinks">[top]</a><br/>

<p class="help">
    To create a new issue, first go to the project list screen by clicking "Project List"
    in the top menu. From this screen you can click on create new issue icon
    (<i class="fa fa-plus" aria-hidden="true"></i>) beside the project you want to
    create
    the issue for.<br/>
    <br/>
    You must have "create" permission in a project before you can create a new issue
    for that project.<br/>
</p>

<hr width="75%" noshade height="1"/>
<a name="edit"></a><span class="editColumnTitle">Editing an Issue</span>
<a href="#top" class="headerLinks">[top]</a><br/>

<p class="help">
    There are a couple ways to edit an existing issue.<br/>
    <br/>
    If the issue is visible on your myITracker page, you can click the edit
    (<i class="fa fa-edit" aria-hidden="true"></i>) icon beside the issue you wish to
    edit.<br/>
    <br/>
    If you are currently viewing the details of an issue, the edit icon
    (<i class="fa fa-edit" aria-hidden="true"></i>) will be displayed in actions area.<br/>
    <br/>
    To edit an existing issue, first go to the project list screen by clicking "Project List"
    in the top menu. From this screen you can click the view project issues icon
    (<i class="fa fa-tasks" aria-hidden="true"></i>) beside the project you want a list
    of issues for. Once you have a list of issues, you can select the edit icon
    (<i class="fa fa-edit" aria-hidden="true"></i>) beside the issue you wish to edit.<br/>
    <br/>
    You must have edit permissions for a project before you can edit an existing issue
    for a project.<br/>
</p>

<hr width="75%" noshade height="1"/>
<a name="list"></a><span class="editColumnTitle">Listing Issues</span>
<a href="#top" class="headerLinks">[top]</a><br/>

<p class="help">
    To list the issues for a project, first go to the project list screen by clicking
    "Project List" in the top menu. From this screen you can click the view project
    issues icon (<i class="fa fa-tasks" aria-hidden="true"></i>) beside the
    project you want a list of issues for. From this screen you can view the details of the
    issue, watch the issue if you aren't already being notified of changes, or edit the issue
    if you have permissions.<br/>
</p>


<hr width="75%" noshade height="1"/>
<a name="search"></a><span class="editColumnTitle">Searching for Issues</span>
<a href="#top" class="headerLinks">[top]</a><br/>

<p class="help">
    This lets you search for issues across projects based on their severity and status codes.
    From the search page you can select multiple values by holding the Ctrl key while you select
    the values from the list, or you can select a range by clicking the first value you want,
    and then holding down Shift while you select the second value. Once you have your search
    and sort criteria specified, just click Search to find all the matches.
    <br/>
    You can also perform a more detailed search within a particular project by clicking on the
    Project Issue search icon (<i class="fa fa-search" aria-hidden="true"></i>) beside
    the project on the Project List page.
    <br/>
    From the results section you can edit or view the issues if you have permission to do so,
    by clicking on the appropriate icon.
    <br/>
</p>

<hr width="75%" noshade height="1"/>
<a name="report"></a><span class="editColumnTitle">Running Reports</span>
<a href="#top" class="headerLinks">[top]</a><br/>

<p class="help">
    To run the reports, first select the checkboxes beside the projects you want included in
    the report. Then select the desired report from the pull-down menu and click the run report
    button. The report will then be processed and the results will be displayed in your current
    browser window. Depending on the number of issues in a project and the number of projects
    selected, a report may take several minutes to run.<br/>
</p>

<hr width="75%" noshade height="1"/>
<a name="prefs"></a><span class="editColumnTitle">Editing Preferences</span>
<a href="#top" class="headerLinks">[top]</a><br/>

<p class="help">
    To edit your preferences, click on the My Preferences Link in the top menu. This
    will take you to a screen that will allow you to change your personal information. You
    can also set some preferences that affect the display of the site.<br/>
    <br/>
    If you allow automatic login, a permanent cookie will be stored on your machine with your
    plaintext user ID and password. This may be a security issue so be sure you want this option
    before you select it.<br/>
    <br/>
    When selecting the number of issues to display on a page, you can display all the issues
    by putting in either 0 or a negative number. Any positive number will paginate the results
    into screens of the selected number of items.<br/>
</p>
