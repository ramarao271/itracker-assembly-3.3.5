<%@ include file="/common/taglibs.jsp" %>

<footer>
    <div class="container-fluid">
        <div class="row">
            <div class="col-sm-6 text-left">
                <it:message key="itracker.web.footer.powered" /> <a href="http://itracker.sourceforge.net" target="_blank">itracker </a> <c:out  value="${currentVersion}" />,
                 <it:message key="itracker.web.footer.licensing" /> <a href="http://www.gnu.org/licenses/lgpl.html">LGPL</a></td>
            </div>
            <div class="col-sm-6 text-right">


                <c:set var="gendate"><it:formatDate date="${currentDate}"/></c:set>
                <it:message key="itracker.web.footer.gendate" arg0="${gendate}"/>
            </div>
        </div>
    </div>
</footer>

