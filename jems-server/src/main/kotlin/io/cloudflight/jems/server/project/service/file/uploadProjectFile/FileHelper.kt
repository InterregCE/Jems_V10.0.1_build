package io.cloudflight.jems.server.project.service.file.uploadProjectFile

import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import org.apache.commons.io.FilenameUtils

private val ALLOWED_EXTENSIONS = arrayOf("ddoc", "asice", "asics", "adoc", "bdoc", "edoc", "sce", "scs", "csv","dat","db","dbf","log","mdb","xml","email","eml","emlx","msg","oft","ost","pst","vcf","bmp","gif","jpeg","jpg","png","psd","svg","tif","tiff","htm","html","key","odp","pps","ppt","pptx","ods","xls","xlsm","xlsx","doc","docx","odt","pdf","rtf","tex","txt","wpd","mov","avi","mp4","zip","rar","ace","7z","url")

fun isFileTypeInvalid(file: ProjectFile): Boolean {
    return FilenameUtils.getExtension(file.name).lowercase() !in ALLOWED_EXTENSIONS
}
