package com.jxx.approval.confirm.domain.connect;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "JXX_CONFIRM_DOCUMENT_CONNECTION_PARAMETER")
public class ConnectionParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONNECTION_PARAMETER_PK")
    @Comment("REST API 파라미터 PK")
    private Long connectionParameterPk;
    @Column(name = "PARAMETER_TYPE", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @Comment("파라미터 유형(Request Body/Path Variable/Query String)")
    private ParameterType parameterType;
    @Column(name = "PARAMETER_KEY", nullable = false)
    @Comment("파라미터 키")
    private String parameterKey;
    @Column(name = "PARAMETER_VALUE")
    @Comment("파라미터 값")
    private String parameterValue;
    @Column(name = "PATH_VARIABLE_ORDER")
    @Comment("Path Variable 의 경우, URL 경로 몇 번째 Path Variable 에 들어갈지 순서 값이 존재해야 함")
    private int pathVariableOrder;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONNECTION_PK", referencedColumnName = "CONNECTION_PK",
            foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private ConfirmDocumentConnection confirmDocumentConnection;

    @Builder
    public ConnectionParameter(ParameterType parameterType, String parameterKey, String parameterValue, int pathVariableOrder,
                               ConfirmDocumentConnection confirmDocumentConnection) {
        this.parameterType = parameterType;
        this.parameterKey = parameterKey;
        this.parameterValue = parameterValue;
        this.pathVariableOrder = pathVariableOrder;
        this.confirmDocumentConnection = confirmDocumentConnection;
    }

    public boolean isNotRequestBodyType() {
        return !Objects.equals(this.parameterType, ParameterType.REQUEST_BODY);
    }

    public boolean isNotQueryStringType() {
        return !Objects.equals(this.parameterType, ParameterType.QUERY_STRING);
    }
}
