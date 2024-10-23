package com.jxx.approval.confirm.domain.connect;

import com.jxx.approval.confirm.domain.document.DocumentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.http.HttpMethod;
import org.springframework.util.NumberUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 결재 문서에 따라 외부 API 호출이 필요한 정보를 모와놓는 엔티티
 **/
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_REST_API_CONNECTION",
        indexes = @Index(columnList = "DOCUMENT_TYPE, TRIGGER_TYPE", unique = true))
public class RestApiConnection {

    private static final List<String> ALLOW_SCHEMES = List.of("http", "https");
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONNECTION_PK", columnDefinition = "BIGINT")
    private Long connectionPk;
    @Comment("URL scheme")
    @Column(name = "SCHEME", columnDefinition = "VARCHAR(8)")
    private String scheme;
    @Comment("URL host or ipv4")
    @Column(name = "HOST", columnDefinition = "VARCHAR(16)")
    private String host;
    @Comment("URL port")
    @Column(name = "PORT", columnDefinition = "INT")
    private int port;
    @Comment("URL path")
    @Column(name = "PATH", columnDefinition = "VARCHAR(255)")
    private String path;
    @Comment("REST API 메서드 유형")
    @Column(name = "METHOD_TYPE", columnDefinition = "VARCHAR(8)")
    private String methodType;
    @Comment("결재 문서 유형")
    @Column(name = "DOCUMENT_TYPE", columnDefinition = "VARCHAR(8)")
    @Enumerated(value = EnumType.STRING)
    private DocumentType documentType;
    @Comment("트리거 타입(어떤 이벤트가 발생할 때 호출해야 하는지를 표현)")
    @Column(name = "TRIGGER_TYPE", columnDefinition = "VARCHAR(16)")
    private String triggerType;
    @Comment("REST API에 대한 설명")
    @Column(name = "DESCRIPTION", columnDefinition = "VARCHAR(255)")
    private String description;
    @Comment("생성일시")
    @Column(name = "CREATE_DATE_TIME", columnDefinition = "DATETIME")
    private LocalDateTime createDateTime;
    @Comment("생성을 요청한 사용자의 ID")
    @Column(name = "REQUESTER_ID", columnDefinition = "VARCHAR(16)")
    private String requesterId;
    @Comment("연동 API 사용여부(false=0/true=1)")
    @Column(name = "USED", columnDefinition = "TINYINT(1) DEFAULT 1")
    private boolean used;
    @OneToMany(mappedBy = "restApiConnection", fetch = FetchType.LAZY)
    private List<ConnectionElement> connectionElements = new ArrayList<>();

    @Builder
    public RestApiConnection(String scheme, String host, int port, String path, String methodType, DocumentType documentType,
                             String triggerType, String description, LocalDateTime creteDateTime, String requesterId, boolean used,
                             List<ConnectionElement> connectionElements) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.path = path;
        this.methodType = methodType;
        this.documentType = documentType;
        this.triggerType = triggerType;
        this.description = description;
        this.createDateTime = creteDateTime;
        this.requesterId = requesterId;
        this.used = used;
        this.connectionElements = connectionElements;
    }

    /**
     * REST API PATH 가 유효한 형식인지 검증
     * ^/.* = 문자열은 슬래시(/)로 시작하고 뒤에는 0 개 이상의 문자열이 올 수 있다.
     **/
    public static boolean validatePath(String path) {
        return Pattern.matches("^/.*", path);
    }

    public static boolean validateMethodType(String methodType) {
        List<HttpMethod> httpMethods = Arrays.asList(HttpMethod.values());
        return httpMethods.contains(HttpMethod.valueOf(methodType));
    }

    public static boolean validatePort(Integer port) {
        return port >= 0 && port <= 65535;
    }

    public static boolean validateScheme(String scheme) {
        return ALLOW_SCHEMES.contains(scheme.toLowerCase());
    }
}


