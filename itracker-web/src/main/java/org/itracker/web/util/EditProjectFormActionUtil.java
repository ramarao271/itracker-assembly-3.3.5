package org.itracker.web.util;

import org.apache.log4j.Logger;
import org.itracker.model.util.ProjectUtilities;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class EditProjectFormActionUtil {
    private static final Logger log = Logger.getLogger(EditProjectFormActionUtil.class);

    public static class CustomFieldInfo {
        private int id;
        private String name;
        private String type;

        public CustomFieldInfo(Integer id, String customFieldName, String string) {
            this.id = id;
            this.name = customFieldName;
            this.type = string;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class VersionInfo {
        private int id;
        private String number;
        private String description;
        private Date lastModifiedDate;
        private Long countIssuesByVersion;

        public VersionInfo(int id, String number, String description,
                           Date lastModifiedDate, Long countIssuesByVersion) {
            this.id = id;
            this.number = number;
            this.description = description;
            this.lastModifiedDate = lastModifiedDate;
            this.countIssuesByVersion = countIssuesByVersion;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Date getDate() {
            return lastModifiedDate;
        }

        public void setDate(Date lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
        }

        public Long getCount() {
            return countIssuesByVersion;
        }

        public void setCount(Long countIssuesByVersion) {
            this.countIssuesByVersion = countIssuesByVersion;
        }

    }

    public static class ComponentInfo {
        private int id;
        private String name;
        private String description;
        private Date lastModifiedDate;
        private Long countIssuesByComponent;

        public ComponentInfo(Integer id, String name, String description,
                             Date lastModifiedDate, Long countIssuesByComponent) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.lastModifiedDate = lastModifiedDate;
            this.countIssuesByComponent = countIssuesByComponent;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Date getDate() {
            return lastModifiedDate;
        }

        public void setDate(Date date) {
            this.lastModifiedDate = date;
        }

        public Long getCount() {
            return countIssuesByComponent;
        }

        public void setCount(Long countIssuesByComponent) {
            this.countIssuesByComponent = countIssuesByComponent;
        }

    }

    public static final void setUpPrioritiesInEnv(HttpServletRequest request) {

        String prioritySizeStr = ProjectUtilities.getScriptPrioritySize();
        int prioritySize = Integer.parseInt(prioritySizeStr);
        Map<Integer, String> priorityList = new TreeMap<Integer, String>();
        for (int j = 1; j <= prioritySize; j++) {
            priorityList.put(j, ProjectUtilities.getScriptPriorityLabelKey(j));
        }

        request.setAttribute("priorityList", priorityList);
    }
}
