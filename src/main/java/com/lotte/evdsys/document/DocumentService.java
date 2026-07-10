package com.lotte.evdsys.document;

import com.lotte.evdsys.common.DuplicateResourceException;
import com.lotte.evdsys.common.BadRequestException;
import com.lotte.evdsys.common.ForbiddenOperationException;
import com.lotte.evdsys.common.ResourceNotFoundException;
import com.lotte.evdsys.document.dto.CreateDocumentRequest;
import com.lotte.evdsys.document.dto.DocumentResponse;
import com.lotte.evdsys.document.dto.DocumentPageResponse;
import com.lotte.evdsys.document.dto.UpdateDocumentRequest;
import com.lotte.evdsys.security.CurrentUserService;
import com.lotte.evdsys.user.Role;
import com.lotte.evdsys.user.User;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class DocumentService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "code", "title", "category", "status", "createdAt", "updatedAt"
    );

    private final DocumentRepository documentRepository;
    private final CurrentUserService currentUserService;
    private final FileStorageService fileStorageService;

    public DocumentService(DocumentRepository documentRepository, CurrentUserService currentUserService,
                           FileStorageService fileStorageService) {
        this.documentRepository = documentRepository;
        this.currentUserService = currentUserService;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public DocumentResponse create(CreateDocumentRequest request) {
        if (documentRepository.existsByCode(request.code())) {
            throw new DuplicateResourceException("Document code already exists");
        }
        User currentUser = currentUserService.getCurrentUser();
        Document document = new Document(request.code(), request.title(), request.description(), request.category(),
                request.status(), currentUser);
        return toResponse(documentRepository.save(document));
    }

    public DocumentResponse findById(Long id) {
        Document document = findEntityById(id);
        ensureCanViewOrEdit(document, currentUserService.getCurrentUser());
        return toResponse(document);
    }

    public DocumentPageResponse findAll(int page, int size, DocumentStatus status, String category,
                                        String keyword, String sort) {
        if (page < 0) {
            throw new BadRequestException("Page must be greater than or equal to 0");
        }
        if (size < 1 || size > 100) {
            throw new BadRequestException("Size must be between 1 and 100");
        }

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        User currentUser = currentUserService.getCurrentUser();
        Specification<Document> specification = buildSpecification(status, category, keyword, currentUser);
        Page<DocumentResponse> result = documentRepository.findAll(specification, pageable).map(this::toResponse);

        return new DocumentPageResponse(result.getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages(), sort);
    }

    @Transactional
    public DocumentResponse update(Long id, UpdateDocumentRequest request) {
        Document document = findEntityById(id);
        ensureCanViewOrEdit(document, currentUserService.getCurrentUser());
        document.update(request.title(), request.description(), request.category(), request.status());
        return toResponse(document);
    }

    @Transactional
    public DocumentResponse uploadFile(Long id, MultipartFile file) {
        Document document = findEntityById(id);
        ensureCanViewOrEdit(document, currentUserService.getCurrentUser());
        document.changeFileName(fileStorageService.store(file));
        return toResponse(document);
    }

    @Transactional
    public void delete(Long id) {
        User currentUser = currentUserService.getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new ForbiddenOperationException("Only ADMIN can delete documents");
        }
        documentRepository.delete(findEntityById(id));
    }

    private Document findEntityById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + id));
    }

    private Specification<Document> buildSpecification(DocumentStatus status, String category, String keyword,
                                                       User currentUser) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (category != null && !category.isBlank()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("category")),
                        category.trim().toLowerCase(Locale.ROOT)));
            }
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), pattern)
                ));
            }
            if (currentUser.getRole() == Role.STAFF) {
                predicates.add(criteriaBuilder.equal(root.get("createdBy").get("id"), currentUser.getId()));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",", -1);
        String property = parts[0].trim();
        if (!ALLOWED_SORT_FIELDS.contains(property)) {
            throw new BadRequestException("Unsupported sort field: " + property);
        }
        Sort.Direction direction = parts.length == 2 && "asc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        if (parts.length > 2 || (parts.length == 2 && !"asc".equalsIgnoreCase(parts[1].trim())
                && !"desc".equalsIgnoreCase(parts[1].trim()))) {
            throw new BadRequestException("Sort must use field,asc or field,desc");
        }
        return Sort.by(direction, property);
    }

    private void ensureCanViewOrEdit(Document document, User currentUser) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = document.getCreatedBy().getId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new ForbiddenOperationException("You can access only documents you created");
        }
    }

    private DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
                document.getId(), document.getCode(), document.getTitle(), document.getDescription(),
                document.getCategory(), document.getStatus(), document.getCreatedBy().getUsername(),
                document.getCreatedAt(), document.getUpdatedAt(), document.getFileName()
        );
    }
}
