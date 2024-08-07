package org.iperp.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.iperp.Enums.JobType;
import org.iperp.Enums.JobLocation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_location", nullable = false)
    private JobLocation jobLocation;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private AppUser createdBy;

    @CreationTimestamp
    private LocalDateTime createdOn;

    @Builder.Default
    @Column(name = "accepting_applications", nullable = false)
    private boolean acceptingApplications = true;

    @Builder.Default
    @Column(name = "archived", nullable = false)
    private boolean archived = false;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PostSkill> skills = new ArrayList<>();
}
