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
@Table(name = "JXX_CONNECTION_ELEMENT")
public class ConnectionElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONNECTION_ELEMENT_PK")
    @Comment("REST API 파라미터 PK")
    private Long connectionParameterPk;
    @Enumerated(value = EnumType.STRING)
    @Comment("파라미터 유형(Request Body/Path Variable/Query String)")
    @Column(name = "ELEMENT_TYPE", nullable = false)
    private ElementType elementType;
    @Column(name = "ELEMENT_KEY", nullable = false)
    @Comment("요소의 키")
    private String elementKey;
    @Column(name = "ELEMENT_VALUE")
    @Comment("요소의 값")
    private String elementValue;
    @Column(name = "ELEMENT_VALUE_TYPE")
    @Comment("요소 값 유형")
    @Enumerated(value = EnumType.STRING)
    private ElementValueType elementValueType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONNECTION_PK", referencedColumnName = "CONNECTION_PK",
            foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private RestApiConnection restApiConnection;

    @Builder
    public ConnectionElement(ElementType elementType, String elementKey, String elementValue, ElementValueType elementValueType) {
        this.elementType = elementType;
        this.elementKey = elementKey;
        this.elementValue = elementValue;
        this.elementValueType = elementValueType;
    }

    public boolean isRequestBodyType() {
        return Objects.equals(this.elementType, ElementType.REQUEST_BODY);
    }

    public boolean isQueryStringType() {
        return Objects.equals(this.elementType, ElementType.QUERY_STRING);
    }

    public boolean isPathVariableType() {
        return Objects.equals(this.elementType, ElementType.PATH_VARIABLE);
    }
}