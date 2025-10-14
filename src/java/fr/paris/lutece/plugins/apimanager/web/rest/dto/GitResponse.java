package fr.paris.lutece.plugins.apimanager.web.rest.dto;

public class GitResponse {
    public String file_name;
    public String file_path;
    public int size;
    public String encoding;
    public String content_sha256;
    public String ref;
    public String blob_id;
    public String commit_id;
    public String last_commit_id;
    public boolean execute_filemode;
    public String content;

    public GitResponse()
    {}

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getContent_sha256() {
        return content_sha256;
    }

    public void setContent_sha256(String content_sha256) {
        this.content_sha256 = content_sha256;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getBlob_id() {
        return blob_id;
    }

    public void setBlob_id(String blob_id) {
        this.blob_id = blob_id;
    }

    public String getCommit_id() {
        return commit_id;
    }

    public void setCommit_id(String commit_id) {
        this.commit_id = commit_id;
    }

    public String getLast_commit_id() {
        return last_commit_id;
    }

    public void setLast_commit_id(String last_commit_id) {
        this.last_commit_id = last_commit_id;
    }

    public boolean isExecute_filemode() {
        return execute_filemode;
    }

    public void setExecute_filemode(boolean execute_filemode) {
        this.execute_filemode = execute_filemode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
