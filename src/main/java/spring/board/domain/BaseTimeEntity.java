package spring.board.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseTimeEntity {
	
	//정보를 처음 등록할 때 등록한 시간을 생성해서 DB에 저장함.
	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdDate;
	
	//정보를 수정할 때 마지막 수정시간을 생성해서 DB에 저장함.
	@LastModifiedDate
	@Column(updatable = true)
	private LocalDateTime lastModifiedDate;

}
