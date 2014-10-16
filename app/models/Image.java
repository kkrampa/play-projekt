package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import play.db.ebean.Model;


@Entity
public class Image extends Model {

    public static final Finder<Long, Image> FINDER = new Finder<Long, Image>(Long.class, Image.class);

    @Id
    private Long id;

    @Lob
    private byte[] content;
    private String contentType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}