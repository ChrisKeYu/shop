package top.chris.shop.pojo;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "`pdman_db_version`")
public class PdmanDbVersion implements Serializable {
    @Column(name = "`DB_VERSION`")
    private String dbVersion;

    @Column(name = "`VERSION_DESC`")
    private String versionDesc;

    @Column(name = "`CREATED_TIME`")
    private String createdTime;

    private static final long serialVersionUID = 1L;

    /**
     * @return DB_VERSION
     */
    public String getDbVersion() {
        return dbVersion;
    }

    /**
     * @param dbVersion
     */
    public void setDbVersion(String dbVersion) {
        this.dbVersion = dbVersion;
    }

    /**
     * @return VERSION_DESC
     */
    public String getVersionDesc() {
        return versionDesc;
    }

    /**
     * @param versionDesc
     */
    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    /**
     * @return CREATED_TIME
     */
    public String getCreatedTime() {
        return createdTime;
    }

    /**
     * @param createdTime
     */
    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}