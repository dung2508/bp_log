package vn.edu.clevai.bplog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@AllArgsConstructor
@Audited
@Data
@EntityListeners(value = AuditingEntityListener.class)
@MappedSuperclass
@NoArgsConstructor
@SuperBuilder
public class Auditable<T> {
	@CreatedBy
	@Column(name = "created_by")
	private T createdBy;

	@LastModifiedBy
	@Column(name = "updated_by")
	private T updatedBy;

	@CreatedDate
	@CreationTimestamp
	@Column(name = "created_at")
	private Timestamp createdAt;

	@LastModifiedDate
	@UpdateTimestamp
	@Column(name = "updated_at", updatable = false)
	private Timestamp updatedAt;
}
