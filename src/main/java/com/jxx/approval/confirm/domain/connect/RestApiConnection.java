package com.jxx.approval.confirm.domain.connect;

import com.jxx.approval.confirm.domain.document.DocumentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

/** 결재 문서에 따라 외부 API 호출이 필요한 정보를 모와놓는 엔티티 **/
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_REST_API_CONNECTION",
        indexes = @Index(columnList = "DOCUMENT_TYPE, TRIGGER_TYPE", unique = true))
public class RestApiConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONNECTION_PK")
    private Long connectionPk;
    @Comment("URL scheme")
    @Column(name = "SCHEME")
    private String scheme;
    @Comment("URL host or ip")
    @Column(name = "HOST")
    private String host;
    @Comment("URL port")
    @Column(name = "PORT")
    private int port;
    @Comment("URL path")
    @Column(name = "PATH")
    private String path;
    @Comment("REST API 메서드 유형")
    @Column(name = "METHOD_TYPE")
    private String methodType;
    @Comment("결재 문서 유형")
    @Column(name = "DOCUMENT_TYPE")
    @Enumerated(value = EnumType.STRING)
    private DocumentType documentType;
    @Comment("트리거 타입(어떤 이벤트가 발생할 때 호출해야 하는지를 표현)")
    @Column(name = "TRIGGER_TYPE")
    private String triggerType;
    @Comment("REST API에 대한 설명")
    @Column(name = "DESCRIPTION")
    private String description;
    @OneToMany(mappedBy = "restApiConnection", fetch = FetchType.LAZY)
    private List<ConnectionElement> connectionElements = new ArrayList<>();

    @Builder
    public RestApiConnection(String scheme, String host, int port, String path, String methodType, DocumentType documentType,
                             String triggerType, String description, List<ConnectionElement> connectionElements) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.path = path;
        this.methodType = methodType;
        this.documentType = documentType;
        this.triggerType = triggerType;
        this.description = description;
        this.connectionElements = connectionElements;
    }
}


