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

package org.itracker.model.util;

import java.util.HashSet;

@Deprecated
public class IssueAttachmentUtilities {
    public static final String DEFAULT_ATTACHMENT_DIR = "./itracker/attachments";

    public IssueAttachmentUtilities() {
    }

}

@Deprecated
class MimeType {
    private String mimeType;
    private HashSet<?> suffixes;
    private String imageName;

    public MimeType(String mimeType, String imageName) {
        setMimeType(mimeType);
        setImageName(imageName);
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String value) {
        mimeType = value;
    }

    public String imageName() {
        return imageName;
    }

    public void setImageName(String value) {
        imageName = value;
    }

    public HashSet<?> getSuffixes() {
        return suffixes;
    }

    public void setSuffixes(HashSet<?> suffixes) {
        this.suffixes = suffixes;
    }
}

/*
 image/gif                       gif
 image/ief                       ief
 image/jpeg                      jpeg jpg jpe
 image/pjpeg                     jpg
 image/png                       png
 image/x-png                     png
 image/tiff                      tiff tif
 image/x-cmu-raster              ras
 image/x-portable-anymap         pnm
 image/x-portable-bitmap         pbm
 image/x-portable-graymap        pgm
 image/x-portable-pixmap         ppm
 image/x-rgb                     rgb
 image/x-xbitmap                 xbm
 image/x-xpixmap                 xpm
 image/x-xwindowdump             xwd
 application/x-compress          z Z
 application/x-gtar              gtar tgz
 application/x-gunzip            gz
 application/x-gzip              gz
 application/x-gzip-compressed   gz
 application/x-tar               tar
 application/zip                 zip
 application/x-zip-compressed    zip
 text/plain                      asc txt c cc h hh cpp hpp
 application/excel               xls
 application/msword              doc dot wrd
 application/pdf                 pdf
 application/postscript          ai eps ps
 application/powerpoint          ppt
 application/vnd.rn-realmedia    rm
 audio/x-ms-wma                  wma
 application/x-shockwave-flash   swf
*/