package lt.scoutress.StatisticsApp.CC;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "content_creator")
public class ContentCreator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "channel_url", nullable = false, unique = true)
    private String channelURL;

    @OneToMany(mappedBy = "contentCreator", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CCContent> ccContent = new ArrayList<>();

    public ContentCreator() {}

    public ContentCreator(String username, String channelURL, List<CCContent> ccContent) {
        this.username = username;
        this.channelURL = channelURL;
        this.ccContent = ccContent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChannelURL() {
        return channelURL;
    }

    public void setChannelURL(String channelURL) {
        this.channelURL = channelURL;
    }

    public List<CCContent> getCcContent() {
        return ccContent;
    }

    public void setCcContent(List<CCContent> ccContent) {
        this.ccContent = ccContent;
    }

    @Override
    public String toString() {
        return "ContentCreator [id=" + id + ", username=" + username + ", channelURL=" + channelURL + ", ccContent="
                + ccContent + "]";
    }
}
